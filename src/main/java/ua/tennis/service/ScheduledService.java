package ua.tennis.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import ua.tennis.domain.Match;
import ua.tennis.repository.MatchRepository;
import ua.tennis.repository.ScheduledRepository;
import ua.tennis.service.dto.GroupDTO;
import ua.tennis.service.dto.MatchDTO;
import ua.tennis.service.mapper.MatchMapper;

import java.util.*;

@Service
@Transactional
public class ScheduledService {

    private static final String LIVE_MATCHES_URL = "https://bcdapi.itsfogo.com/v1/BettingOffer/Grid/liveBestsellerEvents"
        + "?x-bwin-accessId=NzdmNThmYTAtNTlkMC00MDBlLTgwNmUtYTdiYmNlNGE2ZjNl&sportId=5";

    private static final String UPCOMING_MATCHES_URL = "https://bcdapi.itsfogo.com/v1/bettingoffer/grid/upcomingEventsBySport"
        + "?x-bwin-accessId=YjU5ZGYwOTMtOWRjNS00Y2M0LWJmZjktMDNhN2FhNGY3NDkw&sportId=5";

    private final RestTemplate restTemplate;

    private final ScheduledRepository scheduledRepository;

    private final MatchMapper matchMapper;

    private final MatchRepository matchRepository;

    public ScheduledService(RestTemplate restTemplate,
                            ScheduledRepository scheduledRepository,
                            MatchMapper matchMapper,
                            MatchRepository matchRepository) {
        this.restTemplate = restTemplate;
        this.scheduledRepository = scheduledRepository;
        this.matchMapper = matchMapper;
        this.matchRepository = matchRepository;
    }


    public List<MatchDTO> getLiveMatches() {
        Map liveMatches = restTemplate.getForObject(LIVE_MATCHES_URL, Map.class);
        List<Map> tennisData = (List<Map>) ((Map) ((Map) liveMatches.get("response")).get("events")).values();
        List<GroupDTO> groups = getGroups((Map) ((Map) liveMatches.get("response")).get("groups"));
        return scheduledRepository.getMatches(groups, tennisData, true);
    }

    public void saveUpcomingMatches() {
        Map upcomingMatches = restTemplate.getForObject(UPCOMING_MATCHES_URL, Map.class);
        Map tennisData = (Map) ((Map) ((Map) upcomingMatches.get("response")).get("groupedEvents")).get("5");
        List<GroupDTO> groups = getGroups((Map<String, Map>) tennisData.get("groups"));
        List<MatchDTO> matchDTOs = scheduledRepository.getMatches(groups, (List<Map>) tennisData.get("events"), false);

        List<Match> matches = matchMapper.matchDtosToEntity(matchDTOs);
        matchRepository.save(matches);
    }

    private List<GroupDTO> getGroups(Map<String, Map> groups) {
        List<GroupDTO> groupDTOs = new ArrayList<>();
        groups.forEach((key, value) ->
            groupDTOs.add(new GroupDTO((String) value.get("id"), (String) value.get("name")))
        );
        return groupDTOs;
    }
}
