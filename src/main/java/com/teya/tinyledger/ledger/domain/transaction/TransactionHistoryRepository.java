package com.teya.tinyledger.ledger.domain.transaction;
import com.teya.tinyledger.ledger.domain.account.AccountId;

public interface TransactionHistoryRepository {

    AccountTransactionHistory get(AccountId accountId);

    void save(AccountTransactionHistory history);
}
