package com.teya.tinyledger.ledger.infrastructure.rest.request;

import com.teya.tinyledger.ledger.application.deposit.DepositCommand;
import com.teya.tinyledger.ledger.application.exception.InvalidAmountException;
import com.teya.tinyledger.ledger.domain.account.AccountId;
import com.teya.tinyledger.ledger.domain.amount.Amount;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record DepositRequest(
    @NotNull(message = "Amount is required")
    BigDecimal amount,
    String referenceId
) {

    public DepositCommand toCommand(AccountId accountId) {
        try {
            return new DepositCommand(accountId, Amount.of(amount), referenceId);
        } catch (IllegalArgumentException exception) {
            throw new InvalidAmountException(exception.getMessage());
        }
    }
}
