package com.teya.tinyledger.ledger.infrastructure.repository;

import com.teya.tinyledger.ledger.domain.account.AccountId;
import com.teya.tinyledger.ledger.domain.account.AccountRepository;
import java.util.LinkedHashSet;
import java.util.Set;

public class InMemoryAccountRepository implements AccountRepository {

    private final Set<AccountId> accountIds;

    public InMemoryAccountRepository(Set<AccountId> accountIds) {
        this.accountIds = new LinkedHashSet<>(accountIds);
    }

    @Override
    public boolean exists(AccountId accountId) {
        return accountIds.contains(accountId);
    }

    @Override
    public Set<AccountId> findAll() {
        return accountIds;
    }
}
