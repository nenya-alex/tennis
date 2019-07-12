package ua.tennis.repository;

import org.springframework.stereotype.Repository;
import ua.tennis.service.CalculatorService;
import ua.tennis.service.MatchCache;
import ua.tennis.service.dto.*;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ScheduledRepository {

    private static final String SUSPENDED = "Suspended";

    private static final String FINISHED = "Finished";


    private final MatchCache matchCache;

    private final CalculatorService calculator;

    public ScheduledRepository(MatchCache matchCache,
                               CalculatorService calculator) {
        this.matchCache = matchCache;
        this.calculator = calculator;
    }

    public List<MatchDTO> getMatches(List<GroupDTO> groups, List<Map> matches, boolean isLiveMatches) {

        List<MatchDTO> matchDTOs = new ArrayList<>();

        matches.forEach(match ->
            {
                Long matchId = ((Integer) match.get("id")).longValue();
                List<OddsDTO> odds = getOdds(groups, (Map) match.get("markets"), matchId);
                String name = (String) match.get("name");
                String leagueName = (String) ((Map) match.get("league")).get("name");
                Long leagueId = ((Integer) ((Map) match.get("league")).get("id")).longValue();
                MatchDTO matchDTO = getMatchDTO(match, matchId, name, leagueName, leagueId);
                if (isLiveMatches) {
                    Map scoreboardSlim = (Map) match.get("scoreboardSlim");
                    String period = (String) scoreboardSlim.get("period");
                    if (!SUSPENDED.equals(period) && !FINISHED.equals(period)) {

                        //TODO take homeMatchProbability from DB!!!!!!!!!!
                        double homeMatchProbability = 0.544;

                        String gameMode = (String) scoreboardSlim.get("gameMode");
                        Integer numberOfSetsToWin = (Integer.valueOf(gameMode.substring(gameMode.length() - 1)) - 3) / 2 + 2;
                        matchCache.addToCache(matchId, calculator.getGameProbabilities(homeMatchProbability, numberOfSetsToWin));

                        matchDTO.setGameMode(gameMode);
                        matchDTO.setNumberOfSetsToWin(numberOfSetsToWin);

                        List<String> points = (List<String>) scoreboardSlim.get("points");
                        Integer setNumber = Integer.valueOf(period.substring(0, 1));
                        List<List<String>> sets = (List<List<String>>) scoreboardSlim.get("sets");
                        List<String> homeSets = sets.get(0);
                        List<String> awaySets = sets.get(1);

                        List<Integer> scoresInMatch = getScoresInMatch(homeSets, awaySets, setNumber);

                        Integer homeScoreInMatch = scoresInMatch.get(0);
                        Integer awayScoreInMatch = scoresInMatch.get(1);

                        matchDTO.setHomeScore(homeScoreInMatch);
                        matchDTO.setAwayScore(awayScoreInMatch);

                        if ("0".equals(points.get(0)) && "0".equals(points.get(1))) {

                            Integer homeScoreInSett = Integer.valueOf(homeSets.get(setNumber - 1));
                            Integer awayScoreInSett = Integer.valueOf(awaySets.get(setNumber - 1));

                            List<SettDTO> cachedSets = matchCache.getCachedMatch(matchId);

                            SettDTO cachedSettDTO = getCachedSettDTO(cachedSets, homeScoreInMatch, awayScoreInMatch);
                            GameDTO cachedGameDTO = getCachedGameDTO(cachedSettDTO.getGames(), homeScoreInSett, awayScoreInSett);

                            SettDTO clonedSettDTO = cloneSettDTO(cachedSettDTO, matchId, odds);
                            GameDTO clonedGameDTO = cloneGameDTO(cachedGameDTO);

                            clonedSettDTO.getGames().add(clonedGameDTO);
                            matchDTO.getSetts().add(clonedSettDTO);
                        }
                    }

                }

                matchDTO.setOdds(odds);
                matchDTOs.add(matchDTO);
            }
        );
        return matchDTOs;
    }

    private SettDTO getCachedSettDTO(List<SettDTO> cachedSets, Integer homeScoreInMatch, Integer awayScoreInMatch) {
        return cachedSets.stream().filter(set ->
            set.getHomeScore().equals(homeScoreInMatch) && set.getAwayScore().equals(awayScoreInMatch))
            .collect(Collectors.toList()).get(0);
    }

    private GameDTO getCachedGameDTO(List<GameDTO> cachedGames, Integer homeScoreInSett, Integer awayScoreInSett) {
        return cachedGames.stream().filter(game ->
            game.getHomeScore().equals(homeScoreInSett) && game.getAwayScore().equals(awayScoreInSett))
            .collect(Collectors.toList()).get(0);
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

    private List<Integer> getScoresInMatch(List<String> homeSets, List<String> awaySets, Integer setNumber) {
        List<Integer> result = new ArrayList<>();
        Integer homeScoreResult = 0;
        Integer awayScoreResult = 0;
        for (int i = 0; i < setNumber - 1; i++) {
            Integer homeScore = Integer.valueOf(homeSets.get(i));
            Integer awayScore = Integer.valueOf(awaySets.get(i));
            if (homeScore.equals(6) || awayScore.equals(6)) {
                if (homeScore.compareTo(awayScore) > 0) {
                    homeScoreResult = homeScoreResult + 1;
                } else {
                    awayScoreResult = awayScoreResult + 1;
                }
            }

        }

        result.add(homeScoreResult);
        result.add(awayScoreResult);
        return result;
    }

    private MatchDTO getMatchDTO(Map match, Long matchId, String name, String leagueName, Long leagueId) {
        return new MatchDTO(
            matchId,
            matchId.toString(),
            (Integer) match.get("prematchEventId"),
            name,
            Instant.parse((String) match.get("openDate")),
            Instant.parse((String) match.get("startDate")),
            name.substring(0, name.indexOf("-") - 1),
            name.substring(name.indexOf("-") + 2),
            leagueName,
            leagueId
        );
    }

//    private List<MatchDTO> getUpcomingMatches(List<GroupDTO> groups, List<Map> matches) {
//        List<MatchDTO> matchDTOs = new ArrayList<>();
//        matches.forEach(match ->
//            {
//                Long matchId = ((Integer) match.get("id")).longValue();
//                Set<OddsDTO> odds = getOdds(groups, (Map) match.get("markets"), matchId);
//                String name = (String) match.get("name");
//                String leagueName = (String) ((Map) match.get("league")).get("name");
//                Long leagueId = ((Integer) ((Map) match.get("league")).get("id")).longValue();
//                matchDTOs.add(
//                    new MatchDTO(
//                        matchId,
//                        matchId.toString(),
//                        (Integer) match.get("prematchEventId"),
//                        name,
//                        Instant.parse((String) match.get("openDate")),
//                        Instant.parse((String) match.get("startDate")),
//                        name.substring(0, name.indexOf("-") - 1),
//                        name.substring(name.indexOf("-") + 2),
//                        leagueName,
//                        leagueId,
//                        odds
//                    ));
//            }
//        );
//        return matchDTOs;
//    }

    private List<OddsDTO> getOdds(List<GroupDTO> groups, Map markets, Long matchId) {
        List<OddsDTO> odds = new ArrayList<>();
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
        return odds;
    }

    public List<GroupDTO> getGroups(Map<String, Map> groups) {
        List<GroupDTO> groupDTOs = new ArrayList<>();
        groups.forEach((key, value) ->
            groupDTOs.add(new GroupDTO((String) value.get("id"), (String) value.get("name")))
        );
        return groupDTOs;
    }
}
