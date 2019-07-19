package ua.tennis.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import ua.tennis.domain.Match;
import ua.tennis.domain.Odds;
import ua.tennis.domain.enumeration.MatchStatus;
import ua.tennis.domain.enumeration.MatchWinner;
import ua.tennis.repository.MatchRepository;
import ua.tennis.repository.OddsRepository;
import ua.tennis.repository.ScheduledRepository;
import ua.tennis.service.dto.GameDTO;
import ua.tennis.service.dto.MatchDTO;
import ua.tennis.service.mapper.MatchMapper;

import java.util.*;

@Service
@Transactional
public class ScheduledService {

    private static final String UPCOMING_MATCHES_URL = "https://bcdapi.itsfogo.com/v1/bettingoffer/grid/upcomingEventsBySport"
        + "?x-bwin-accessId=YjU5ZGYwOTMtOWRjNS00Y2M0LWJmZjktMDNhN2FhNGY3NDkw&sportId=5";

    private static final String LIVE_MATCHES_URL = "https://bcdapi.itsfogo.com/v1/bettingoffer/grid/liveOverviewEvents" +
        "?x-bwin-accessId=YjU5ZGYwOTMtOWRjNS00Y2M0LWJmZjktMDNhN2FhNGY3NDkw&sportId=5";

    private static final double MULTIPLIER = 1.1;

    private final RestTemplate restTemplate;

    private final ScheduledRepository scheduledRepository;

    private final MatchMapper matchMapper;

    private final MatchRepository matchRepository;

    private final OddsRepository oddsRepository;

    public ScheduledService(RestTemplate restTemplate,
                            ScheduledRepository scheduledRepository,
                            MatchMapper matchMapper,
                            MatchRepository matchRepository,
                            OddsRepository oddsRepository) {
        this.restTemplate = restTemplate;
        this.scheduledRepository = scheduledRepository;
        this.matchMapper = matchMapper;
        this.matchRepository = matchRepository;
        this.oddsRepository = oddsRepository;
    }


    public void saveLiveMatches() {
        Map liveMatches = restTemplate.getForObject(LIVE_MATCHES_URL, Map.class);

        Map<MatchStatus, List<MatchDTO>> result = new HashMap<>();
        result.put(MatchStatus.NOT_STARTED, new ArrayList<>());
        result.put(MatchStatus.LIVE, new ArrayList<>());
        result.put(MatchStatus.FINISHED, new ArrayList<>());
        result.put(MatchStatus.SUSPENDED, new ArrayList<>());

        scheduledRepository.fillResultByMatches(liveMatches, true, result);

        saveMatches(result.get(MatchStatus.NOT_STARTED));

        placeBet(result.get(MatchStatus.LIVE));

        updateAccountForFinishedMatches(result.get(MatchStatus.FINISHED));

    }

    private void placeBet(List<MatchDTO> matchDTOs) {
        for (MatchDTO matchDTO: matchDTOs) {

            //TODO java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
            GameDTO gameDTO = matchDTO.getSetts().get(0).getGames().get(0);

            double homeOdds = gameDTO.getOddsDTO().getHomeOdds();
            double awayOdds = gameDTO.getOddsDTO().getAwayOdds();
            double bookmakersHomeProbability = awayOdds / (homeOdds + awayOdds);

            if (gameDTO.getHomeProbability() > bookmakersHomeProbability * MULTIPLIER) {
                place(matchDTO.getId(), homeOdds, MatchWinner.HOME);
            } else if ((1 - gameDTO.getHomeProbability()) > (1 - bookmakersHomeProbability) * MULTIPLIER) {
                place(matchDTO.getId(), awayOdds, MatchWinner.AWAY);
            }
        }
    }

    private void place(Long matchId, double odds, MatchWinner matchWinner) {

    }

    private void updateAccountForFinishedMatches(List<MatchDTO> matchDTOs) {

    }

    public void saveUpcomingMatches() {

        Map upcomingMatches = restTemplate.getForObject(UPCOMING_MATCHES_URL, Map.class);

        Map<MatchStatus, List<MatchDTO>> result = new HashMap<>();
        result.put(MatchStatus.UPCOMING, new ArrayList<>());

        scheduledRepository.fillResultByMatches(upcomingMatches, false, result);

        saveMatches(result.get(MatchStatus.UPCOMING));
    }

    private void saveMatches(List<MatchDTO> matchDTOs) {
        List<Match> matches = matchMapper.matchDtosToEntity(matchDTOs);
        for (Match match : matches) {
            Optional<Match> excitedMatch = matchRepository.findByIdentifier(match.getIdentifier());
            if (excitedMatch.isPresent()) {
                Odds excitedOdds = oddsRepository.findTopByMatchIdOrderByCheckDateDesc(excitedMatch.get().getId());
                Odds odds = new ArrayList<>(match.getOdds()).get(0);
                if (!odds.getHomeOdds().equals(excitedOdds.getHomeOdds())
                    || !odds.getAwayOdds().equals(excitedOdds.getAwayOdds())) {
                    oddsRepository.save(odds);
                }
            } else {
                matchRepository.save(match);
            }
        }
    }

}
