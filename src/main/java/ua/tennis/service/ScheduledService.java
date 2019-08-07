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
import ua.tennis.domain.enumeration.Winner;
import ua.tennis.repository.*;
import ua.tennis.service.dto.*;
import ua.tennis.service.mapper.*;

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

    private final CalculatorService calculatorService;

    private final SettRepository settRepository;

    private final SettMapper settMapper;

    private final MatchCache matchCache;

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
                            OddsMapper oddsMapper,
                            CalculatorService calculatorService,
                            SettRepository settRepository,
                            SettMapper settMapper,
                            MatchCache matchCache) {
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
        this.calculatorService = calculatorService;
        this.settRepository = settRepository;
        this.settMapper = settMapper;
        this.matchCache = matchCache;
    }


    public void saveLiveMatches() {
        Map liveMatches = restTemplate.getForObject(LIVE_MATCHES_URL, Map.class);

        Map<MatchStatus, List<MatchDTO>> result = new HashMap<>();
        result.put(MatchStatus.NOT_STARTED, new ArrayList<>());
        result.put(MatchStatus.LIVE, new ArrayList<>());
        result.put(MatchStatus.SUSPENDED, new ArrayList<>());

        scheduledRepository.fillResultByMatches(liveMatches, true, result);

        saveSuspendedMatches(result.get(MatchStatus.SUSPENDED));

        saveMatches(result.get(MatchStatus.NOT_STARTED));

        placeBet(result.get(MatchStatus.LIVE));

    }

    private void placeBet(List<MatchDTO> matchDTOs) {
        for (MatchDTO matchDTO : matchDTOs) {

            Match match = matchRepository.findOne(matchDTO.getId());

            if (match != null) {
                createOrUpdateSett(matchDTO);
                updateMatch(match);

                //TODO java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
                log.debug("\nPLACE BET: \n Request for MatchDTO with \n POTENTIAL ERROR : {}", matchDTO);

                List<GameDTO> gameDTOs = matchDTO.getSetts().get(matchDTO.getCurrentSetNumber() - 1).getGames();
                if (!gameDTOs.isEmpty()) {
                    GameDTO gameDTO = gameDTOs.get(0);

                    double homeOdds = gameDTO.getOddsDTO().getHomeOdds();
                    double awayOdds = gameDTO.getOddsDTO().getAwayOdds();
                    double bookmakersHomeProbability = calculatorService.getRoundedDoubleNumber(awayOdds / (homeOdds + awayOdds));
                    double homeProbability = gameDTO.getHomeProbability();

                    if (gameDTO.getHomeProbability() > bookmakersHomeProbability * MULTIPLIER) {
                        place(match, homeOdds, calculatorService.getRoundedDoubleNumber((homeOdds + awayOdds) / awayOdds), homeProbability, BetSide.HOME);
                    } else if ((1 - gameDTO.getHomeProbability()) > (1 - bookmakersHomeProbability) * MULTIPLIER) {
                        place(match, awayOdds, calculatorService.getRoundedDoubleNumber((homeOdds + awayOdds) / homeOdds), 1 - homeProbability, BetSide.AWAY);
                    }
                }
            }
        }
    }

    private void updateMatch(Match match) {
//        log.debug("\nPLACE BET: Request for Match : {}", match);
        if (match.getStatus() != MatchStatus.LIVE) {
            matchRepository.saveAndFlush(match.status(MatchStatus.LIVE).updatedDate(Instant.now()));
//            log.debug("\nPLACE BET: Saved LIVE Match : {}", match);
        }
    }

    private void createOrUpdateSett(MatchDTO matchDTO) {
        SettDTO settDTO = matchDTO.getSetts().get(matchDTO.getCurrentSetNumber() - 1);
//        log.debug("\nPLACE BET: Request for saving SettDTO : {}", settDTO);

        Sett sett = settRepository.findBySetNumberAndMatchId(matchDTO.getCurrentSetNumber(), matchDTO.getId());

        if (sett == null) {
            settRepository.save(settMapper.toEntity(settDTO));
        } else {
            settRepository.save(sett.homeScore(settDTO.getHomeScore()).awayScore(settDTO.getAwayScore())
                .homeProbability(settDTO.getHomeProbability()));
        }
//        log.debug("\nPLACE BET: Saved Sett : {}", sett);
    }

    private void place(Match match,
                       double odds,
                       double bookmakerOddsWithoutMarge,
                       double probability,
                       BetSide betSide) {
        double kellyCoefficient = calculatorService.getRoundedDoubleNumber(
            (bookmakerOddsWithoutMarge * probability - 1) / (bookmakerOddsWithoutMarge - 1));

//        log.debug("\nPLACE BET: KellyCoefficient = {} for Match id :{}", kellyCoefficient, match.getId());

        Account account = accountRepository.findOne(1L);

        if (account.getAmount().compareTo(BigDecimal.ZERO) > 0) {

//        log.debug("\nPLACE BET: Account before placing : {} ", account);

            BigDecimal stakeAmount = account.getAmount().multiply(BigDecimal.valueOf(kellyCoefficient));

//        log.debug("\nPLACE BET: stakeAmount = {} ", stakeAmount);

            BetDTO betDTO = new BetDTO();
            betDTO.setAmount(stakeAmount);
            betDTO.setOdds(odds);
            betDTO.setBetSide(betSide);
            betDTO.setPlacedDate(Instant.now());
            betDTO.setMatchId(match.getId());
            betDTO.setKellyCoefficient(kellyCoefficient);
            betDTO.setCountedProbability(probability);
            betDTO.setBookmakerProbability(calculatorService.getRoundedDoubleNumber(1 / bookmakerOddsWithoutMarge));

            if (match.getBets().stream().noneMatch(bet -> bet.getStatus() == BetStatus.OPENED)) {
                Bet savedBet = saveBet(betDTO, BetStatus.OPENED);

                saveAccount(account, account.getAmount().subtract(stakeAmount), account.getPlacedAmount().add(stakeAmount));

                saveAccountDetail(account.getAmount(), account.getId(), stakeAmount, savedBet.getId());

            } else {
//            saveBet(betDTO, BetStatus.POTENTIAL);
            }
        }
    }

    private Bet saveBet(BetDTO betDTO, BetStatus betStatus) {
        betDTO.setStatus(betStatus);
        Bet savedBet = betRepository.saveAndFlush(betMapper.toEntity(betDTO));
//        log.debug("\nPLACE BET: Saved " + betStatus.name() + " Bet : {}", savedBet);
        return savedBet;

    }

    private void saveAccount(Account account, BigDecimal amount, BigDecimal placedAmount) {
        accountRepository.saveAndFlush(account.amount(amount).updatedDate(Instant.now()).placedAmount(placedAmount));
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

    public void saveUpcomingMatches() {

        Map upcomingMatches = restTemplate.getForObject(UPCOMING_MATCHES_URL, Map.class);

        Map<MatchStatus, List<MatchDTO>> result = new HashMap<>();
        result.put(MatchStatus.UPCOMING, new ArrayList<>());

        scheduledRepository.fillResultByMatches(upcomingMatches, false, result);

        saveMatches(result.get(MatchStatus.UPCOMING));
    }

    private void saveMatches(List<MatchDTO> matchDTOs) {
        for (MatchDTO matchDTO : matchDTOs) {

            matchDTO.setUpdatedDate(Instant.now());

//            log.debug("\nMatchDTO to save : {}", matchDTO);

            Match excitedMatch = matchRepository.findOne(matchDTO.getId());
            if (excitedMatch != null) {
                Odds existedOdds = oddsRepository.findTopByMatchIdOrderByCheckDateDesc(excitedMatch.getId());

                List<OddsDTO> oddsDTOs = matchDTO.getOdds();
                if (!oddsDTOs.isEmpty()) {
                    OddsDTO oddsDTO = oddsDTOs.get(0);

                    if (!oddsDTO.getHomeOdds().equals(existedOdds.getHomeOdds())
                        || !oddsDTO.getAwayOdds().equals(existedOdds.getAwayOdds())) {

                        Odds odds = oddsRepository.saveAndFlush(oddsMapper.toEntity(oddsDTO));
//                        log.debug("\nSaved Odds : {} \n for existed Match : {}", odds, excitedMatch);

                    }
                }
            } else {
                Match match = matchRepository.saveAndFlush(matchMapper.toEntity(matchDTO));
//                log.debug("\nSaved new Match : {}", match);
            }
        }
    }

    private void saveSuspendedMatches(List<MatchDTO> matchDTOs) {
        for (MatchDTO matchDTO : matchDTOs) {

            //            log.debug("\nSuspended MatchDTO to update : {}", matchDTO);

            Match existedMatch = matchRepository.findOne(matchDTO.getId());
            if (existedMatch != null) {
                matchRepository.save(existedMatch.status(MatchStatus.SUSPENDED).updatedDate(Instant.now()));
//                log.debug("\nUpdated Suspended Match : {}", existedMatch);
            }
        }
    }

    public void prepareMatchesToFinish() {
        List<Match> matches =
            matchRepository.findByStatusAndUpdatedDateBefore(MatchStatus.LIVE, Instant.now().minusSeconds(1800L));
        for (Match match : matches) {
            saveMatchAsReadyToFinish(match);
        }
    }

    private void saveMatchAsReadyToFinish(Match match) {
        log.debug("\nPREPARE TO FINISH: Match : {}", match);
        match.setStatus(MatchStatus.READY_TO_FINISH);

        Integer matchHomeScore = match.getHomeScore();
        Integer matchAwayScore = match.getAwayScore();

        if (matchHomeScore.compareTo(matchAwayScore) > 0) {
            match.setWinner(Winner.HOME);
        } else if (matchHomeScore.compareTo(matchAwayScore) < 0) {
            match.setWinner(Winner.AWAY);
        }

        matchRepository.save(match);
        log.debug("\nPREPARE TO FINISH: Saved Match : {}", match);

        matchCache.deleteFromCache(match.getId());
    }

    public void finishMatchsAndSettleBets() {
        List<Match> matchesToFinish = matchRepository.findByStatus(MatchStatus.READY_TO_FINISH);
        for (Match match : matchesToFinish) {
            if (match.isScoreCorrect()) {
                matchRepository.save(match.status(MatchStatus.FINISHED));
                log.debug("\nFINISH MATCH: Saved Match : {}", match);

                settleBets(betRepository.findByMatchIdAndStatus(match.getId(), BetStatus.OPENED), match.getWinner());
            }
        }
    }

    private void settleBets(Set<Bet> bets, Winner winner) {

        Account account = accountRepository.findOne(1L);
        log.debug("\nSETTLE BET: Account before settlement: {}", account);

        for (Bet bet : bets) {
            log.debug("\nSETTLE BET: Bet before settlement : {}", bet);

            BigDecimal amount;
            BigDecimal placedAmount = account.getPlacedAmount().subtract(bet.getAmount());

            if (isBetWon(bet, winner.name())) {
                amount = account.getAmount().add(bet.getAmount().multiply(BigDecimal.valueOf(bet.getOdds())));
            } else {
                amount = account.getAmount();
            }

            betRepository.saveAndFlush(bet.status(BetStatus.CLOSED).winner(winner).settledDate(Instant.now()));
            log.debug("\nSETTLE BET: Bet after settlement : {}", bet);

            saveAccount(account, amount, placedAmount);

            saveAccountDetail(account.getAmount(), account.getId(), BigDecimal.ZERO, bet.getId());
        }
    }

    private boolean isBetWon(Bet bet, String winnerName) {
        return bet.getBetSide().name().equals(winnerName);
    }
}
