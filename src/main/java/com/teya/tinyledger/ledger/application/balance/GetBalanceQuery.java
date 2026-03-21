package com.teya.tinyledger.ledger.application.balance;

import com.teya.tinyledger.ledger.domain.account.AccountId;

public record GetBalanceQuery(AccountId accountId) {

    public GetBalanceQuery {
        if (accountId == null) {
            throw new IllegalArgumentException("Account ID must not be null");
        }
    }
}
