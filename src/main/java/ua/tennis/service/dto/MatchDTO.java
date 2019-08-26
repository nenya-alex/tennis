package ua.tennis.service.dto;

import ua.tennis.domain.enumeration.MatchStatus;
import ua.tennis.domain.enumeration.Winner;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MatchDTO implements Serializable {

    private Long id;

    private Integer homeScore;

    private Integer awayScore;

    private Instant startDate;

    private String name;

    private String leagueName;

    private Instant updatedDate;

    private String gameMode;

    private Integer numberOfSetsToWin;

    private String period;

    private int currentSetNumber;

    private double homeMatchProbability;

    private MatchStatus status;

    private Winner winner;

    private List<OddsDTO> odds = new ArrayList<>();

    private List<SettDTO> setts = new ArrayList<>();

    public MatchDTO() {
    }

    public MatchDTO(Long id,
                    String name,
                    Instant startDate,
                    String leagueName;) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.leagueName = leagueName;
    }

    public MatchDTO(double homeMatchProbability, List<SettDTO> setts) {
        this.homeMatchProbability = homeMatchProbability;
        this.setts = setts;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getLeagueName() {
        return leagueName;
    }

    public void setLeagueName(String leagueName) {
        this.leagueName = leagueName;
    }

    public Instant getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Instant updatedDate) {
        this.updatedDate = updatedDate;
    }

    public String getGameMode() {
        return gameMode;
    }

    public void setGameMode(String gameMode) {
        this.gameMode = gameMode;
    }

    public Integer getNumberOfSetsToWin() {
        return numberOfSetsToWin;
    }

    public void setNumberOfSetsToWin(Integer numberOfSetsToWin) {
        this.numberOfSetsToWin = numberOfSetsToWin;
    }

    public List<OddsDTO> getOdds() {
        return odds;
    }

    public void setOdds(List<OddsDTO> odds) {
        this.odds = odds;
    }

    public List<SettDTO> getSetts() {
        return setts;
    }

    public void setSetts(List<SettDTO> setts) {
        this.setts = setts;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public double getHomeMatchProbability() {
        return homeMatchProbability;
    }

    public void setHomeMatchProbability(double homeMatchProbability) {
        this.homeMatchProbability = homeMatchProbability;
    }

    public MatchStatus getStatus() {
        return status;
    }

    public void setStatus(MatchStatus status) {
        this.status = status;
    }

    public MatchDTO status(MatchStatus status) {
        this.status = status;
        return this;
    }

    public Winner getWinner() {
        return winner;
    }

    public void setWinner(Winner winner) {
        this.winner = winner;
    }

    public int getCurrentSetNumber() {
        return currentSetNumber;
    }

    public void setCurrentSetNumber(int currentSetNumber) {
        this.currentSetNumber = currentSetNumber;
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
        return "\n" +"MatchDTO{" +
            "id=" + id +"\n" +
            ", name='" + name + '\'' +"\n" +
            ", homeScore=" + homeScore +"\n" +
            ", awayScore=" + awayScore +"\n" +
            ", startDate=" + startDate +"\n" +
            ", leagueName='" + leagueName + '\'' +"\n" +
            ", updatedDate=" + updatedDate + "\n" +
            ", homeMatchProbability=" + homeMatchProbability +"\n" +
            ", status=" + status +"\n" +
            ", winner=" + winner + "\n" +
            ", currentSetNumber=" + currentSetNumber + "\n" +
            ", odds=" + odds + "\n" +
            ", setts=" + setts +
            '}';
    }
}
