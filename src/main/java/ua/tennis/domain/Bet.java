package ua.tennis.domain;

import ua.tennis.domain.enumeration.BetSide;
import ua.tennis.domain.enumeration.BetStatus;
import ua.tennis.domain.enumeration.MatchWinner;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "bet")
public class Bet implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "amount", precision = 21, scale = 2)
    private BigDecimal amount;

    @Column(name = "odds")
    private Double odds;

    @Enumerated(EnumType.STRING)
    @Column(name = "bet_side")
    private BetSide betSide;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private BetStatus status;

    @Column(name = "placed_date")
    private Instant placedDate;

    @ManyToOne
    @JoinColumn(unique = true)
    private Match match;

    @OneToMany(mappedBy = "bet")
    private Set<AccountDetail> accountDetails = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Bet amount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Double getOdds() {
        return odds;
    }

    public Bet odds(Double odds) {
        this.odds = odds;
        return this;
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

    public Bet status(BetStatus status) {
        this.status = status;
        return this;
    }

    public void setStatus(BetStatus status) {
        this.status = status;
    }

    public Instant getPlacedDate() {
        return placedDate;
    }

    public Bet placedDate(Instant placedDate) {
        this.placedDate = placedDate;
        return this;
    }

    public void setPlacedDate(Instant placedDate) {
        this.placedDate = placedDate;
    }

    public Match getMatch() {
        return match;
    }

    public Bet match(Match match) {
        this.match = match;
        return this;
    }

    public void setMatch(Match match) {
        this.match = match;
    }

    public Set<AccountDetail> getAccountDetails() {
        return accountDetails;
    }

    public void setAccountDetails(Set<AccountDetail> accountDetails) {
        this.accountDetails = accountDetails;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Bet)) {
            return false;
        }
        return id != null && id.equals(((Bet) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Bet{" +
            "id=" + getId() +
            ", amount=" + getAmount() +
            ", odds=" + getOdds() +
            ", betSide='" + getBetSide() + "'" +
            ", status='" + getStatus() + "'" +
            ", placedDate='" + getPlacedDate() + "'" +
            "}";
    }
}
