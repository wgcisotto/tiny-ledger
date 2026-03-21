package com.teya.tinyledger.ledger.application.deposit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.teya.tinyledger.ledger.application.exception.AccountNotFoundException;
import com.teya.tinyledger.ledger.domain.account.AccountId;
import com.teya.tinyledger.ledger.domain.account.AccountRepository;
import com.teya.tinyledger.ledger.domain.amount.Amount;
import com.teya.tinyledger.ledger.domain.balance.BalanceRepository;
import com.teya.tinyledger.ledger.domain.transaction.TransactionHistoryRepository;
import com.teya.tinyledger.ledger.domain.transaction.TransactionType;
import com.teya.tinyledger.ledger.infrastructure.concurrency.InMemoryAccountOperationGuard;
import com.teya.tinyledger.ledger.infrastructure.repository.InMemoryAccountRepository;
import com.teya.tinyledger.ledger.infrastructure.repository.InMemoryBalanceRepository;
import com.teya.tinyledger.ledger.infrastructure.repository.InMemoryTransactionHistoryRepository;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DepositHandlerTest {

    private static final AccountId ACCOUNT_ID = AccountId.of("ACCOUNT_1");

    private DepositHandler depositHandler;
    private BalanceRepository balanceRepository;
    private TransactionHistoryRepository transactionHistoryRepository;

    @BeforeEach
    void setUp() {
        AccountRepository accountRepository = new InMemoryAccountRepository(Set.of(ACCOUNT_ID));
        balanceRepository = new InMemoryBalanceRepository(accountRepository);
        transactionHistoryRepository = new InMemoryTransactionHistoryRepository(accountRepository);
        depositHandler = new DepositHandler(
            accountRepository,
            balanceRepository,
            transactionHistoryRepository,
            new InMemoryAccountOperationGuard()
        );
    }

    @Test
    void depositUpdatesBalanceAndHistory() {
        var transaction = depositHandler.handle(new DepositCommand(ACCOUNT_ID, Amount.of("10.50"), "deposit-ref"));

        assertThat(transaction.type()).isEqualTo(TransactionType.DEPOSIT);
        assertThat(balanceRepository.get(ACCOUNT_ID).amount()).isEqualTo(Amount.of("10.50"));
        assertThat(transactionHistoryRepository.get(ACCOUNT_ID).transactions()).containsExactly(transaction);
    }

    @Test
    void depositForUnknownAccountFails() {
        assertThatThrownBy(() -> depositHandler.handle(
            new DepositCommand(AccountId.of("UNKNOWN"), Amount.of("10.50"), "deposit-ref")
        ))
            .isInstanceOf(AccountNotFoundException.class)
            .hasMessage("Account not found: UNKNOWN");
    }
}
