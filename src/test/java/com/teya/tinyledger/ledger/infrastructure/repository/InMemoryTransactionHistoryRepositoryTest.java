package com.teya.tinyledger.ledger.infrastructure.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.teya.tinyledger.ledger.domain.account.AccountId;
import com.teya.tinyledger.ledger.domain.account.AccountRepository;
import com.teya.tinyledger.ledger.domain.amount.Amount;
import com.teya.tinyledger.ledger.domain.transaction.AccountTransactionHistory;
import com.teya.tinyledger.ledger.domain.transaction.Transaction;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class InMemoryTransactionHistoryRepositoryTest {

    private final AccountRepository accountRepository = new InMemoryAccountRepository(Set.of(AccountId.of("ACCOUNT_1")));

    @Test
    void returnsEmptyHistoryForSeededAccount() {
        InMemoryTransactionHistoryRepository repository = new InMemoryTransactionHistoryRepository(accountRepository);

        assertThat(repository.get(AccountId.of("ACCOUNT_1")))
            .isEqualTo(AccountTransactionHistory.empty(AccountId.of("ACCOUNT_1")));
    }

    @Test
    void savesTransactionsInInsertionOrder() {
        InMemoryTransactionHistoryRepository repository = new InMemoryTransactionHistoryRepository(accountRepository);
        Transaction first = Transaction.deposit(AccountId.of("ACCOUNT_1"), Amount.of("10.00"), "first");
        Transaction second = Transaction.withdrawal(AccountId.of("ACCOUNT_1"), Amount.of("2.00"), "second");
        AccountTransactionHistory history = new AccountTransactionHistory(AccountId.of("ACCOUNT_1"), List.of(first, second));

        repository.save(history);

        assertThat(repository.get(AccountId.of("ACCOUNT_1")).transactions()).containsExactly(first, second);
    }
}
