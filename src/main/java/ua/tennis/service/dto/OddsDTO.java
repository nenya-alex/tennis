package ua.tennis.service.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link ua.tennis.domain.Odds} entity.
 */
public class OddsDTO implements Serializable {

    private Long id;

    private Double homeOdds;

    private Double awayOdds;

    private Instant checkDate;

    private Long matchId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Instant getCheckDate() {
        return checkDate;
    }

    public void setCheckDate(Instant checkDate) {
        this.checkDate = checkDate;
    }

    public Long getMatchId() {
        return matchId;
    }

    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        OddsDTO oddsDTO = (OddsDTO) o;
        if (oddsDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), oddsDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "OddsDTO{" +
            "id=" + getId() +
            ", homeOdds=" + getHomeOdds() +
            ", awayOdds=" + getAwayOdds() +
            ", checkDate='" + getCheckDate() + "'" +
            ", match=" + getMatchId() +
            "}";
    }
}
