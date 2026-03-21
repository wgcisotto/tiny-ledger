package com.teya.tinyledger.ledger.infrastructure.rest.response;

import com.teya.tinyledger.ledger.domain.transaction.AccountTransactionHistory;
import java.util.List;

public record TransactionsResponse(String accountId, List<TransactionResponse> transactions) {

    public static TransactionsResponse from(AccountTransactionHistory history) {
        return new TransactionsResponse(
            history.accountId().value(),
            history.transactions().stream().map(TransactionResponse::from).toList()
        );
    }
}
