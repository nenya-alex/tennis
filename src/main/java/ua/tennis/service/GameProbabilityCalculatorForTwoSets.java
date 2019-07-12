package ua.tennis.service;

public class GameProbabilityCalculatorForTwoSets implements GameProbabilityCalculator {

    @Override
    public double calculateZeroZero(double homeSetProbability, double homeGameProbability) {
        double homeSetProbabilityPowerTwo = Math.pow(homeSetProbability, 2);
        return homeSetProbabilityPowerTwo + 2 * homeGameProbability * (homeSetProbability - homeSetProbabilityPowerTwo);
    }

    @Override
    public double calculateOneZero(double homeSetProbability, double homeGameProbability) {
        return homeSetProbability + (1 - homeSetProbability) * homeGameProbability;
    }

    @Override
    public double calculateZeroOne(double homeSetProbability, double homeGameProbability) {
        return homeSetProbability * homeGameProbability;
    }

    @Override
    public double calculateOneOne(double homeSetProbability, double homeGameProbability) {
        return homeGameProbability;
    }

    @Override
    public double calculateTwoOne(double homeSetProbability, double homeGameProbability) {
        return 0;
    }

    @Override
    public double calculateOneTwo(double homeSetProbability, double homeGameProbability) {
        return 0;
    }

    @Override
    public double calculateTwoTwo(double homeSetProbability, double homeGameProbability) {
        return 0;
    }

    @Override
    public double calculateTwoZero(double homeSetProbability, double homeGameProbability) {
        return 0;
    }

    @Override
    public double calculateZeroTwo(double homeSetProbability, double homeGameProbability) {
        return 0;
    }

}
