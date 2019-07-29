package ua.tennis.repository;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import ua.tennis.domain.Odds;
import ua.tennis.domain.enumeration.MatchStatus;
import ua.tennis.domain.enumeration.MatchWinner;
import ua.tennis.service.CalculatorService;
import ua.tennis.service.MatchCache;
import ua.tennis.service.dto.*;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ScheduledRepository {

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
        matches.forEach(match ->
            {
                MatchDTO matchDTO = getMatchDTO(match);
                List<OddsDTO> odds = getOdds(groups, (Map) match.get("markets"), matchDTO.getId());
                matchDTO.setOdds(odds);
                if (isLiveMatches) {
                    Map scoreboardSlim = (Map) match.get("scoreboardSlim");
                    String period = (String) scoreboardSlim.get("period");
                    matchDTO.setPeriod(period);
                    if (MatchStatus.NOT_STARTED.getName().equals(period)) {
                        matchDTO.setStatus(MatchStatus.NOT_STARTED);
                        result.get(MatchStatus.NOT_STARTED).add(matchDTO);
                    } else if (MatchStatus.FINISHED.getName().equals(period)) {
                        matchDTO.setStatus(MatchStatus.FINISHED);
                        workWithFinishedMatch(matchDTO, scoreboardSlim);
                        result.get(MatchStatus.FINISHED).add(matchDTO);
                    } else if (period.endsWith("Set")) {
                        matchDTO.setStatus(MatchStatus.LIVE);
                        fillCachedMatchByProbabilities(matchDTO, scoreboardSlim);
                        workWithLiveMatch(matchDTO, scoreboardSlim, period, result);
                    } else if (MatchStatus.SUSPENDED.getName().equals(period)) {
                        matchDTO.setStatus(MatchStatus.SUSPENDED);
                        result.get(MatchStatus.SUSPENDED).add(matchDTO);
                    }
                } else {
                    matchDTO.setStatus(MatchStatus.UPCOMING);
                    result.get(MatchStatus.UPCOMING).add(matchDTO);
                }
            }
        );
    }

    private void fillCachedMatchByProbabilities(MatchDTO matchDTO, Map scoreboardSlim) {
        String gameMode = (String) scoreboardSlim.get("gameMode");
        Integer numberOfSetsToWin = (Integer.valueOf(gameMode.substring(gameMode.length() - 1)) - 3) / 2 + 2;

        Odds excitedOdds = oddsRepository.findTopByMatchIdOrderByCheckDateDesc(matchDTO.getId());

        if (excitedOdds != null){
            double homeMatchProbability = calculatorService.getRoundedDoubleNumber(
                excitedOdds.getAwayOdds() / (excitedOdds.getHomeOdds() + excitedOdds.getAwayOdds()), calculatorService.getSCALE());

            matchCache.addToCache(matchDTO.getId(),
                new MatchDTO(homeMatchProbability, calculatorService.getGameProbabilities(homeMatchProbability, numberOfSetsToWin)));

            matchDTO.setGameMode(gameMode);
            matchDTO.setNumberOfSetsToWin(numberOfSetsToWin);
        }
    }

    private void workWithFinishedMatch(MatchDTO matchDTO, Map scoreboardSlim) {
        List<List<String>> sets = (List<List<String>>) scoreboardSlim.get("sets");
        List<String> homeSets = sets.get(0);
        List<String> awaySets = sets.get(1);

        List<Integer> scoresInMatch = getScoresInMatch(homeSets, awaySets);

        Integer homeScoreInMatch = scoresInMatch.get(0);
        Integer awayScoreInMatch = scoresInMatch.get(1);
        matchDTO.setHomeScore(homeScoreInMatch);
        matchDTO.setAwayScore(awayScoreInMatch);

        if (homeScoreInMatch.compareTo(awayScoreInMatch) > 0){
            matchDTO.setWinner(MatchWinner.HOME);
        }else if (homeScoreInMatch.compareTo(awayScoreInMatch) < 0){
            matchDTO.setWinner(MatchWinner.AWAY);
        }
        matchCache.deleteFromCache(matchDTO.getId());
    }

    private void workWithLiveMatch(MatchDTO matchDTO,
                                   Map scoreboardSlim,
                                   String period,
                                   Map<MatchStatus, List<MatchDTO>> result) {

        List<List<String>> sets = (List<List<String>>) scoreboardSlim.get("sets");
        List<String> homeSets = sets.get(0);
        List<String> awaySets = sets.get(1);

        if (!"0".equals(homeSets.get(0)) || !"0".equals(awaySets.get(0))){
            List<Integer> scoresInMatch = getScoresInMatch(homeSets, awaySets);

            Integer homeScoreInMatch = scoresInMatch.get(0);
            Integer awayScoreInMatch = scoresInMatch.get(1);

            matchDTO.setHomeScore(homeScoreInMatch);
            matchDTO.setAwayScore(awayScoreInMatch);

            List<String> points = (List<String>) scoreboardSlim.get("points");
            if ("0".equals(points.get(0)) && "0".equals(points.get(1))) {

                Optional<MatchDTO> cachedMatchDTO = matchCache.getCachedMatch(matchDTO.getId());

                if (cachedMatchDTO.isPresent()) {

                    Integer setNumber = Integer.valueOf(period.substring(0, 1));

                    prepareMatchDtoForPlacingBet(matchDTO, cachedMatchDTO.get().getSetts(), Integer.valueOf(homeSets.get(setNumber - 1)),
                        Integer.valueOf(awaySets.get(setNumber - 1)));

                    result.get(MatchStatus.LIVE).add(matchDTO);
                }
            }
        }
    }

    private void prepareMatchDtoForPlacingBet(MatchDTO matchDTO,
                                              List<SettDTO> cachedSets,
                                              Integer homeScoreInSett,
                                              Integer awayScoreInSett) {

        SettDTO cachedSettDTO = getCachedSettDTO(cachedSets, matchDTO.getHomeScore(), matchDTO.getAwayScore());
        GameDTO cachedGameDTO = getCachedGameDTO(cachedSettDTO.getGames(), homeScoreInSett, awayScoreInSett);

        SettDTO clonedSettDTO = cloneSettDTO(cachedSettDTO, matchDTO.getId(), matchDTO.getOdds());
        GameDTO clonedGameDTO = cloneGameDTO(cachedGameDTO);
        clonedGameDTO.setOddsDTO(matchDTO.getOdds().get(0));

        clonedSettDTO.getGames().add(clonedGameDTO);
        matchDTO.getSetts().add(clonedSettDTO);
    }

    private List<Integer> getScoresInMatch(List<String> homeSets, List<String> awaySets) {
        List<Integer> result = new ArrayList<>();
        Integer homeScoreResult = 0;
        Integer awayScoreResult = 0;

        for (int i = 0; i < homeSets.size(); i++) {
            if (StringUtils.isNumeric(homeSets.get(i)) && StringUtils.isNumeric(awaySets.get(i))){
                Integer homeScore =  Integer.valueOf(homeSets.get(i));
                Integer awayScore =  Integer.valueOf(awaySets.get(i));
                if (homeScore.equals(6) || awayScore.equals(6)){
                    if (homeScore.compareTo(awayScore) > 0){
                        homeScoreResult = homeScoreResult + 1;
                    }else{
                        awayScoreResult = awayScoreResult + 1;
                    }
                }
            }
        }

        result.add(homeScoreResult);
        result.add(awayScoreResult);
        return result;
    }

    private SettDTO getCachedSettDTO(List<SettDTO> cachedSets, Integer homeScoreInMatch, Integer awayScoreInMatch) {
        return cachedSets.stream().filter( set ->
            set.getHomeScore().equals(homeScoreInMatch) && set.getAwayScore().equals(awayScoreInMatch))
            .collect(Collectors.toList()).get(0);
    }

    private GameDTO getCachedGameDTO(List<GameDTO> cachedGames, Integer homeScoreInSett, Integer awayScoreInSett) {
        GameDTO gameDTO;

        if (cachedGames.isEmpty()){
            gameDTO = new GameDTO();
        }else{
            gameDTO = cachedGames.stream().filter( game ->
                game.getHomeScore().equals(homeScoreInSett) && game.getAwayScore().equals(awayScoreInSett))
                .collect(Collectors.toList()).get(0);
        }

        return gameDTO;
    }

    private GameDTO cloneGameDTO(GameDTO gameDTO) {
        return new GameDTO(gameDTO.getHomeScore(), gameDTO.getAwayScore(), gameDTO.getHomeProbability());
    }

    private SettDTO cloneSettDTO(SettDTO settDTO, Long matchId, List<OddsDTO> odds) {
        return new SettDTO(settDTO.getHomeScore(),
            settDTO.getAwayScore(),
            odds.get(0).getHomeOdds(),
            odds.get(0).getAwayOdds(),
            settDTO.getHomeProbability(),
            matchId);
    }

    private MatchDTO getMatchDTO(Map match) {
        Long matchId = ((Integer) match.get("id")).longValue();
        String name = (String) match.get("name");
        return new MatchDTO(
            matchId,
            name,
            Instant.parse((String) match.get("openDate")),
            Instant.parse((String) match.get("startDate")),
            name.substring(0, name.indexOf(" - ")),
            name.substring(name.indexOf(" - ") + 3)
        );
    }

    private List<OddsDTO> getOdds(List<GroupDTO> groups, Map markets, Long matchId) {
        List<OddsDTO> odds = new ArrayList<>();

        if (!markets.isEmpty()) {

            String neededGroupId = groups.stream().filter(group -> group.getName().equals("2way - Who will win?"))
                .findAny().get().getId();
            List<Map> options = (List) ((Map) ((Map) markets.values().stream().filter(market ->
                {
                    String groupId = (String) ((Map) market).get("groupId");
                    return groupId.equals(neededGroupId);
                }
            ).findAny().get()).get("options")).values().stream()
                .sorted(Comparator.comparingInt(option -> (int) ((Map) option).get("order"))).collect(Collectors.toList());

            OddsDTO oddsDTO = new OddsDTO();
            oddsDTO.setHomeOdds((Double) options.get(0).get("odds"));
            oddsDTO.setAwayOdds((Double) options.get(1).get("odds"));
            oddsDTO.setCheckDate(Instant.now());
            oddsDTO.setMatchId(matchId);

            odds.add(oddsDTO);
        }
        return odds;
    }

    private List<GroupDTO> getGroups(Map rawMatches, boolean isLiveMatches){
        Map<String, Map> groups;
        if (isLiveMatches) {
            groups = (Map) ((Map) rawMatches.get("response")).get("groups");
        }else{
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
        if (isLiveMatches){
            Collection tennisData = ((Map) ((Map) rawMatches.get("response")).get("events")).values();
            result = (List<Map>) tennisData.stream().collect(Collectors.toList());
        }else{
            Map tennisData = (Map) ((Map) ((Map) rawMatches.get("response")).get("groupedEvents")).get("5");
            result = (List<Map>) tennisData.get("events");
        }
        return result;
    }
}
