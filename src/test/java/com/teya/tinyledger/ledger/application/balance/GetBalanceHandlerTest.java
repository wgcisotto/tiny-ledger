package com.teya.tinyledger.ledger.application.balance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.teya.tinyledger.ledger.application.deposit.DepositCommand;
import com.teya.tinyledger.ledger.application.deposit.DepositHandler;
import com.teya.tinyledger.ledger.application.exception.AccountNotFoundException;
import com.teya.tinyledger.ledger.infrastructure.concurrency.InMemoryAccountOperationGuard;
import com.teya.tinyledger.ledger.infrastructure.repository.InMemoryAccountRepository;
import com.teya.tinyledger.ledger.infrastructure.repository.InMemoryBalanceRepository;
import com.teya.tinyledger.ledger.infrastructure.repository.InMemoryTransactionHistoryRepository;
import com.teya.tinyledger.ledger.domain.account.AccountId;
import com.teya.tinyledger.ledger.domain.account.AccountRepository;
import com.teya.tinyledger.ledger.domain.amount.Amount;
import com.teya.tinyledger.ledger.domain.balance.BalanceRepository;
import com.teya.tinyledger.ledger.domain.transaction.TransactionHistoryRepository;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GetBalanceHandlerTest {

    private static final AccountId ACCOUNT_ID = AccountId.of("ACCOUNT_1");

    private GetBalanceHandler getBalanceHandler;

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
        depositHandler.handle(new DepositCommand(ACCOUNT_ID, Amount.of("10.50"), "seed-ref"));

        getBalanceHandler = new GetBalanceHandler(accountRepository, balanceRepository);
    }

    @Test
    void returnsCurrentProjectedBalance() {
        var balance = getBalanceHandler.handle(new GetBalanceQuery(ACCOUNT_ID));

        assertThat(balance.accountId()).isEqualTo(ACCOUNT_ID);
        assertThat(balance.amount()).isEqualTo(Amount.of("10.50"));
    }

    @Test
    void unknownAccountFails() {
        assertThatThrownBy(() -> getBalanceHandler.handle(new GetBalanceQuery(AccountId.of("UNKNOWN"))))
            .isInstanceOf(AccountNotFoundException.class)
            .hasMessage("Account not found: UNKNOWN");
    }
}
