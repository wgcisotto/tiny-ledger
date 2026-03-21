package com.teya.tinyledger.ledger.infrastructure.rest.response;

import com.teya.tinyledger.ledger.domain.transaction.Transaction;
import java.math.BigDecimal;
import java.time.Instant;

public record TransactionResponse(
    String id,
    String accountId,
    String type,
    BigDecimal amount,
    String referenceId,
    Instant createdAt
) {

    public static TransactionResponse from(Transaction transaction) {
        return new TransactionResponse(
            transaction.id().toString(),
            transaction.accountId().value(),
            transaction.type().name(),
            transaction.amount().value(),
            transaction.referenceId(),
            transaction.createdAt()
        );
    }
}
