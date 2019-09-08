package ua.tennis.domain.enumeration;

public enum BetSide {
    HOME,
    AWAY;

    public BetSide getOppositeBetSide() {
        if (this == HOME) {
            return AWAY;
        } else {
            return HOME;
        }
    }
}
