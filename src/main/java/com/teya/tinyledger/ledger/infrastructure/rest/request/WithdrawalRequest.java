package com.teya.tinyledger.ledger.infrastructure.rest.request;

import com.teya.tinyledger.ledger.common.exception.InvalidAmountException;
import com.teya.tinyledger.ledger.application.withdrawal.WithdrawCommand;
import com.teya.tinyledger.ledger.domain.account.AccountId;
import com.teya.tinyledger.ledger.domain.amount.Amount;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record WithdrawalRequest(
    @NotNull(message = "Amount is required")
    BigDecimal amount,
    String referenceId
) {

    public WithdrawCommand toCommand(AccountId accountId) {
        try {
            return new WithdrawCommand(accountId, Amount.of(amount), referenceId);
        } catch (IllegalArgumentException exception) {
            throw new InvalidAmountException(exception.getMessage());
        }
    }
}
