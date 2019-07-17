package ua.tennis.service.dto;


import java.io.Serializable;
import java.util.Objects;

public class GameDTO implements Serializable {

    private Long id;

    private Integer homeScore;

    private Integer awayScore;

    private Double homeProbability;

    private Double awayProbability;

    private Long settId;

    private OddsDTO oddsDTO;

    public GameDTO(Integer homeScore, Integer awayScore, Double homeProbability) {
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.homeProbability = homeProbability;
    }

    public GameDTO() {
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

    public Long getSettId() {
        return settId;
    }

    public void setSettId(Long settId) {
        this.settId = settId;
    }

    public OddsDTO getOddsDTO() {
        return oddsDTO;
    }

    public void setOddsDTO(OddsDTO oddsDTO) {
        this.oddsDTO = oddsDTO;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        GameDTO gameDTO = (GameDTO) o;
        if(gameDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), gameDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "GameDTO{" +
            "id=" + getId() +
            ", homeScore=" + getHomeScore() +
            ", awayScore=" + getAwayScore() +
            ", homeProbability=" + getHomeProbability() +
            ", awayProbability=" + getAwayProbability() +
            "}";
    }
}
