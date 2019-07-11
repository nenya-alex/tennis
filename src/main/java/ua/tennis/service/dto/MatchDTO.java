package ua.tennis.service.dto;


import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the Match entity.
 */
public class MatchDTO implements Serializable {

    private Long id;

    private String identifier;

    private String homeName;

    private String awayName;

    private Integer homeScore;

    private Integer awayScore;

    private Instant startDate;

    private String name;

    private Instant openDate;

    private Integer prematchEventId;

    private double homeOdds;

    private double awayOdds;

    private Instant checkedDate;

    private String leagueName;

    private Long leagueId;

    public MatchDTO() {
    }

    public MatchDTO(Long id,
                    String identifier,
                    Integer prematchEventId,
                    String name,
                    double homeOdds,
                    double awayOdds,
                    Instant openDate,
                    Instant startDate,
                    String homeName,
                    String awayName,
                    Instant checkedDate,
                    String leagueName,
                    Long leagueId) {
        this.id = id;
        this.identifier = identifier;
        this.homeName = homeName;
        this.awayName = awayName;
        this.startDate = startDate;
        this.name = name;
        this.openDate = openDate;
        this.prematchEventId = prematchEventId;
        this.homeOdds = homeOdds;
        this.awayOdds = awayOdds;
        this.checkedDate = checkedDate;
        this.leagueName = leagueName;
        this.leagueId = leagueId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getHomeName() {
        return homeName;
    }

    public void setHomeName(String homeName) {
        this.homeName = homeName;
    }

    public String getAwayName() {
        return awayName;
    }

    public void setAwayName(String awayName) {
        this.awayName = awayName;
    }

    public Integer getHomeScore() {
        return homeScore;
    }

    public void setHomeScore(Integer homeScore) {
        this.homeScore = homeScore;
    }

    public Integer getAwayScore() {
        return awayScore;
    }

    public void setAwayScore(Integer awayScore) {
        this.awayScore = awayScore;
    }

    public Instant getStartDate() {
        return startDate;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instant getOpenDate() {
        return openDate;
    }

    public void setOpenDate(Instant openDate) {
        this.openDate = openDate;
    }

    public Integer getPrematchEventId() {
        return prematchEventId;
    }

    public void setPrematchEventId(Integer prematchEventId) {
        this.prematchEventId = prematchEventId;
    }

    public double getHomeOdds() {
        return homeOdds;
    }

    public void setHomeOdds(double homeOdds) {
        this.homeOdds = homeOdds;
    }

    public double getAwayOdds() {
        return awayOdds;
    }

    public void setAwayOdds(double awayOdds) {
        this.awayOdds = awayOdds;
    }

    public Instant getCheckedDate() {
        return checkedDate;
    }

    public void setCheckedDate(Instant checkedDate) {
        this.checkedDate = checkedDate;
    }

    public String getLeagueName() {
        return leagueName;
    }

    public void setLeagueName(String leagueName) {
        this.leagueName = leagueName;
    }

    public Long getLeagueId() {
        return leagueId;
    }

    public void setLeagueId(Long leagueId) {
        this.leagueId = leagueId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MatchDTO matchDTO = (MatchDTO) o;
        if(matchDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), matchDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "MatchDTO{" +
            "id=" + getId() +
            ", identifier='" + getIdentifier() + "'" +
            ", homeName='" + getHomeName() + "'" +
            ", awayName='" + getAwayName() + "'" +
            ", homeScore=" + getHomeScore() +
            ", awayScore=" + getAwayScore() +
            ", startDate='" + getStartDate() + "'" +
            ", name='" + getName() + "'" +
            ", openDate='" + getOpenDate() + "'" +
            "}";
    }
}
