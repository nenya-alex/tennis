package ua.tennis.repository;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import ua.tennis.domain.Odds;
import ua.tennis.domain.enumeration.MatchStatus;
import ua.tennis.service.CalculatorService;
import ua.tennis.service.MatchCache;
import ua.tennis.service.dto.*;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ScheduledRepository {

    private static final Integer INTEGER_ZERO = Integer.valueOf("0");
    private final MatchCache matchCache;

    private final CalculatorService calculatorService;

    private final OddsRepository oddsRepository;

    public ScheduledRepository(MatchCache matchCache,
                               CalculatorService calculatorService,
                               OddsRepository oddsRepository) {
        this.matchCache = matchCache;
        this.calculatorService = calculatorService;
        this.oddsRepository = oddsRepository;
    }

    public void fillResultByMatches(Map rawMatches,
                                    boolean isLiveMatches,
                                    Map<MatchStatus, List<MatchDTO>> result) {
        List<GroupDTO> groups = getGroups(rawMatches, isLiveMatches);
        List<Map> matches = getMatches(rawMatches, isLiveMatches);
        for (Map match : matches) {
            MatchDTO matchDTO = getMatchDTO(match);
            List<OddsDTO> odds = getOdds(groups, (Map) match.get("markets"), matchDTO.getId());
            matchDTO.setOdds(odds);
            if (isLiveMatches) {
                Map scoreboardSlim = (Map) match.get("scoreboardSlim");
                String period = (String) scoreboardSlim.get("period");
                matchDTO.setPeriod(period);
                if (MatchStatus.NOT_STARTED.getName().equals(period)) {
                    workWithMatch(matchDTO.status(MatchStatus.NOT_STARTED), result.get(MatchStatus.NOT_STARTED));
                } else if (period.endsWith("Set")) {
                    workWithLiveMatch(matchDTO.status(MatchStatus.LIVE), scoreboardSlim, period, result);
                } else if (MatchStatus.SUSPENDED.getName().equals(period)) {
                    workWithMatch(matchDTO.status(MatchStatus.SUSPENDED), result.get(MatchStatus.SUSPENDED));
                } else if (MatchStatus.FINISHED.getName().equals(period)) {
                    workWithFinishedMatch(matchDTO.status(MatchStatus.FINISHED), scoreboardSlim, result);
                }
            } else {
                workWithMatch(matchDTO.status(MatchStatus.UPCOMING), result.get(MatchStatus.UPCOMING));
            }
        }
    }

    private void workWithFinishedMatch(MatchDTO matchDTO, Map scoreboardSlim, Map<MatchStatus, List<MatchDTO>> result) {
        List<SettDTO> settDTOs = getSetts((List<List<String>>) scoreboardSlim.get("sets"), matchDTO.getId());
        matchDTO.setSetts(settDTOs);
        fillMatchByScores(matchDTO);
        result.get(MatchStatus.FINISHED).add(matchDTO);
    }

    private void workWithMatch(MatchDTO matchDTO, List<MatchDTO> matchDTOs) {
        matchDTOs.add(matchDTO);
    }

    private void fillCachedMatchByProbabilities(MatchDTO matchDTO, Map scoreboardSlim) {
        String gameMode = (String) scoreboardSlim.get("gameMode");
        Integer numberOfSetsToWin = (Integer.valueOf(gameMode.substring(gameMode.length() - 1)) - 3) / 2 + 2;

        Odds existedOdds = oddsRepository.findTopByMatchIdOrderByCheckDateDesc(matchDTO.getId());

        if (existedOdds != null) {
            double homeMatchProbability = calculatorService.getRoundedDoubleNumber(
                existedOdds.getAwayOdds() / (existedOdds.getHomeOdds() + existedOdds.getAwayOdds()));

            matchCache.addToCache(matchDTO.getId(),
                new MatchDTO(homeMatchProbability, calculatorService.getGameProbabilities(homeMatchProbability, numberOfSetsToWin)));

            matchDTO.setGameMode(gameMode);
            matchDTO.setNumberOfSetsToWin(numberOfSetsToWin);
        }
    }

    private void workWithLiveMatch(MatchDTO matchDTO,
                                   Map scoreboardSlim,
                                   String period,
                                   Map<MatchStatus, List<MatchDTO>> result) {

        fillCachedMatchByProbabilities(matchDTO, scoreboardSlim);

        List<SettDTO> settDTOs = getSetts((List<List<String>>) scoreboardSlim.get("sets"), matchDTO.getId());
        matchDTO.setSetts(settDTOs);

        int currentSetNumber = StringUtils.isNumeric(period.substring(0, 1)) ? Integer.valueOf(period.substring(0, 1)) : 0;
        matchDTO.setCurrentSetNumber(currentSetNumber);
        fillMatchByScores(matchDTO);

        Integer setHomeScore = settDTOs.get(currentSetNumber - 1).getHomeScore();
        Integer setAwayScore = settDTOs.get(currentSetNumber - 1).getAwayScore();

        if (!setHomeScore.equals(INTEGER_ZERO) || setAwayScore.equals(INTEGER_ZERO)) {

            if (isZeroPoints((List<String>) scoreboardSlim.get("points"))) {

                Optional<MatchDTO> cachedMatchDTO = matchCache.getCachedMatch(matchDTO.getId());

                if (cachedMatchDTO.isPresent()) {
                    prepareMatchDtoForPlacingBet(matchDTO, cachedMatchDTO.get().getSetts(), setHomeScore, setAwayScore);
                }
            }
        }
        result.get(MatchStatus.LIVE).add(matchDTO);
    }

    private boolean isZeroPoints(List<String> points) {
        return "0".equals(points.get(0)) && "0".equals(points.get(1));
    }

    private List<SettDTO> getSetts(List<List<String>> sets, Long matchId) {

        List<String> homeSets = sets.get(0);
        List<String> awaySets = sets.get(1);

        List<SettDTO> result = new ArrayList<>();
        for (int i = 0; i < homeSets.size(); i++) {
            Integer homeScore = StringUtils.isNumeric(homeSets.get(i)) ? Integer.valueOf(homeSets.get(i)) : 0;
            Integer awayScore = StringUtils.isNumeric(awaySets.get(i)) ? Integer.valueOf(awaySets.get(i)) : 0;

            result.add(new SettDTO(homeScore, awayScore, i + 1, matchId));
        }
        return result;
    }

    private void prepareMatchDtoForPlacingBet(MatchDTO matchDTO,
                                              List<SettDTO> cachedSets,
                                              Integer setHomeScore,
                                              Integer setAwayScore) {

        SettDTO cachedSettDTO = getCachedSettDTO(cachedSets, matchDTO.getHomeScore(), matchDTO.getAwayScore());
        GameDTO cachedGameDTO = getCachedGameDTO(cachedSettDTO.getGames(), setHomeScore, setAwayScore);

        SettDTO settDTO = matchDTO.getSetts().get(matchDTO.getCurrentSetNumber() - 1);
        settDTO.setHomeOdds(matchDTO.getOdds().get(0).getHomeOdds());
        settDTO.setAwayOdds(matchDTO.getOdds().get(0).getAwayOdds());
        //TODO remove next line?
        settDTO.setHomeProbability(cachedSettDTO.getHomeProbability());

        GameDTO clonedGameDTO = cloneGameDTO(cachedGameDTO);
        clonedGameDTO.setOddsDTO(matchDTO.getOdds().get(0));

        settDTO.getGames().add(clonedGameDTO);
    }

    private void fillMatchByScores(MatchDTO matchDTO) {
        Integer matchHomeScore = 0;
        Integer matchAwayScore = 0;

        List<SettDTO> setts = matchDTO.getSetts();

        for (SettDTO settDTO : setts) {
            Integer setHomeScore = settDTO.getHomeScore();
            Integer setAwayScore = settDTO.getAwayScore();
            if (isReadyToCountMatchScores(setHomeScore, setAwayScore)) {
                if (setHomeScore.compareTo(setAwayScore) > 0) {
                    matchHomeScore = matchHomeScore + 1;
                } else {
                    matchAwayScore = matchAwayScore + 1;
                }
            }
        }
        matchDTO.setHomeScore(matchHomeScore);
        matchDTO.setAwayScore(matchAwayScore);
    }

    private boolean isReadyToCountMatchScores(Integer setHomeScore, Integer setAwayScore) {
        return Math.max(setHomeScore, setAwayScore) >= 6 && Math.abs(setHomeScore - setAwayScore) >= 1;
    }

    private SettDTO getCachedSettDTO(List<SettDTO> cachedSets, Integer matchHomeScore, Integer matchAwayScore) {
        return cachedSets.stream().filter(set ->
            set.getHomeScore().equals(matchHomeScore) && set.getAwayScore().equals(matchAwayScore))
            .findFirst().get();
    }

    private GameDTO getCachedGameDTO(List<GameDTO> cachedGames, Integer homeScoreInSett, Integer awayScoreInSett) {

        Optional<GameDTO> optionalGameDTO = cachedGames.stream().filter(game ->
            game.getHomeScore().equals(homeScoreInSett) && game.getAwayScore().equals(awayScoreInSett)).findFirst();

        return optionalGameDTO.orElseGet(GameDTO::new);
    }

    private GameDTO cloneGameDTO(GameDTO gameDTO) {
        return new GameDTO(gameDTO.getHomeScore(), gameDTO.getAwayScore(), gameDTO.getHomeProbability());
    }

    private MatchDTO getMatchDTO(Map match) {
        Long matchId = ((Integer) match.get("id")).longValue();
        String name = (String) match.get("name");
        String leagueName = (String) ((Map) match.get("league")).get("name");
        return new MatchDTO(
            matchId,
            name,
            Instant.parse((String) match.get("startDate")),
            leagueName
        );
    }

    private List<OddsDTO> getOdds(List<GroupDTO> groups, Map markets, Long matchId) {
        List<OddsDTO> odds = new ArrayList<>();

        if (!markets.isEmpty()) {

            String neededGroupId = groups.stream().filter(group -> group.getName().equals("2way - Who will win?"))
                .findAny().get().getId();

            Optional<Map> neededGroupOptional = markets.values().stream().filter(market ->
                {
                    String groupId = (String) ((Map) market).get("groupId");
                    return groupId.equals(neededGroupId);
                }
            ).findAny();

            if (neededGroupOptional.isPresent()) {
                List<Map> options = (List) ((Map) ((Map) neededGroupOptional.get()).get("options")).values().stream()
                    .sorted(Comparator.comparingInt(option -> (int) ((Map) option).get("order"))).collect(Collectors.toList());
                OddsDTO oddsDTO = new OddsDTO();
                oddsDTO.setHomeOdds((Double) options.get(0).get("odds"));
                oddsDTO.setAwayOdds((Double) options.get(1).get("odds"));
                oddsDTO.setCheckDate(Instant.now());
                oddsDTO.setMatchId(matchId);

                odds.add(oddsDTO);
            }
        }
        return odds;
    }

    private List<GroupDTO> getGroups(Map rawMatches, boolean isLiveMatches) {
        Map<String, Map> groups;
        if (isLiveMatches) {
            groups = (Map) ((Map) rawMatches.get("response")).get("groups");
        } else {
            Map tennisData = (Map) ((Map) ((Map) rawMatches.get("response")).get("groupedEvents")).get("5");
            groups = (Map<String, Map>) tennisData.get("groups");
        }
        List<GroupDTO> groupDTOs = new ArrayList<>();
        groups.forEach((key, value) ->
            groupDTOs.add(new GroupDTO((String) value.get("id"), (String) value.get("name")))
        );
        return groupDTOs;
    }

    private List<Map> getMatches(Map rawMatches, boolean isLiveMatches) {
        List<Map> result;
        if (isLiveMatches) {
            Collection tennisData = ((Map) ((Map) rawMatches.get("response")).get("events")).values();
            result = (List<Map>) tennisData.stream().collect(Collectors.toList());
        } else {
            Map tennisData = (Map) ((Map) ((Map) rawMatches.get("response")).get("groupedEvents")).get("5");
            result = (List<Map>) tennisData.get("events");
        }
        return result;
    }
}
