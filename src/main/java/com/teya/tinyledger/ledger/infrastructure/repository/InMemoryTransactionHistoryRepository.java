package com.teya.tinyledger.ledger.infrastructure.repository;

import com.teya.tinyledger.ledger.domain.account.AccountId;
import com.teya.tinyledger.ledger.domain.account.AccountRepository;
import com.teya.tinyledger.ledger.domain.transaction.AccountTransactionHistory;
import com.teya.tinyledger.ledger.domain.transaction.TransactionHistoryRepository;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class InMemoryTransactionHistoryRepository implements TransactionHistoryRepository {

    private final Map<AccountId, AccountTransactionHistory> histories = new ConcurrentHashMap<>();

    public InMemoryTransactionHistoryRepository(AccountRepository accountRepository) {
        accountRepository.findAll().forEach(accountId -> histories.put(accountId, AccountTransactionHistory.empty(accountId)));
    }

    @Override
    public AccountTransactionHistory get(AccountId accountId) {
        return histories.computeIfAbsent(accountId, AccountTransactionHistory::empty);
    }

    @Override
    public void save(AccountTransactionHistory history) {
        histories.put(history.accountId(), history);
    }
}
