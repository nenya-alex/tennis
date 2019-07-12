package ua.tennis.service.dto;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A DTO for the Sett entity.
 */
public class SettDTO implements Serializable {

    private Long id;

    private Integer homeScore;

    private Integer awayScore;

    private Double homeOdds;

    private Double awayOdds;

    private Double homeProbability;

    private Double awayProbability;

    private Long matchId;

    private List<GameDTO> games = new ArrayList<>();

    public SettDTO() {
    }

    public SettDTO(Integer homeScore,
                   Integer awayScore,
                   Double homeOdds,
                   Double awayOdds,
                   Double homeProbability,
                   Long matchId) {
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.homeOdds = homeOdds;
        this.awayOdds = awayOdds;
        this.homeProbability = homeProbability;
        this.matchId = matchId;
    }

    public SettDTO(Integer homeScore, Integer awayScore, double homeProbability) {
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.homeProbability = homeProbability;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Double getHomeProbability() {
        return homeProbability;
    }

    public void setHomeProbability(Double homeProbability) {
        this.homeProbability = homeProbability;
    }

    public Double getAwayProbability() {
        return awayProbability;
    }

    public void setAwayProbability(Double awayProbability) {
        this.awayProbability = awayProbability;
    }

    public Long getMatchId() {
        return matchId;
    }

    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }

    public Double getHomeOdds() {
        return homeOdds;
    }

    public void setHomeOdds(Double homeOdds) {
        this.homeOdds = homeOdds;
    }

    public Double getAwayOdds() {
        return awayOdds;
    }

    public void setAwayOdds(Double awayOdds) {
        this.awayOdds = awayOdds;
    }

    public List<GameDTO> getGames() {
        return games;
    }

    public void setGames(List<GameDTO> games) {
        this.games = games;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SettDTO settDTO = (SettDTO) o;
        if(settDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), settDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "SettDTO{" +
            "id=" + getId() +
            ", homeScore=" + getHomeScore() +
            ", awayScore=" + getAwayScore() +
            ", homeProbability=" + getHomeProbability() +
            ", awayProbability=" + getAwayProbability() +
            "}";
    }
}
