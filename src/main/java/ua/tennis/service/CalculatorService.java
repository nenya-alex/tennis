package ua.tennis.service;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.analysis.solvers.LaguerreSolver;
import org.apache.commons.math3.util.CombinatoricsUtils;
import org.springframework.stereotype.Service;
import ua.tennis.service.dto.GameDTO;
import ua.tennis.service.dto.SettDTO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
public class CalculatorService {

    private static final int NUMBER_OF_GAMES_TO_WIN = 6;
    private static final int SCALE = 3;

    private final GameProbabilityCalculatorForTwoSets gameProbabilityCalculatorForTwoSets;

    private final GameProbabilityCalculatorForThreeSets gameProbabilityCalculatorForThreeSets;

    public CalculatorService(GameProbabilityCalculatorForTwoSets gameProbabilityCalculatorForTwoSets,
                             GameProbabilityCalculatorForThreeSets gameProbabilityCalculatorForThreeSets) {
        this.gameProbabilityCalculatorForTwoSets = gameProbabilityCalculatorForTwoSets;
        this.gameProbabilityCalculatorForThreeSets = gameProbabilityCalculatorForThreeSets;
    }

    public List<SettDTO> getGameProbabilities(double homeMatchProbability, int numberOfSetsToWin) {
        double homeSetProbability = getSetProbability(homeMatchProbability, numberOfSetsToWin);

        return getAllSetsProbabilities(homeSetProbability, numberOfSetsToWin);
    }

    private List<SettDTO> getAllSetsProbabilities(double homeSetProbability, int numberOfSetsToWin) {
        double homeGameProbability = getGameProbability(homeSetProbability);

        Map<String, GameDTO> games = getGames(homeGameProbability);

        List<SettDTO> result = new ArrayList<>();
        for (int i = 0; i <= numberOfSetsToWin; i++) {
            for (int j = 0; j <= numberOfSetsToWin; j++) {
                result.add(getSett(homeSetProbability, games, numberOfSetsToWin, j, i));
            }
        }
        return result;
    }

    private SettDTO getSett(double homeSetProbability,
                            Map<String, GameDTO> games,
                            int numberOfSetsToWin,
                            int homeScore,
                            int awayScore) {

        int homeScoreLeft = numberOfSetsToWin - homeScore;
        int awayScoreLeft = numberOfSetsToWin - awayScore;
        SettDTO sett = new SettDTO(homeScore, awayScore,
            getRoundedDoubleNumber(getWinProbability(homeSetProbability, homeScoreLeft, awayScoreLeft), SCALE));

        if (numberOfSetsToWin == 2) {
            fillSetByGames(games, homeSetProbability, sett, gameProbabilityCalculatorForTwoSets, numberOfSetsToWin);
        }

        if (numberOfSetsToWin == 3) {
            fillSetByGames(games, homeSetProbability, sett, gameProbabilityCalculatorForThreeSets, numberOfSetsToWin);
        }

        return sett;
    }

    private void fillSetByGames(Map<String, GameDTO> games,
                                double homeSetProbability,
                                SettDTO sett,
                                GameProbabilityCalculator gameProbabilityCalculator,
                                int numberOfSetsToWin) {

        List<GameDTO> result = new ArrayList<>();
        int homeScore = sett.getHomeScore();
        int awayScore = sett.getAwayScore();
        if (homeScore < numberOfSetsToWin && awayScore < numberOfSetsToWin) {
            for (int i = 0; i <= NUMBER_OF_GAMES_TO_WIN; i++) {
                for (int j = 0; j <= NUMBER_OF_GAMES_TO_WIN; j++) {
                    GameDTO game = games.get(j + ":" + i);
                    double homeProbability = game.getHomeProbability();
                    double winProbability = 0;
                    if (homeScore == 0 && awayScore == 0) {
                        winProbability = gameProbabilityCalculator.calculateZeroZero(homeSetProbability, homeProbability);
                    }
                    if (homeScore == 1 && awayScore == 0) {
                        winProbability = gameProbabilityCalculator.calculateOneZero(homeSetProbability, homeProbability);
                    }
                    if (homeScore == 0 && awayScore == 1) {
                        winProbability = gameProbabilityCalculator.calculateZeroOne(homeSetProbability, homeProbability);
                    }
                    if (homeScore == 1 && awayScore == 1) {
                        winProbability = gameProbabilityCalculator.calculateOneOne(homeSetProbability, homeProbability);
                    }
                    if (homeScore == 2 && awayScore == 1) {
                        winProbability = gameProbabilityCalculator.calculateTwoOne(homeSetProbability, homeProbability);
                    }
                    if (homeScore == 1 && awayScore == 2) {
                        winProbability = gameProbabilityCalculator.calculateOneTwo(homeSetProbability, homeProbability);
                    }
                    if (homeScore == 2 && awayScore == 2) {
                        winProbability = gameProbabilityCalculator.calculateTwoTwo(homeSetProbability, homeProbability);
                    }
                    if (homeScore == 2 && awayScore == 0) {
                        winProbability = gameProbabilityCalculator.calculateTwoZero(homeSetProbability, homeProbability);
                    }
                    if (homeScore == 0 && awayScore == 2) {
                        winProbability = gameProbabilityCalculator.calculateZeroTwo(homeSetProbability, homeProbability);
                    }
                    result.add(new GameDTO(j, i, getRoundedDoubleNumber(winProbability, SCALE)));
                }
            }
            sett.setGames(result);
        }
    }

    private Map<String, GameDTO> getGames(double homeGameProbability) {
        Map<String, GameDTO> result = new TreeMap<>();
        for (int i = 0; i <= NUMBER_OF_GAMES_TO_WIN; i++) {
            for (int j = 0; j <= NUMBER_OF_GAMES_TO_WIN; j++) {
                result.put(j + ":" + i, getGame(homeGameProbability, NUMBER_OF_GAMES_TO_WIN, j, i));
            }
        }
        return result;
    }

    private GameDTO getGame(double homeGameProbability, int numberOfGamesToWin, int homeScore, int awayScore) {
        int homeScoreLeft = numberOfGamesToWin - homeScore;
        int awayScoreLeft = numberOfGamesToWin - awayScore;
        return new GameDTO(homeScore, awayScore, getRoundedDoubleNumber(getWinProbability(homeGameProbability, homeScoreLeft, awayScoreLeft), SCALE));

    }

    private double getWinProbability(double homeUnitProbability, int homeScoreLeft, int awayScoreLeft) {
        double result = 0;
        int helpNumber = homeScoreLeft + awayScoreLeft - 1;
        for (int i = homeScoreLeft; i <= helpNumber; i++) {
            result += CombinatoricsUtils.binomialCoefficient(helpNumber, i) * Math.pow(homeUnitProbability, i) * Math.pow(1 - homeUnitProbability, helpNumber - i);
        }
        return result;
    }

    private double getSetProbability(double homeMatchProbability, int numberOfSetsToWin) {
        double root = 0;
        LaguerreSolver laguerreSolver = new LaguerreSolver();
        if (numberOfSetsToWin == 2) {
            PolynomialFunction polynomial = new PolynomialFunction(new double[]{-homeMatchProbability, 0, 3, -2});
            root = laguerreSolver.solve(100, polynomial, 0, 1);
        }
        if (numberOfSetsToWin == 3) {
            PolynomialFunction polynomial = new PolynomialFunction(new double[]{-homeMatchProbability, 0, 0,
                10, -15, 6});
            root = laguerreSolver.solve(100, polynomial, 0, 1);
        }
        return getRoundedDoubleNumber(root, 3);
    }

    private double getGameProbability(double homeSetProbability) {
        LaguerreSolver laguerreSolver = new LaguerreSolver();
        PolynomialFunction polynomial = new PolynomialFunction(new double[]{-homeSetProbability,
            0, 0, 0, 0, 0, 462, -1980, 3465, -3080, 1386, -252});
        double root = laguerreSolver.solve(100, polynomial, 0, 1);
        return getRoundedDoubleNumber(root, 3);
    }

    public double getRoundedDoubleNumber(double number, int scale) {
        return new BigDecimal(number).setScale(scale, RoundingMode.HALF_EVEN).doubleValue();
    }

    public int getSCALE() {
        return SCALE;
    }
}
