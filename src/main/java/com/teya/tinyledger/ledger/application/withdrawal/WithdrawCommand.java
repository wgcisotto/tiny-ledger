package com.teya.tinyledger.ledger.application.withdrawal;

import com.teya.tinyledger.ledger.domain.account.AccountId;
import com.teya.tinyledger.ledger.domain.amount.Amount;

public record WithdrawCommand(AccountId accountId, Amount amount, String referenceId) {

    public WithdrawCommand {
        if (accountId == null) {
            throw new IllegalArgumentException("Account ID must not be null");
        }
        if (amount == null) {
            throw new IllegalArgumentException("Amount must not be null");
        }
    }
}
