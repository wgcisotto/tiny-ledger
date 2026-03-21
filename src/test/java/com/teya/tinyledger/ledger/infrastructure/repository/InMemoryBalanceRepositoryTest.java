package com.teya.tinyledger.ledger.infrastructure.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.teya.tinyledger.ledger.domain.account.AccountId;
import com.teya.tinyledger.ledger.domain.account.AccountRepository;
import com.teya.tinyledger.ledger.domain.amount.Amount;
import com.teya.tinyledger.ledger.domain.balance.AccountBalance;
import java.util.Set;
import org.junit.jupiter.api.Test;

class InMemoryBalanceRepositoryTest {

    private final AccountRepository accountRepository = new InMemoryAccountRepository(Set.of(AccountId.of("ACCOUNT_1")));

    @Test
    void returnsZeroBalanceForSeededAccount() {
        InMemoryBalanceRepository repository = new InMemoryBalanceRepository(accountRepository);

        assertThat(repository.get(AccountId.of("ACCOUNT_1")))
            .isEqualTo(AccountBalance.zero(AccountId.of("ACCOUNT_1")));
    }

    @Test
    void savesUpdatedBalanceForAccount() {
        InMemoryBalanceRepository repository = new InMemoryBalanceRepository(accountRepository);
        AccountBalance updatedBalance = AccountBalance.zero(AccountId.of("ACCOUNT_1")).add(Amount.of("10.50"));

        repository.save(updatedBalance);

        assertThat(repository.get(AccountId.of("ACCOUNT_1"))).isEqualTo(updatedBalance);
    }
}
