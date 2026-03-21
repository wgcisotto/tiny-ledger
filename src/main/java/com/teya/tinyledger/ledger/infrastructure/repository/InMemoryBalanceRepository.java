package com.teya.tinyledger.ledger.infrastructure.repository;

import com.teya.tinyledger.ledger.domain.account.AccountId;
import com.teya.tinyledger.ledger.domain.account.AccountRepository;
import com.teya.tinyledger.ledger.domain.balance.AccountBalance;
import com.teya.tinyledger.ledger.domain.balance.BalanceRepository;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class InMemoryBalanceRepository implements BalanceRepository {

    private final Map<AccountId, AccountBalance> balances = new ConcurrentHashMap<>();

    public InMemoryBalanceRepository(AccountRepository accountRepository) {
        accountRepository.findAll().forEach(accountId -> balances.put(accountId, AccountBalance.zero(accountId)));
    }

    @Override
    public AccountBalance get(AccountId accountId) {
        return balances.computeIfAbsent(accountId, AccountBalance::zero);
    }

    @Override
    public void save(AccountBalance accountBalance) {
        balances.put(accountBalance.accountId(), accountBalance);
    }
}
