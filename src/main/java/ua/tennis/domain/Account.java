package ua.tennis.domain;

import ua.tennis.domain.enumeration.AccountType;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "account")
public class Account implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "amount", precision = 21, scale = 2)
    private BigDecimal amount;

    @Column(name = "placed_amount", precision = 21, scale = 2)
    private BigDecimal placedAmount;

    @Column(name = "updated_date")
    private Instant updatedDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private AccountType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AccountStatus status;

    @OneToMany(mappedBy = "account")
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

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Account amount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    public Instant getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Instant updatedDate) {
        this.updatedDate = updatedDate;
    }

    public Account updatedDate(Instant updatedDate) {
        this.updatedDate = updatedDate;
        return this;
    }

    public BigDecimal getPlacedAmount() {
        return placedAmount;
    }

    public void setPlacedAmount(BigDecimal placedAmount) {
        this.placedAmount = placedAmount;
    }

    public Account placedAmount(BigDecimal placedAmount) {
        this.placedAmount = placedAmount;
        return this;
    }

    public Set<AccountDetail> getAccountDetails() {
        return accountDetails;
    }

    public void setAccountDetails(Set<AccountDetail> accountDetails) {
        this.accountDetails = accountDetails;
    }

    public AccountType getType() {
        return type;
    }

    public void setType(AccountType type) {
        this.type = type;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(id, account.id) &&
            Objects.equals(amount, account.amount) &&
            Objects.equals(placedAmount, account.placedAmount) &&
            Objects.equals(updatedDate, account.updatedDate) &&
            Objects.equals(accountDetails, account.accountDetails);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, amount, placedAmount, updatedDate, accountDetails);
    }

    @Override
    public String toString() {
        return "\n" +"Account{" +
            "id=" + id +"\n" +
            ", amount=" + amount +"\n" +
            ", placedAmount=" + placedAmount +"\n" +
            ", updatedDate=" + updatedDate +
            '}';
    }
}
