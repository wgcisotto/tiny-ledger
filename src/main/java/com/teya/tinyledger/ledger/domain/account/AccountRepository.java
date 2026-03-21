package com.teya.tinyledger.ledger.domain.account;
import java.util.Set;

public interface AccountRepository {

    boolean exists(AccountId accountId);

    Set<AccountId> findAll();
}
