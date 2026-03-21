package com.teya.tinyledger.ledger.application.transaction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.teya.tinyledger.ledger.application.deposit.DepositCommand;
import com.teya.tinyledger.ledger.application.deposit.DepositHandler;
import com.teya.tinyledger.ledger.common.exception.AccountNotFoundException;
import com.teya.tinyledger.ledger.application.withdrawal.WithdrawCommand;
import com.teya.tinyledger.ledger.application.withdrawal.WithdrawHandler;
import com.teya.tinyledger.ledger.infrastructure.support.InMemoryAccountOperationGuard;
import com.teya.tinyledger.ledger.infrastructure.repository.InMemoryAccountRepository;
import com.teya.tinyledger.ledger.infrastructure.repository.InMemoryBalanceRepository;
import com.teya.tinyledger.ledger.infrastructure.repository.InMemoryTransactionHistoryRepository;
import com.teya.tinyledger.ledger.domain.account.AccountId;
import com.teya.tinyledger.ledger.domain.account.AccountRepository;
import com.teya.tinyledger.ledger.domain.amount.Amount;
import com.teya.tinyledger.ledger.domain.balance.BalanceRepository;
import com.teya.tinyledger.ledger.domain.transaction.TransactionHistoryRepository;
import com.teya.tinyledger.ledger.domain.transaction.TransactionType;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GetTransactionsHandlerTest {

    private static final AccountId ACCOUNT_ID = AccountId.of("ACCOUNT_1");

    private GetTransactionsHandler getTransactionsHandler;

    @BeforeEach
    void setUp() {
        AccountRepository accountRepository = new InMemoryAccountRepository(Set.of(ACCOUNT_ID));
        BalanceRepository balanceRepository = new InMemoryBalanceRepository(accountRepository);
        TransactionHistoryRepository transactionHistoryRepository = new InMemoryTransactionHistoryRepository(accountRepository);
        var operationGuard = new InMemoryAccountOperationGuard();

        DepositHandler depositHandler = new DepositHandler(
            accountRepository,
            balanceRepository,
            transactionHistoryRepository,
            operationGuard
        );
        WithdrawHandler withdrawHandler = new WithdrawHandler(
            accountRepository,
            balanceRepository,
            transactionHistoryRepository,
            operationGuard
        );

        depositHandler.handle(new DepositCommand(ACCOUNT_ID, Amount.of("10.50"), "deposit-ref"));
        withdrawHandler.handle(new WithdrawCommand(ACCOUNT_ID, Amount.of("4.25"), "withdraw-ref"));

        getTransactionsHandler = new GetTransactionsHandler(accountRepository, transactionHistoryRepository);
    }

    @Test
    void returnsOrderedTransactionHistory() {
        var history = getTransactionsHandler.handle(new GetTransactionsQuery(ACCOUNT_ID));

        assertThat(history.accountId()).isEqualTo(ACCOUNT_ID);
        assertThat(history.transactions()).hasSize(2);
        assertThat(history.transactions().get(0).type()).isEqualTo(TransactionType.DEPOSIT);
        assertThat(history.transactions().get(1).type()).isEqualTo(TransactionType.WITHDRAWAL);
    }

    @Test
    void unknownAccountFails() {
        assertThatThrownBy(() -> getTransactionsHandler.handle(new GetTransactionsQuery(AccountId.of("UNKNOWN"))))
            .isInstanceOf(AccountNotFoundException.class)
            .hasMessage("Account not found: UNKNOWN");
    }
}
