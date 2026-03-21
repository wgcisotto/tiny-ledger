package com.teya.tinyledger.ledger.domain.balance;

import com.teya.tinyledger.ledger.domain.account.AccountId;
import com.teya.tinyledger.ledger.domain.amount.Amount;

public record AccountBalance(AccountId accountId, Amount amount) {

    public AccountBalance {
        if (accountId == null) {
            throw new IllegalArgumentException("Account ID must not be null");
        }
        if (amount == null) {
            throw new IllegalArgumentException("Balance amount must not be null");
        }
    }

    public static AccountBalance zero(AccountId accountId) {
        return new AccountBalance(accountId, Amount.zero());
    }

    public AccountBalance add(Amount value) {
        return new AccountBalance(accountId, amount.add(value));
    }

    public AccountBalance subtract(Amount value) {
        return new AccountBalance(accountId, amount.subtract(value));
    }
}
