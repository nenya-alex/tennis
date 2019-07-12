package ua.tennis.service;

public interface GameProbabilityCalculator {

    double calculateZeroZero(double homeSetProbability, double homeGameProbability);

    double calculateOneZero(double homeSetProbability, double homeGameProbability);

    double calculateZeroOne(double homeSetProbability, double homeGameProbability);

    double calculateOneOne(double homeSetProbability, double homeGameProbability);

    double calculateTwoOne(double homeSetProbability, double homeGameProbability);

    double calculateOneTwo(double homeSetProbability, double homeGameProbability);

    double calculateTwoTwo(double homeSetProbability, double homeGameProbability);

    double calculateTwoZero(double homeSetProbability, double homeGameProbability);

    double calculateZeroTwo(double homeSetProbability, double homeGameProbability);
}
