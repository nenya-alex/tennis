package ua.tennis.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A Game.
 */
@Entity
@Table(name = "game")
public class Game implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "probability_home", precision=10, scale=2)
    private BigDecimal probabilityHome;

    @Column(name = "probability_away", precision=10, scale=2)
    private BigDecimal probabilityAway;

    @ManyToOne
    @JsonIgnore
    private Sett sett;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getProbabilityHome() {
        return probabilityHome;
    }

    public Game probabilityHome(BigDecimal probabilityHome) {
        this.probabilityHome = probabilityHome;
        return this;
    }

    public void setProbabilityHome(BigDecimal probabilityHome) {
        this.probabilityHome = probabilityHome;
    }

    public BigDecimal getProbabilityAway() {
        return probabilityAway;
    }

    public Game probabilityAway(BigDecimal probabilityAway) {
        this.probabilityAway = probabilityAway;
        return this;
    }

    public void setProbabilityAway(BigDecimal probabilityAway) {
        this.probabilityAway = probabilityAway;
    }

    public Sett getSett() {
        return sett;
    }

    public Game sett(Sett sett) {
        this.sett = sett;
        return this;
    }

    public void setSett(Sett sett) {
        this.sett = sett;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Game game = (Game) o;
        if (game.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), game.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Game{" +
            "id=" + getId() +
            ", probabilityHome=" + getProbabilityHome() +
            ", probabilityAway=" + getProbabilityAway() +
            "}";
    }
}
