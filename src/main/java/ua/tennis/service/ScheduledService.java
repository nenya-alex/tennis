package ua.tennis.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import ua.tennis.domain.*;
import ua.tennis.domain.enumeration.BetSide;
import ua.tennis.domain.enumeration.BetStatus;
import ua.tennis.domain.enumeration.MatchStatus;
import ua.tennis.repository.*;
import ua.tennis.service.dto.*;
import ua.tennis.service.mapper.AccountDetailMapper;
import ua.tennis.service.mapper.BetMapper;
import ua.tennis.service.mapper.MatchMapper;
import ua.tennis.service.mapper.OddsMapper;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

@Service
@Transactional
public class ScheduledService {

    private final Logger log = LoggerFactory.getLogger(ScheduledService.class);

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

    private final OddsMapper oddsMapper;

    public ScheduledService(RestTemplate restTemplate,
                            ScheduledRepository scheduledRepository,
                            MatchMapper matchMapper,
                            MatchRepository matchRepository,
                            OddsRepository oddsRepository,
                            AccountRepository accountRepository,
                            BetRepository betRepository,
                            BetMapper betMapper,
                            AccountDetailRepository accountDetailRepository,
                            AccountDetailMapper accountDetailMapper,
                            OddsMapper oddsMapper) {
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
        this.oddsMapper = oddsMapper;
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

        settleBet(result.get(MatchStatus.FINISHED));

    }

    private void placeBet(List<MatchDTO> matchDTOs) {
        for (MatchDTO matchDTO : matchDTOs) {

            Match match = matchRepository.findOne(matchDTO.getId());

            if (match != null) {

                log.debug("\nPLACE BET: Request for Match : {}", match);
                log.debug("\nPLACE BET: Request for MatchDTO : {}", matchDTO);

                if (match.getStatus() != MatchStatus.LIVE) {
                    match.setStatus(MatchStatus.LIVE);
                    matchRepository.saveAndFlush(match);

                    log.debug("\nPLACE BET: Saved LIVE Match : {}", match);
                }

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

        log.debug("\nPLACE BET: KellyCoefficient = {} for Match id :{}", kellyCoefficient, match.getId());

        Account account = accountRepository.findOne(1L);

        log.debug("\nPLACE BET: Account before placing : {} ", account);

        BigDecimal stakeAmount = account.getAmount().multiply(BigDecimal.valueOf(kellyCoefficient));

        log.debug("\nPLACE BET: stakeAmount = {} ", stakeAmount);

        BetDTO betDTO = new BetDTO();
        betDTO.setAmount(stakeAmount);
        betDTO.setOdds(odds);
        betDTO.setBetSide(betSide);
        betDTO.setPlacedDate(Instant.now());
        betDTO.setMatchId(match.getId());

        if (match.getBets().stream().noneMatch(bet -> bet.getStatus() == BetStatus.OPENED)) {
            Bet savedBet = saveBet(betDTO, BetStatus.OPENED);
            log.debug("\nPLACE BET: Saved OPENED Bet : {}", savedBet);

            saveAccount(account, account.getAmount().subtract(stakeAmount),
                account.getPlacedAmount().add(stakeAmount));

            saveAccountDetail(account.getAmount(), account.getId(), stakeAmount, savedBet.getId());

        } else {
            Bet savedBet = saveBet(betDTO, BetStatus.POTENTIAL);
            log.debug("\nPLACE BET: Saved POTENTIAL Bet : {}", savedBet);
        }
    }

    private Bet saveBet(BetDTO betDTO, BetStatus betStatus) {
        betDTO.setStatus(betStatus);
        return betRepository.saveAndFlush(betMapper.toEntity(betDTO));
    }

    private void saveAccount(Account account, BigDecimal amount, BigDecimal placedAmount) {
        account.setAmount(amount);
        account.setUpdatedDate(Instant.now());
        account.setPlacedAmount(placedAmount);
        accountRepository.saveAndFlush(account);

        log.debug("\nSaved Account after action: {}", account);
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
        AccountDetail accountDetail = accountDetailRepository.saveAndFlush(accountDetailMapper.toEntity(accountDetailDTO));

        log.debug("\nSaved AccountDetail : {}", accountDetail);
    }

    private void settleBet(List<MatchDTO> matchDTOs) {
        for (MatchDTO matchDTO : matchDTOs) {
            Match match = matchRepository.findOne(matchDTO.getId());

            if (match != null && match.getStatus() != MatchStatus.FINISHED) {

                log.debug("\nSETTLE BET: Match to finish : {}", match);

                match.setStatus(MatchStatus.FINISHED);
                matchRepository.saveAndFlush(match);

                log.debug("\nSETTLE BET: Saved Match : {}", match);

                Set<Bet> bets = betRepository.findByMatchId(match.getId());

                if (!bets.isEmpty()) {

                    Account account = accountRepository.findOne(1L);

                    log.debug("\nSETTLE BET: Account before settlement: {}", account);

                    Bet bet = bets.stream()
                        .filter(innerBet -> innerBet.getStatus() == BetStatus.OPENED).findFirst().get();

                    log.debug("\nSETTLE BET: Bet before settlement : {}", bet);

                    BigDecimal amount;
                    BigDecimal placedAmount;

                    if (isBetWon(matchDTO, bet)) {
                        amount = account.getAmount().add(bet.getAmount().multiply(BigDecimal.valueOf(bet.getOdds())));
                        placedAmount = account.getPlacedAmount().subtract(bet.getAmount());
                    } else {
                        amount = account.getAmount();
                        placedAmount = account.getPlacedAmount().subtract(bet.getAmount());
                    }

                    bet.setStatus(BetStatus.CLOSED);
                    betRepository.saveAndFlush(bet);
                    log.debug("\nSETTLE BET: Bet after settlement : {}", bet);

                    saveAccount(account, amount, placedAmount);

                    saveAccountDetail(account.getAmount(), account.getId(), BigDecimal.ZERO, bet.getId());
                }
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
        for (MatchDTO matchDTO : matchDTOs) {
            Match excitedMatch = matchRepository.findOne(matchDTO.getId());
            if (excitedMatch != null) {
                Odds existedOdds = oddsRepository.findTopByMatchIdOrderByCheckDateDesc(excitedMatch.getId());
                OddsDTO oddsDTO = matchDTO.getOdds().get(0);
                if (!oddsDTO.getHomeOdds().equals(existedOdds.getHomeOdds())
                    || !oddsDTO.getAwayOdds().equals(existedOdds.getAwayOdds())) {

                    Odds odds = oddsRepository.saveAndFlush(oddsMapper.toEntity(oddsDTO));

                    log.debug("\nSaved Odds : {} for existed Match : {}", odds, excitedMatch);

                }
            } else {
                Match match = matchRepository.saveAndFlush(matchMapper.toEntity(matchDTO));

                log.debug("\nSaved new Match : {}", match);
            }
        }
    }

}
