package ua.tennis.service.dto;

import ua.tennis.domain.enumeration.BetStatus;
import ua.tennis.domain.enumeration.MatchWinner;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link ua.tennis.domain.Bet} entity.
 */
public class BetDTO implements Serializable {

    private Long id;

    private BigDecimal amount;

    private Double odds;

    private MatchWinner placedBetMatchWinner;

    private BetStatus status;

    private Instant placedDate;


    private Long matchId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Double getOdds() {
        return odds;
    }

    public void setOdds(Double odds) {
        this.odds = odds;
    }

    public MatchWinner getPlacedBetMatchWinner() {
        return placedBetMatchWinner;
    }

    public void setPlacedBetMatchWinner(MatchWinner placedBetMatchWinner) {
        this.placedBetMatchWinner = placedBetMatchWinner;
    }

    public BetStatus getStatus() {
        return status;
    }

    public void setStatus(BetStatus status) {
        this.status = status;
    }

    public Instant getPlacedDate() {
        return placedDate;
    }

    public void setPlacedDate(Instant placedDate) {
        this.placedDate = placedDate;
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

        BetDTO betDTO = (BetDTO) o;
        if (betDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), betDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "BetDTO{" +
            "id=" + getId() +
            ", amount=" + getAmount() +
            ", odds=" + getOdds() +
            ", placedBetMatchWinner='" + getPlacedBetMatchWinner() + "'" +
            ", status='" + getStatus() + "'" +
            ", placedDate='" + getPlacedDate() + "'" +
            ", match=" + getMatchId() +
            "}";
    }
}
