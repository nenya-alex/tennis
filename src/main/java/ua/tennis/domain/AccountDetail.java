package ua.tennis.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "account_detail")
public class AccountDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "amount", precision = 21, scale = 2)
    private BigDecimal amount;

    @Column(name = "placed_amount", precision = 21, scale = 2)
    private BigDecimal placedAmount;

    @Column(name = "profit", precision = 21, scale = 2)
    private BigDecimal profit;

    @Column(name = "created_date")
    private Instant createdDate;

    @ManyToOne
    private Bet bet;

    @ManyToOne
    private Account account;

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

    public Bet getBet() {
        return bet;
    }

    public void setBet(Bet bet) {
        this.bet = bet;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public BigDecimal getPlacedAmount() {
        return placedAmount;
    }

    public void setPlacedAmount(BigDecimal placedAmount) {
        this.placedAmount = placedAmount;
    }

    public BigDecimal getProfit() {
        return profit;
    }

    public void setProfit(BigDecimal profit) {
        this.profit = profit;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountDetail that = (AccountDetail) o;
        return Objects.equals(id, that.id) &&
            Objects.equals(amount, that.amount) &&
            Objects.equals(placedAmount, that.placedAmount) &&
            Objects.equals(createdDate, that.createdDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, amount, placedAmount, createdDate);
    }

    @Override
    public String toString() {
        return "\n" +"AccountDetail{" +
            "id=" + id +
            ", amount=" + amount +"\n" +
            ", placedAmount=" + placedAmount +"\n" +
            ", createdDate=" + createdDate +"\n" +
            ", bet=" + bet +
            '}';
    }
}
