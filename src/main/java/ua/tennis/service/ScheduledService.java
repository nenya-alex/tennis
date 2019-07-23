package ua.tennis.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import ua.tennis.domain.Account;
import ua.tennis.domain.Bet;
import ua.tennis.domain.Match;
import ua.tennis.domain.Odds;
import ua.tennis.domain.enumeration.BetSide;
import ua.tennis.domain.enumeration.BetStatus;
import ua.tennis.domain.enumeration.MatchStatus;
import ua.tennis.repository.*;
import ua.tennis.service.dto.AccountDetailDTO;
import ua.tennis.service.dto.BetDTO;
import ua.tennis.service.dto.GameDTO;
import ua.tennis.service.dto.MatchDTO;
import ua.tennis.service.mapper.AccountDetailMapper;
import ua.tennis.service.mapper.BetMapper;
import ua.tennis.service.mapper.MatchMapper;

import java.math.BigDecimal;
import java.time.Instant;
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

    private final AccountRepository accountRepository;

    private final BetRepository betRepository;

    private final BetMapper betMapper;

    private final AccountDetailRepository accountDetailRepository;

    private final AccountDetailMapper accountDetailMapper;

    public ScheduledService(RestTemplate restTemplate,
                            ScheduledRepository scheduledRepository,
                            MatchMapper matchMapper,
                            MatchRepository matchRepository,
                            OddsRepository oddsRepository,
                            AccountRepository accountRepository,
                            BetRepository betRepository,
                            BetMapper betMapper,
                            AccountDetailRepository accountDetailRepository,
                            AccountDetailMapper accountDetailMapper) {
        this.restTemplate = restTemplate;
        this.scheduledRepository = scheduledRepository;
        this.matchMapper = matchMapper;
        this.matchRepository = matchRepository;
        this.oddsRepository = oddsRepository;
        this.accountRepository = accountRepository;
        this.betRepository = betRepository;
        this.betMapper = betMapper;
        this.accountDetailRepository = accountDetailRepository;
        this.accountDetailMapper = accountDetailMapper;
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
        for (MatchDTO matchDTO : matchDTOs) {

            Match match = matchRepository.findOne(matchDTO.getId());

            if (match != null) {

                //TODO java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
                GameDTO gameDTO = matchDTO.getSetts().get(0).getGames().get(0);

                double homeOdds = gameDTO.getOddsDTO().getHomeOdds();
                double awayOdds = gameDTO.getOddsDTO().getAwayOdds();
                double bookmakersHomeProbability = awayOdds / (homeOdds + awayOdds);
                double homeProbability = gameDTO.getHomeProbability();

                if (gameDTO.getHomeProbability() > bookmakersHomeProbability * MULTIPLIER) {
                    place(match, homeOdds, (homeOdds + awayOdds) / awayOdds, homeProbability, BetSide.HOME);
                } else if ((1 - gameDTO.getHomeProbability()) > (1 - bookmakersHomeProbability) * MULTIPLIER) {
                    place(match, awayOdds, (homeOdds + awayOdds) / homeOdds, 1 - homeProbability, BetSide.AWAY);
                }
            }
        }
    }

    private void place(Match match,
                       double odds,
                       double bookmakerOddsWithoutMarge,
                       Double probability,
                       BetSide betSide) {
        double kellyCoefficient = (bookmakerOddsWithoutMarge * probability - 1) / (bookmakerOddsWithoutMarge - 1);
        Account account = accountRepository.findOne(1L);
        BigDecimal stakeAmount = account.getAmount().multiply(BigDecimal.valueOf(kellyCoefficient));

        BetDTO betDTO = new BetDTO();
        betDTO.setAmount(stakeAmount);
        betDTO.setOdds(odds);
        betDTO.setBetSide(betSide);
        betDTO.setPlacedDate(Instant.now());
        betDTO.setMatchId(match.getId());

        if (match.getBets().stream().noneMatch(bet -> bet.getStatus() == BetStatus.OPENED)) {
            Bet savedBet = saveBet(betDTO, BetStatus.OPENED);
            saveAccountDetail(account.getAmount(), account.getId(), stakeAmount, savedBet.getId());
            saveAccount(account, account.getAmount().subtract(stakeAmount),
                account.getPlacedAmount().add(stakeAmount));
        } else {
            saveBet(betDTO, BetStatus.POTENTIAL);
        }
    }

    private Bet saveBet(BetDTO betDTO, BetStatus betStatus) {
        betDTO.setStatus(betStatus);
        return betRepository.save(betMapper.toEntity(betDTO));
    }

    private void saveAccount(Account account, BigDecimal amount, BigDecimal placedAmount) {
        account.setAmount(amount);
        account.setUpdatedDate(Instant.now());
        account.setPlacedAmount(placedAmount);
        accountRepository.save(account);
    }

    private void saveAccountDetail(BigDecimal amount,
                                   Long accountId,
                                   BigDecimal placedAmount,
                                   Long betId) {
        AccountDetailDTO accountDetailDTO = new AccountDetailDTO();
        accountDetailDTO.setAmount(amount);
        accountDetailDTO.setBetId(betId);
        accountDetailDTO.setAccountId(accountId);
        accountDetailDTO.setCreatedDate(Instant.now());
        accountDetailDTO.setPlacedAmount(placedAmount);
        accountDetailRepository.save(accountDetailMapper.toEntity(accountDetailDTO));
    }

    private void updateAccountForFinishedMatches(List<MatchDTO> matchDTOs) {
        for (MatchDTO matchDTO : matchDTOs) {
            Match match = matchRepository.findOne(matchDTO.getId());
            if (match != null) {
                match.setStatus(MatchStatus.FINISHED);

                Account account = accountRepository.findOne(1L);
                Bet bet = match.getBets().stream()
                    .filter(innerBet -> innerBet.getStatus() == BetStatus.OPENED).findFirst().get();

                BigDecimal amount;
                BigDecimal placedAmount;

                if (isBetWon(matchDTO, bet)) {
                    amount = account.getAmount().add(bet.getAmount().multiply(BigDecimal.valueOf(bet.getOdds())));
                    placedAmount = account.getPlacedAmount().subtract(bet.getAmount());
                } else {
                    amount = account.getAmount();
                    placedAmount = account.getPlacedAmount().subtract(bet.getAmount());
                }

                saveAccount(account, amount, placedAmount);

                saveAccountDetail(account.getAmount(), account.getId(), BigDecimal.ZERO, bet.getId());

                bet.setStatus(BetStatus.CLOSED);
                betRepository.save(bet);
            }
        }
    }

    private boolean isBetWon(MatchDTO matchDTO, Bet bet) {
        return bet.getBetSide().name().equals(matchDTO.getWinner().name());
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
