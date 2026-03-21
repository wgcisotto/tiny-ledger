package com.teya.tinyledger.ledger.application.transaction;

import com.teya.tinyledger.ledger.domain.account.AccountId;

public record GetTransactionsQuery(AccountId accountId) {

    public GetTransactionsQuery {
        if (accountId == null) {
            throw new IllegalArgumentException("Account ID must not be null");
        }
    }
}
