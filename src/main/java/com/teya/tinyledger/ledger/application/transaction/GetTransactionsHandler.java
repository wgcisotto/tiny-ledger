package com.teya.tinyledger.ledger.application.transaction;

import com.teya.tinyledger.ledger.application.exception.AccountNotFoundException;
import com.teya.tinyledger.ledger.application.query.QueryHandler;
import com.teya.tinyledger.ledger.domain.account.AccountRepository;
import com.teya.tinyledger.ledger.domain.transaction.AccountTransactionHistory;
import com.teya.tinyledger.ledger.domain.transaction.TransactionHistoryRepository;
import org.springframework.stereotype.Component;

@Component
public class GetTransactionsHandler implements QueryHandler<GetTransactionsQuery, AccountTransactionHistory> {

    private final AccountRepository accountRepository;
    private final TransactionHistoryRepository transactionHistoryRepository;

    public GetTransactionsHandler(
        AccountRepository accountRepository,
        TransactionHistoryRepository transactionHistoryRepository
    ) {
        this.accountRepository = accountRepository;
        this.transactionHistoryRepository = transactionHistoryRepository;
    }

    @Override
    public AccountTransactionHistory handle(GetTransactionsQuery query) {
        if (!accountRepository.exists(query.accountId())) {
            throw new AccountNotFoundException("Account not found: " + query.accountId().value());
        }

        return transactionHistoryRepository.get(query.accountId());
    }
}
