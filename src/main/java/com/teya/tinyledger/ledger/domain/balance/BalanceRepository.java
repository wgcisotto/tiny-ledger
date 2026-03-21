package com.teya.tinyledger.ledger.domain.balance;
import com.teya.tinyledger.ledger.domain.account.AccountId;

public interface BalanceRepository {

    AccountBalance get(AccountId accountId);

    void save(AccountBalance accountBalance);
}
