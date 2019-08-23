package ua.tennis.domain;

import ua.tennis.domain.enumeration.BetSide;
import ua.tennis.domain.enumeration.BetStatus;
import ua.tennis.domain.enumeration.Winner;

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

    @Column(name = "is_bet_won")
    private boolean isBetWon;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private BetStatus status;

    @Column(name = "counted_probability")
    private Double countedProbability;

    @Column(name = "bookmaker_probability")
    private Double bookmakerProbability;

    @Column(name = "kelly_coefficient")
    private Double kellyCoefficient;

    @Column(name = "placed_date")
    private Instant placedDate;

    @Column(name = "settled_date")
    private Instant settledDate;

    @ManyToOne
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

    public boolean getIsBetWon() {
        return isBetWon;
    }

    public void setIsBetWon(boolean isBetWon) {
        this.isBetWon = isBetWon;
    }

    public Bet isBetWon(boolean isBetWon) {
        this.isBetWon = isBetWon;
        return this;
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

    public Bet settledDate(Instant settledDate) {
        this.settledDate = settledDate;
        return this;
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
        return "\n" +"Bet{" +
            "id=" + id + "\n" +
            ", amount=" + amount + "\n" +
            ", odds=" + odds + "\n" +
            ", betSide=" + betSide + "\n" +
            ", status=" + status + "\n" +
            ", placedDate=" + placedDate + "\n" +
            ", match=" + match + "\n" +
            ", accountDetails=" + accountDetails +
            '}';
    }
}
