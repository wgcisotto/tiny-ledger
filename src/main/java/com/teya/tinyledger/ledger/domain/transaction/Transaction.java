package com.teya.tinyledger.ledger.domain.transaction;

import com.teya.tinyledger.ledger.domain.account.AccountId;
import com.teya.tinyledger.ledger.domain.amount.Amount;

import java.time.Instant;
import java.util.UUID;

public record Transaction(
    UUID id,
    AccountId accountId,
    TransactionType type,
    Amount amount,
    String referenceId,
    Instant createdAt
) {

    public Transaction {
        if (id == null) {
            throw new IllegalArgumentException("Transaction ID must not be null");
        }
        if (accountId == null) {
            throw new IllegalArgumentException("Account ID must not be null");
        }
        if (type == null) {
            throw new IllegalArgumentException("Transaction type must not be null");
        }
        if (amount == null) {
            throw new IllegalArgumentException("Amount must not be null");
        }
        if (createdAt == null) {
            throw new IllegalArgumentException("Creation time must not be null");
        }
    }

    public static Transaction deposit(AccountId accountId, Amount amount, String referenceId) {
        return new Transaction(UUID.randomUUID(), accountId, TransactionType.DEPOSIT, amount, referenceId, Instant.now());
    }

    public static Transaction withdrawal(AccountId accountId, Amount amount, String referenceId) {
        return new Transaction(UUID.randomUUID(), accountId, TransactionType.WITHDRAWAL, amount, referenceId, Instant.now());
    }
}
