package com.teya.tinyledger.ledger.infrastructure.bootstrap;

import static org.assertj.core.api.Assertions.assertThat;

import com.teya.tinyledger.ledger.domain.account.AccountId;
import com.teya.tinyledger.ledger.domain.account.AccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AccountBootstrapConfigurationTest {

    @Autowired
    private AccountRepository accountRepository;

    @Test
    void loadsPredefinedAccounts() {
        assertThat(accountRepository.exists(AccountId.of("SYSTEM"))).isTrue();
        assertThat(accountRepository.exists(AccountId.of("ACCOUNT_1"))).isTrue();
        assertThat(accountRepository.exists(AccountId.of("ACCOUNT_2"))).isTrue();
        assertThat(accountRepository.exists(AccountId.of("ACCOUNT_3"))).isTrue();
        assertThat(accountRepository.exists(AccountId.of("ACCOUNT_4"))).isTrue();
    }
}
