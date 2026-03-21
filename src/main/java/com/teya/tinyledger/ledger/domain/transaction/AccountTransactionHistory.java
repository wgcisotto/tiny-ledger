package com.teya.tinyledger.ledger.domain.transaction;

import com.teya.tinyledger.ledger.domain.account.AccountId;
import java.util.ArrayList;
import java.util.List;

public record AccountTransactionHistory(AccountId accountId, List<Transaction> transactions) {

    public AccountTransactionHistory {
        if (accountId == null) {
            throw new IllegalArgumentException("Account ID must not be null");
        }
        if (transactions == null) {
            throw new IllegalArgumentException("Transactions must not be null");
        }

        transactions = List.copyOf(transactions);
    }

    public static AccountTransactionHistory empty(AccountId accountId) {
        return new AccountTransactionHistory(accountId, List.of());
    }

    public AccountTransactionHistory append(Transaction transaction) {
        List<Transaction> updated = new ArrayList<>(transactions);
        updated.add(transaction);
        return new AccountTransactionHistory(accountId, updated);
    }
}
