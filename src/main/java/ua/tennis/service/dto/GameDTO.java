package ua.tennis.service.dto;


import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A DTO for the Game entity.
 */
public class GameDTO implements Serializable {

    private Long id;

    private BigDecimal probabilityHome;

    private BigDecimal probabilityAway;

    private Long settId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getProbabilityHome() {
        return probabilityHome;
    }

    public void setProbabilityHome(BigDecimal probabilityHome) {
        this.probabilityHome = probabilityHome;
    }

    public BigDecimal getProbabilityAway() {
        return probabilityAway;
    }

    public void setProbabilityAway(BigDecimal probabilityAway) {
        this.probabilityAway = probabilityAway;
    }

    public Long getSettId() {
        return settId;
    }

    public void setSettId(Long settId) {
        this.settId = settId;
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
            ", probabilityHome=" + getProbabilityHome() +
            ", probabilityAway=" + getProbabilityAway() +
            "}";
    }
}
