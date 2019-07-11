package ua.tennis.repository;

import org.springframework.stereotype.Repository;
import ua.tennis.service.dto.GroupDTO;
import ua.tennis.service.dto.MatchDTO;

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
                Map<String, Double> odds = getOdds(groups, (Map) match.get("markets"));
                String name = (String) match.get("name");
                Integer id = (Integer) match.get("id");
                String leagueName = (String) ((Map) match.get("league")).get("name");
                Long leagueId = ((Integer) ((Map) match.get("league")).get("id")).longValue();
                matchDTOs.add(
                    new MatchDTO(
                        id.longValue(),
                        id.toString(),
                        (Integer) match.get("prematchEventId"),
                        name,
                        odds.get("homeOdds"),
                        odds.get("awayOdds"),
                        Instant.parse((String) match.get("openDate")),
                        Instant.parse((String) match.get("startDate")),
                        name.substring(0, name.indexOf("-") - 1),
                        name.substring(name.indexOf("-") + 2),
                        Instant.now(),
                        leagueName,
                        leagueId
                    ));
            }
        );
        return matchDTOs;
    }

    private Map<String, Double> getOdds(List<GroupDTO> groups, Map markets) {
        Map<String, Double> odds = new HashMap<>();
        String neededGroupId = groups.stream().filter(group -> group.getName().equals("2way - Who will win?"))
            .findAny().get().getId();
        List<Map> options = (List) ((Map) ((Map) markets.values().stream().filter(market ->
            {
                String groupId = (String) ((Map) market).get("groupId");
                return groupId.equals(neededGroupId);
            }
        ).findAny().get()).get("options")).values().stream()
            .sorted(Comparator.comparingInt(option -> (int) ((Map) option).get("order"))).collect(Collectors.toList());

        odds.put("homeOdds", (Double) options.get(0).get("odds"));
        odds.put("awayOdds", (Double) options.get(1).get("odds"));

        return odds;
    }
}
