package com.teya.tinyledger.ledger.infrastructure.support;

import com.teya.tinyledger.ledger.common.support.AccountOperationGuard;
import com.teya.tinyledger.ledger.domain.account.AccountId;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;
import org.springframework.stereotype.Component;

@Component
public class InMemoryAccountOperationGuard implements AccountOperationGuard {

    private final Map<AccountId, ReentrantLock> locks = new ConcurrentHashMap<>();

    @Override
    public <T> T execute(AccountId accountId, Supplier<T> operation) {
        ReentrantLock lock = locks.computeIfAbsent(accountId, ignored -> new ReentrantLock());
        lock.lock();
        try {
            return operation.get();
        } finally {
            lock.unlock();
        }
    }
}
