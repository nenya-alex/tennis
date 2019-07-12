package ua.tennis.repository;

import org.springframework.stereotype.Repository;
import ua.tennis.service.dto.GroupDTO;
import ua.tennis.service.dto.MatchDTO;
import ua.tennis.service.dto.OddsDTO;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ScheduledRepository {

    public List<MatchDTO> getMatches(List<GroupDTO> groups, List<Map> matches, boolean isLiveMatches) {
        if (isLiveMatches) {
            return new ArrayList<>();
        } else {
            return getUpcomingMatches(groups, matches);
        }
    }

    private List<MatchDTO> getUpcomingMatches(List<GroupDTO> groups, List<Map> matches) {
        List<MatchDTO> matchDTOs = new ArrayList<>();
        matches.forEach(match ->
            {
                Long matchId = ((Integer) match.get("id")).longValue();
                Set<OddsDTO> odds = getOdds(groups, (Map) match.get("markets"), matchId);
                String name = (String) match.get("name");
                String leagueName = (String) ((Map) match.get("league")).get("name");
                Long leagueId = ((Integer) ((Map) match.get("league")).get("id")).longValue();
                matchDTOs.add(
                    new MatchDTO(
                        matchId,
                        matchId.toString(),
                        (Integer) match.get("prematchEventId"),
                        name,
                        Instant.parse((String) match.get("openDate")),
                        Instant.parse((String) match.get("startDate")),
                        name.substring(0, name.indexOf("-") - 1),
                        name.substring(name.indexOf("-") + 2),
                        leagueName,
                        leagueId,
                        odds
                    ));
            }
        );
        return matchDTOs;
    }

    private Set<OddsDTO> getOdds(List<GroupDTO> groups, Map markets, Long matchId) {
        Set<OddsDTO> odds = new HashSet<>();
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
