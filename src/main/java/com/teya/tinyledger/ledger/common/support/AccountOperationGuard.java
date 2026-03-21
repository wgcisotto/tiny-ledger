package com.teya.tinyledger.ledger.common.support;

import com.teya.tinyledger.ledger.domain.account.AccountId;
import java.util.function.Supplier;

public interface AccountOperationGuard {

    <T> T execute(AccountId accountId, Supplier<T> operation);

}
