package com.teya.tinyledger.ledger.application.deposit;

import com.teya.tinyledger.ledger.domain.account.AccountId;
import com.teya.tinyledger.ledger.domain.amount.Amount;

public record DepositCommand(AccountId accountId, Amount amount, String referenceId) {

    public DepositCommand {
        if (accountId == null) {
            throw new IllegalArgumentException("Account ID must not be null");
        }
        if (amount == null) {
            throw new IllegalArgumentException("Amount must not be null");
        }
    }
}
