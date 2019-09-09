package ua.tennis.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import ua.tennis.domain.*;
import ua.tennis.domain.enumeration.*;
import ua.tennis.repository.*;
import ua.tennis.service.dto.*;
import ua.tennis.service.mapper.*;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ScheduledService {

    private static final String IS_OPPOSITE_BET_PLACEMENT_ENABLE = "IS_OPPOSITE_BET_PLACEMENT_ENABLE";
    private final Logger log = LoggerFactory.getLogger(ScheduledService.class);

    private static final String UPCOMING_MATCHES_URL = "https://bcdapi.itsfogo.com/v1/bettingoffer/grid/upcomingEventsBySport"
        + "?x-bwin-accessId=YjU5ZGYwOTMtOWRjNS00Y2M0LWJmZjktMDNhN2FhNGY3NDkw&sportId=5";

    private static final String LIVE_MATCHES_URL = "https://bcdapi.itsfogo.com/v1/bettingoffer/grid/liveOverviewEvents" +
        "?x-bwin-accessId=YjU5ZGYwOTMtOWRjNS00Y2M0LWJmZjktMDNhN2FhNGY3NDkw&sportId=5";

    private static final String PROBABILITY_MULTIPLIER = "PROBABILITY_MULTIPLIER";

    private static final String IS_BET_PLACEMENT_ENABLE = "IS_BET_PLACEMENT_ENABLE";

    private static final String MAX_ODDS = "MAX_ODDS";

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

    private final SettingsRepository settingsRepository;

    private final JavaMailSender javaMailSender;

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
                            MatchCache matchCache,
                            SettingsRepository settingsRepository,
                            JavaMailSender javaMailSender) {
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
        this.settingsRepository = settingsRepository;
        this.javaMailSender = javaMailSender;
    }

    public void saveUpcomingMatches() {

        Map upcomingMatches = restTemplate.getForObject(UPCOMING_MATCHES_URL, Map.class);

        Map<MatchStatus, List<MatchDTO>> result = new HashMap<>();
        result.put(MatchStatus.UPCOMING, new ArrayList<>());

        scheduledRepository.fillResultByMatches(upcomingMatches, false, result);

        saveUpcomingMatches(result.get(MatchStatus.UPCOMING));
    }

    private void saveUpcomingMatches(List<MatchDTO> matchDTOs) {
        saveMatches(matchDTOs);
    }

    public void saveLiveMatches() {
        Map liveMatches = restTemplate.getForObject(LIVE_MATCHES_URL, Map.class);

        Map<MatchStatus, List<MatchDTO>> result = new HashMap<>();
        result.put(MatchStatus.NOT_STARTED, new ArrayList<>());
        result.put(MatchStatus.LIVE, new ArrayList<>());
        result.put(MatchStatus.SUSPENDED, new ArrayList<>());
        result.put(MatchStatus.FINISHED, new ArrayList<>());

        scheduledRepository.fillResultByMatches(liveMatches, true, result);

        saveSuspendedMatches(result.get(MatchStatus.SUSPENDED));

        saveNotStartedMatches(result.get(MatchStatus.NOT_STARTED));

        saveLiveMatches(result.get(MatchStatus.LIVE));

        saveFinishedMatches(result.get(MatchStatus.FINISHED));

    }

    private void saveFinishedMatches(List<MatchDTO> matchDTOs) {
        for (MatchDTO matchDTO : matchDTOs) {
            log.debug("\nFINISH MATCH: \n Request for MatchDTO : {}", matchDTO);

            Match match = matchRepository.findOne(matchDTO.getId());

            if (match != null && match.getStatus() != MatchStatus.FINISHED) {
                createOrUpdateSetts(matchDTO, match);
                updateMatch(match, matchDTO);
            }
        }
    }

    private void saveNotStartedMatches(List<MatchDTO> matchDTOs) {
        saveMatches(matchDTOs);
    }

    private void saveLiveMatches(List<MatchDTO> matchDTOs) {

        double multiplier = Double.parseDouble(settingsRepository.findByKey(PROBABILITY_MULTIPLIER).getValue());
        boolean isBetPlacementEnable = Boolean.valueOf(settingsRepository.findByKey(IS_BET_PLACEMENT_ENABLE).getValue());
        double maxOdds = Double.parseDouble(settingsRepository.findByKey(MAX_ODDS).getValue());

        for (MatchDTO matchDTO : matchDTOs) {

            Match match = matchRepository.findOne(matchDTO.getId());

            if (match != null) {
                createOrUpdateSetts(matchDTO, match);

                match.setNumberOfSetsToWin(matchDTO.getNumberOfSetsToWin());
                updateMatch(match, matchDTO);

//                log.debug("\nPLACE BET: \n Request for MatchDTO with \n POTENTIAL ERROR : {}", matchDTO);
                if (isBetPlacementEnable) {

                    int currentSetNumber = matchDTO.getCurrentSetNumber();
                    List<GameDTO> gameDTOs = matchDTO.getSetts().get(currentSetNumber - 1).getGames();
                    if (!gameDTOs.isEmpty()) {
                        GameDTO gameDTO = gameDTOs.get(0);

                        double homeOdds = gameDTO.getOddsDTO().getHomeOdds();
                        double awayOdds = gameDTO.getOddsDTO().getAwayOdds();
                        double bookmakersHomeProbability = calculatorService.getRoundedDoubleNumber(awayOdds / (homeOdds + awayOdds));
                        double homeProbability = gameDTO.getHomeProbability();

                        if (homeProbability > bookmakersHomeProbability * multiplier) {
                            if (homeOdds <= maxOdds) {
                                place(match, homeOdds, awayOdds, homeProbability, BetSide.HOME, currentSetNumber);
                            }
                        } else if ((1 - homeProbability) > (1 - bookmakersHomeProbability) * multiplier) {
                            if (awayOdds <= maxOdds) {
                                place(match, awayOdds, homeOdds, 1 - homeProbability, BetSide.AWAY, currentSetNumber);
                            }
                        }
                    }

                }
            }
        }
    }

    private void updateMatch(Match match, MatchDTO matchDTO) {
//        log.debug("\nPLACE BET: Request for Match : {}", match);
        if (match.getStatus() != MatchStatus.LIVE) {
            match.setStatus(MatchStatus.LIVE);
        }
        matchRepository.saveAndFlush(
            match
                .homeScore(matchDTO.getHomeScore())
                .awayScore(matchDTO.getAwayScore())
                .updatedDate(Instant.now()));
//        log.debug("\nPLACE BET: Saved LIVE Match : {}", match);
    }

    private void createOrUpdateSetts(MatchDTO matchDTO, Match match) {
        for (SettDTO settDTO : matchDTO.getSetts()) {
//        log.debug("\nPLACE BET: Request for saving SettDTO : {}", settDTO);
            Sett sett = settRepository.findBySetNumberAndMatchId(settDTO.getSetNumber(), matchDTO.getId());
            Sett savedSett;

            if (sett == null) {
                savedSett = settRepository.save(settMapper.toEntity(settDTO));
            } else {
                savedSett = settRepository.save(sett.homeScore(settDTO.getHomeScore()).awayScore(settDTO.getAwayScore()));
            }

            match.getSetts().add(savedSett);
//        log.debug("\nPLACE BET: Saved Sett : {}", sett);
        }
    }

    private void place(Match match,
                       double odds,
                       double oppositeOdds,
                       double probability,
                       BetSide betSide,
                       int currentSetNumber) {

        double bookmakerOddsWithoutMarge = calculatorService.getRoundedDoubleNumber((odds + oppositeOdds) / oppositeOdds);
        double kellyCoefficient = calculatorService.getRoundedDoubleNumber(
            (bookmakerOddsWithoutMarge * probability - 1) / (bookmakerOddsWithoutMarge - 1));

//        log.debug("\nPLACE BET: KellyCoefficient = {} for Match id :{}", kellyCoefficient, match.getId());

        if (kellyCoefficient != 1.0) {

            Account account = accountRepository.findByType(AccountType.FRONT);

            if (account.getAmount().compareTo(BigDecimal.ZERO) > 0) {

//        log.debug("\nPLACE BET: Account before placing : {} ", account);

                BigDecimal stakeAmount = account.getAmount().multiply(BigDecimal.valueOf(kellyCoefficient));

//        log.debug("\nPLACE BET: stakeAmount = {} ", stakeAmount);

                double bookmakerProbability = calculatorService.getRoundedDoubleNumber(1 / bookmakerOddsWithoutMarge);
                double probabilitiesRatio  = calculatorService.getRoundedDoubleNumber(probability / bookmakerProbability);

                BetDTO betDTO = new BetDTO();
                betDTO.setAmount(stakeAmount);
                betDTO.setOdds(odds);
                betDTO.setBetSide(betSide);
                betDTO.setPlacedDate(Instant.now());
                betDTO.setMatchId(match.getId());
                betDTO.setKellyCoefficient(kellyCoefficient);
                betDTO.setCountedProbability(probability);
                betDTO.setBookmakerProbability(bookmakerProbability);
                betDTO.setSetNumber(currentSetNumber);
                betDTO.setProbabilitiesRatio(probabilitiesRatio);
                betDTO.setMatchScore(match.getHomeScore() + " : " + match.getAwayScore());

                Sett currentSett = match.getSetts().stream().filter(set -> set.getSetNumber().equals(currentSetNumber))
                    .findFirst().get();
                String setScore = currentSett.getHomeScore() + " : " + currentSett.getAwayScore();
                betDTO.setSetScore(setScore);

                if (match.getBets().stream().noneMatch(bet -> bet.getStatus() == BetStatus.OPENED)) {
                    Bet savedBet = saveBet(betDTO, BetStatus.OPENED);

                    saveAccount(account.updatedDate(Instant.now()), account.getAmount().subtract(stakeAmount), account.getPlacedAmount().add(stakeAmount));

                    saveAccountDetail(account.getId(), savedBet.getId(), account.getAmount(), stakeAmount, BigDecimal.ZERO);

                    placeOppositeBet(stakeAmount, oppositeOdds, betDTO);

                } else {
                    Bet existedBet = betRepository.findByMatchIdAndStatusAndOddsAndSetNumberAndSetScore(match.getId(),
                        BetStatus.POTENTIAL, odds, currentSetNumber, setScore);
                    if (existedBet == null) {
                        saveBet(betDTO, BetStatus.POTENTIAL);
                    }
                }
            }
        }
    }

    private void placeOppositeBet(BigDecimal stakeAmount, double oppositeOdds, BetDTO betDTO) {
        boolean isOppositeBetPlacementEnable = Boolean.valueOf(settingsRepository.findByKey(IS_OPPOSITE_BET_PLACEMENT_ENABLE).getValue());
        if (isOppositeBetPlacementEnable) {
            placeBackBet(stakeAmount, oppositeOdds, betDTO);
        }
    }

    private void placeBackBet(BigDecimal stakeAmount, double oppositeOdds, BetDTO betDTO) {

        Account account = accountRepository.findByType(AccountType.BACK);
        BigDecimal backStakeAmount = stakeAmount.divide(BigDecimal.valueOf(oppositeOdds - 1), RoundingMode.HALF_EVEN);

        if (account.getAmount().compareTo(backStakeAmount) > 0) {

            betDTO.setAmount(backStakeAmount);
            betDTO.setOdds(oppositeOdds);
            betDTO.setBetSide(betDTO.getBetSide().getOppositeBetSide());
            betDTO.setKellyCoefficient(null);
            betDTO.setCountedProbability(null);
            betDTO.setBookmakerProbability(null);
            betDTO.setProbabilitiesRatio(0.0);

            Bet savedBet = saveBet(betDTO, BetStatus.OPENED);

            saveAccount(account.updatedDate(Instant.now()), account.getAmount().subtract(backStakeAmount), account.getPlacedAmount().add(backStakeAmount));

            saveAccountDetail(account.getId(), savedBet.getId(), account.getAmount(), backStakeAmount, BigDecimal.ZERO);

        }
    }

    private Bet saveBet(BetDTO betDTO, BetStatus betStatus) {
        betDTO.setStatus(betStatus);
        Bet savedBet = betRepository.saveAndFlush(betMapper.toEntity(betDTO));
//        log.debug("\nPLACE BET: Saved " + betStatus.name() + " Bet : {}", savedBet);
        return savedBet;

    }

    private void saveAccount(Account account, BigDecimal amount, BigDecimal placedAmount) {
        accountRepository.saveAndFlush(account.amount(amount).placedAmount(placedAmount));
        log.debug("\nSaved Account after action: {}", account);
    }

    private void saveAccountDetail(Long accountId,
                                   Long betId,
                                   BigDecimal amount,
                                   BigDecimal placedAmount,
                                   BigDecimal profit) {

        AccountDetailDTO accountDetailDTO = new AccountDetailDTO();
        accountDetailDTO.setAmount(amount);
        accountDetailDTO.setBetId(betId);
        accountDetailDTO.setAccountId(accountId);
        accountDetailDTO.setCreatedDate(Instant.now());
        accountDetailDTO.setPlacedAmount(placedAmount);
        accountDetailDTO.setProfit(profit);

        AccountDetail accountDetail = accountDetailRepository.saveAndFlush(accountDetailMapper.toEntity(accountDetailDTO));

        log.debug("\nSaved AccountDetail : {}", accountDetail);
    }

    private void saveMatches(List<MatchDTO> matchDTOs) {
        for (MatchDTO matchDTO : matchDTOs) {

            matchDTO.setUpdatedDate(Instant.now());

//            log.debug("\nMatchDTO to save : {}", matchDTO);

            Match existedMatch = matchRepository.findOne(matchDTO.getId());
            if (existedMatch != null) {
                Odds existedOdds = oddsRepository.findTopByMatchIdOrderByCheckDateDesc(existedMatch.getId());

                List<OddsDTO> oddsDTOs = matchDTO.getOdds();
                if (!oddsDTOs.isEmpty()) {
                    OddsDTO oddsDTO = oddsDTOs.get(0);

                    if (!oddsDTO.getHomeOdds().equals(existedOdds.getHomeOdds())
                        || !oddsDTO.getAwayOdds().equals(existedOdds.getAwayOdds())) {

                        Odds odds = oddsRepository.saveAndFlush(oddsMapper.toEntity(oddsDTO));
//                        log.debug("\nSaved Odds : {} \n for existed Match : {}", odds, existedMatch);

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
//        log.debug("\nPREPARE TO FINISH: Match : {}", match);
        match.setStatus(MatchStatus.READY_TO_FINISH);
        fillMatchByScores(match);
        setMatchWinner(match);

        matchRepository.save(match);
//        log.debug("\nPREPARE TO FINISH: Saved Match : {}", match);

        matchCache.deleteFromCache(match.getId());
    }

    private void setMatchWinner(Match match) {
        Integer matchHomeScore = match.getHomeScore();
        Integer matchAwayScore = match.getAwayScore();

        if (matchHomeScore.compareTo(matchAwayScore) > 0) {
            match.setWinner(Winner.HOME);
        } else if (matchHomeScore.compareTo(matchAwayScore) < 0) {
            match.setWinner(Winner.AWAY);
        }
    }

    private void fillMatchByScores(Match match) {
        Integer matchHomeScore = 0;
        Integer matchAwayScore = 0;

        List<Sett> setts = match.getSetts().stream().sorted(Comparator.comparingInt(Sett::getSetNumber))
            .collect(Collectors.toList());

        for (Sett sett : setts) {
            Integer setHomeScore = sett.getHomeScore();
            Integer setAwayScore = sett.getAwayScore();
            if (setHomeScore.compareTo(setAwayScore) > 0) {
                matchHomeScore = matchHomeScore + 1;
            } else if (setHomeScore.compareTo(setAwayScore) < 0) {
                matchAwayScore = matchAwayScore + 1;
            }
        }
        match.setHomeScore(matchHomeScore);
        match.setAwayScore(matchAwayScore);
    }

    public void finishMatchesAndSettleBets() {
        List<Match> matchesToFinish = matchRepository.findByStatus(MatchStatus.READY_TO_FINISH);
        for (Match match : matchesToFinish) {

            matchRepository.save(match.status(MatchStatus.FINISHED).updatedDate(Instant.now()));
            log.debug("\nFINISH MATCH: Saved Match : {}", match);

            if (match.isScoreCorrect()) {
                settleBets(betRepository.findByMatchIdAndStatus(match.getId(), BetStatus.OPENED), match.getWinner());
            } else {
                returnBetAmount(betRepository.findByMatchIdAndStatus(match.getId(), BetStatus.OPENED));
            }
        }
    }

    private void returnBetAmount(Set<Bet> bets) {

        for (Bet bet : bets) {
            log.debug("\nRETURN BET: Bet before returning : {}", bet);

            betRepository.saveAndFlush(bet.status(BetStatus.RETURNED).settledDate(Instant.now()));
            log.debug("\nRETURN BET: Bet after returning : {}", bet);

            Account account = bet.getAccountDetails().stream().findFirst().get().getAccount();

            saveAccount(account.updatedDate(Instant.now()), account.getAmount().add(bet.getAmount()), account.getPlacedAmount().subtract(bet.getAmount()));

            saveAccountDetail(account.getId(), bet.getId(), account.getAmount(), BigDecimal.ZERO, bet.getAmount());
        }
    }

    private void settleBets(Set<Bet> bets, Winner winner) {

        for (Bet bet : bets) {
            log.debug("\nSETTLE BET: Bet before settlement : {}", bet);

            BigDecimal amount;
            BigDecimal profit;
            boolean isBetWon = isBetWon(bet, winner.name());
            Account account = bet.getAccountDetails().stream().findFirst().get().getAccount();

            if (isBetWon) {
                profit = bet.getAmount().multiply(BigDecimal.valueOf(bet.getOdds()));
                amount = account.getAmount().add(profit);
            } else {
                profit = bet.getAmount().negate();
                amount = account.getAmount();
            }

            betRepository.saveAndFlush(bet.status(BetStatus.CLOSED).isBetWon(isBetWon)
                .settledDate(Instant.now()).profit(profit));
            log.debug("\nSETTLE BET: Bet after settlement : {}", bet);

            saveAccount(account.updatedDate(Instant.now()), amount, account.getPlacedAmount().subtract(bet.getAmount()));

            saveAccountDetail(account.getId(), bet.getId(), account.getAmount(), BigDecimal.ZERO, profit);
        }
    }

    private boolean isBetWon(Bet bet, String winnerName) {
        return bet.getBetSide().name().equals(winnerName);
    }

    public void sendEmail(){
        try {
            MimeMessage msg = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true);
            helper.setFrom("alexander.nenya@gmail.com");
            helper.setTo("nenya.alex@gmail.com");

            helper.setSubject("Account from: " + Instant.now());

            Account frontAccount = accountRepository.findByType(AccountType.FRONT);
            Account backAccount = accountRepository.findByType(AccountType.BACK);

            helper.setText("FRONT: Amount = " + frontAccount.getAmount() + ", Placed Amount = " + frontAccount.getPlacedAmount() +
                "\n BACK: Amount = " + backAccount.getAmount() + ", Placed Amount = " + backAccount.getPlacedAmount());

//            FileSystemResource file = new FileSystemResource(new File("1.txt"));
//            helper.addAttachment("test_file.txt", file);

            javaMailSender.send(msg);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
