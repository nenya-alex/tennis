package ua.tennis.domain;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A Odds.
 */
@Entity
@Table(name = "odds")
public class Odds implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "home_odds")
    private Double homeOdds;

    @Column(name = "away_odds")
    private Double awayOdds;

    @Column(name = "check_date")
    private Instant checkDate;

    @ManyToOne
    private Match match;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getHomeOdds() {
        return homeOdds;
    }

    public Odds homeOdds(Double homeOdds) {
        this.homeOdds = homeOdds;
        return this;
    }

    public void setHomeOdds(Double homeOdds) {
        this.homeOdds = homeOdds;
    }

    public Double getAwayOdds() {
        return awayOdds;
    }

    public Odds awayOdds(Double awayOdds) {
        this.awayOdds = awayOdds;
        return this;
    }

    public void setAwayOdds(Double awayOdds) {
        this.awayOdds = awayOdds;
    }

    public Instant getCheckDate() {
        return checkDate;
    }

    public Odds checkDate(Instant checkDate) {
        this.checkDate = checkDate;
        return this;
    }

    public void setCheckDate(Instant checkDate) {
        this.checkDate = checkDate;
    }

    public Match getMatch() {
        return match;
    }

    public Odds match(Match match) {
        this.match = match;
        return this;
    }

    public void setMatch(Match match) {
        this.match = match;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Odds)) {
            return false;
        }
        return id != null && id.equals(((Odds) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Odds{" +
            "id=" + getId() +
            ", homeOdds=" + getHomeOdds() +
            ", awayOdds=" + getAwayOdds() +
            ", checkDate='" + getCheckDate() + "'" +
            "}";
    }
}
