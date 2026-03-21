package com.teya.tinyledger.ledger.infrastructure.bootstrap;

import com.teya.tinyledger.ledger.domain.account.AccountId;
import com.teya.tinyledger.ledger.domain.account.AccountRepository;
import com.teya.tinyledger.ledger.infrastructure.repository.InMemoryAccountRepository;
import java.util.LinkedHashSet;
import java.util.Set;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AccountBootstrapConfiguration {

    @Bean
    AccountRepository accountRepository() {
        Set<AccountId> accountIds = new LinkedHashSet<>();
        accountIds.add(AccountId.of("SYSTEM"));
        accountIds.add(AccountId.of("ACCOUNT_1"));
        accountIds.add(AccountId.of("ACCOUNT_2"));
        accountIds.add(AccountId.of("ACCOUNT_3"));
        accountIds.add(AccountId.of("ACCOUNT_4"));
        return new InMemoryAccountRepository(accountIds);
    }
}
