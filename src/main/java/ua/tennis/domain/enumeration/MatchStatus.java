package ua.tennis.domain.enumeration;

public enum MatchStatus {

    UPCOMING("Upcoming"),
    NOT_STARTED("Not Started"),
    LIVE("Upcoming"),
    READY_TO_FINISH("Ready to finish"),
    FINISHED("Finished"),
    SUSPENDED("Suspended");

    private String name;

    MatchStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
