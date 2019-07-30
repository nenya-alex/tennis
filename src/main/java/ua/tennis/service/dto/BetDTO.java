package ua.tennis.service.dto;

import ua.tennis.domain.enumeration.BetSide;
import ua.tennis.domain.enumeration.BetStatus;
import ua.tennis.domain.enumeration.Winner;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class BetDTO implements Serializable {

    private Long id;

    private BigDecimal amount;

    private Double odds;

    private BetSide betSide;

    private BetStatus status;

    private Instant placedDate;

    private Long matchId;

    private Winner winner;

    private Double countedProbability;

    private Double bookmakerProbability;

    private Double kellyCoefficient;

    private Instant settledDate;

    private Set<AccountDetailDTO> accountDetailDTOs = new HashSet<>();

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

    public BetSide getBetSide() {
        return betSide;
    }

    public void setBetSide(BetSide betSide) {
        this.betSide = betSide;
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

    public Set<AccountDetailDTO> getAccountDetailDTOs() {
        return accountDetailDTOs;
    }

    public void setAccountDetailDTOs(Set<AccountDetailDTO> accountDetailDTOs) {
        this.accountDetailDTOs = accountDetailDTOs;
    }

    public Winner getWinner() {
        return winner;
    }

    public void setWinner(Winner winner) {
        this.winner = winner;
    }

    public Double getCountedProbability() {
        return countedProbability;
    }

    public void setCountedProbability(Double countedProbability) {
        this.countedProbability = countedProbability;
    }

    public Double getBookmakerProbability() {
        return bookmakerProbability;
    }

    public void setBookmakerProbability(Double bookmakerProbability) {
        this.bookmakerProbability = bookmakerProbability;
    }

    public Double getKellyCoefficient() {
        return kellyCoefficient;
    }

    public void setKellyCoefficient(Double kellyCoefficient) {
        this.kellyCoefficient = kellyCoefficient;
    }

    public Instant getSettledDate() {
        return settledDate;
    }

    public void setSettledDate(Instant settledDate) {
        this.settledDate = settledDate;
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
            ", status='" + getStatus() + "'" +
            ", placedDate='" + getPlacedDate() + "'" +
            ", match=" + getMatchId() +
            "}";
    }
}
