package ua.tennis.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

public class AccountDTO implements Serializable {

    private Long id;

    private BigDecimal amount;

    private Long betId;

    private Instant updatedDate;

    private BigDecimal placedAmount;

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

    public Long getBetId() {
        return betId;
    }

    public void setBetId(Long betId) {
        this.betId = betId;
    }

    public Instant getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Instant updatedDate) {
        this.updatedDate = updatedDate;
    }

    public BigDecimal getPlacedAmount() {
        return placedAmount;
    }

    public void setPlacedAmount(BigDecimal placedAmount) {
        this.placedAmount = placedAmount;
    }

    public Set<AccountDetailDTO> getAccountDetailDTOs() {
        return accountDetailDTOs;
    }

    public void setAccountDetailDTOs(Set<AccountDetailDTO> accountDetailDTOs) {
        this.accountDetailDTOs = accountDetailDTOs;
    }
}