package ua.tennis.service;

import org.springframework.stereotype.Component;

@Component
public class GameProbabilityCalculatorForThreeSets implements GameProbabilityCalculator {

    @Override
    public double calculateZeroZero(double homeSetProbability, double homeGameProbability) {
        return homeGameProbability * Math.pow(homeSetProbability, 2) +
            3 * (1 - homeGameProbability) * (1 - homeSetProbability) * Math.pow(homeSetProbability, 3) +
            2 * homeGameProbability * (1 - homeSetProbability) * Math.pow(homeSetProbability, 2) +
            3 * homeGameProbability * Math.pow(1 - homeSetProbability, 2) * Math.pow(homeSetProbability, 2);
    }

    @Override
    public double calculateOneZero(double homeSetProbability, double homeGameProbability) {
        return homeGameProbability * homeSetProbability +
            (1 - homeGameProbability) * Math.pow(homeSetProbability, 2) +
            homeGameProbability * (1 - homeSetProbability) * homeSetProbability +
            homeGameProbability * Math.pow(1 - homeSetProbability, 2) * homeSetProbability;
    }

    @Override
    public double calculateZeroOne(double homeSetProbability, double homeGameProbability) {
        return homeGameProbability * Math.pow(homeSetProbability, 2) +
            (1 - homeGameProbability) * Math.pow(homeSetProbability, 3) +
            2 * homeGameProbability * (1 - homeSetProbability) * Math.pow(homeSetProbability, 2);
    }

    @Override
    public double calculateOneOne(double homeSetProbability, double homeGameProbability) {
        double homeSetProbabilityPowerTwo = Math.pow(homeSetProbability, 2);
        return homeSetProbabilityPowerTwo + 2 * homeGameProbability * (homeSetProbability - homeSetProbabilityPowerTwo);
    }

    @Override
    public double calculateTwoOne(double homeSetProbability, double homeGameProbability) {
        return homeSetProbability + (1 - homeSetProbability) * homeGameProbability;
    }

    @Override
    public double calculateOneTwo(double homeSetProbability, double homeGameProbability) {
        return homeSetProbability * homeGameProbability;
    }

    @Override
    public double calculateTwoTwo(double homeSetProbability, double homeGameProbability) {
        return homeGameProbability;
    }

    @Override
    public double calculateTwoZero(double homeSetProbability, double homeGameProbability) {
        return homeGameProbability + (1 - homeGameProbability) * homeSetProbability +
            (1 - homeGameProbability) * (1 - homeSetProbability) * homeSetProbability;
    }

    @Override
    public double calculateZeroTwo(double homeSetProbability, double homeGameProbability) {
        return homeGameProbability * Math.pow(homeSetProbability, 2);
    }
}
