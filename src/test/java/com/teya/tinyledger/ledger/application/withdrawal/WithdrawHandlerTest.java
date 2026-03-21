package com.teya.tinyledger.ledger.application.withdrawal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.teya.tinyledger.ledger.application.deposit.DepositCommand;
import com.teya.tinyledger.ledger.application.deposit.DepositHandler;
import com.teya.tinyledger.ledger.application.exception.AccountNotFoundException;
import com.teya.tinyledger.ledger.application.exception.InsufficientFundsException;
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

class WithdrawHandlerTest {

    private static final AccountId ACCOUNT_ID = AccountId.of("ACCOUNT_1");

    private WithdrawHandler withdrawHandler;
    private BalanceRepository balanceRepository;
    private TransactionHistoryRepository transactionHistoryRepository;

    @BeforeEach
    void setUp() {
        AccountRepository accountRepository = new InMemoryAccountRepository(Set.of(ACCOUNT_ID));
        balanceRepository = new InMemoryBalanceRepository(accountRepository);
        transactionHistoryRepository = new InMemoryTransactionHistoryRepository(accountRepository);
        var operationGuard = new InMemoryAccountOperationGuard();

        DepositHandler depositHandler = new DepositHandler(
            accountRepository,
            balanceRepository,
            transactionHistoryRepository,
            operationGuard
        );
        withdrawHandler = new WithdrawHandler(
            accountRepository,
            balanceRepository,
            transactionHistoryRepository,
            operationGuard
        );

        depositHandler.handle(new DepositCommand(ACCOUNT_ID, Amount.of("10.50"), "seed-ref"));
    }

    @Test
    void withdrawUpdatesBalanceAndHistory() {
        var transaction = withdrawHandler.handle(new WithdrawCommand(ACCOUNT_ID, Amount.of("4.25"), "withdraw-ref"));

        assertThat(transaction.type()).isEqualTo(TransactionType.WITHDRAWAL);
        assertThat(balanceRepository.get(ACCOUNT_ID).amount()).isEqualTo(Amount.of("6.25"));
        assertThat(transactionHistoryRepository.get(ACCOUNT_ID).transactions()).hasSize(2);
        assertThat(transactionHistoryRepository.get(ACCOUNT_ID).transactions().getLast()).isEqualTo(transaction);
    }

    @Test
    void withdrawExactAvailableBalanceSucceeds() {
        var transaction = withdrawHandler.handle(new WithdrawCommand(ACCOUNT_ID, Amount.of("10.50"), "withdraw-all"));

        assertThat(transaction.type()).isEqualTo(TransactionType.WITHDRAWAL);
        assertThat(balanceRepository.get(ACCOUNT_ID).amount().value()).isEqualByComparingTo("0.00");
    }

    @Test
    void withdrawAboveBalanceFails() {
        assertThatThrownBy(() -> withdrawHandler.handle(
            new WithdrawCommand(ACCOUNT_ID, Amount.of("10.51"), "too-much")
        ))
            .isInstanceOf(InsufficientFundsException.class)
            .hasMessage("Insufficient funds for account: ACCOUNT_1");
    }

    @Test
    void withdrawForUnknownAccountFails() {
        assertThatThrownBy(() -> withdrawHandler.handle(
            new WithdrawCommand(AccountId.of("UNKNOWN"), Amount.of("1.00"), "unknown")
        ))
            .isInstanceOf(AccountNotFoundException.class)
            .hasMessage("Account not found: UNKNOWN");
    }
}
