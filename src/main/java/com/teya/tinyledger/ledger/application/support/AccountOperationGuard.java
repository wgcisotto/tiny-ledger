package com.teya.tinyledger.ledger.application.support;

import com.teya.tinyledger.ledger.domain.account.AccountId;
import java.util.function.Supplier;

public interface AccountOperationGuard {

    <T> T execute(AccountId accountId, Supplier<T> operation);

    default void execute(AccountId accountId, Runnable operation) {
        execute(accountId, () -> {
            operation.run();
            return null;
        });
    }
}
