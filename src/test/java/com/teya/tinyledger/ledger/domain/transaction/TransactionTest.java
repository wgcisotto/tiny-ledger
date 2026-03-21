package com.teya.tinyledger.ledger.domain.transaction;

import static org.assertj.core.api.Assertions.assertThat;

import com.teya.tinyledger.ledger.domain.account.AccountId;
import com.teya.tinyledger.ledger.domain.amount.Amount;
import org.junit.jupiter.api.Test;

class TransactionTest {

    @Test
    void createsDepositTransaction() {
        Transaction transaction = Transaction.deposit(
            AccountId.of("ACCOUNT_1"),
            Amount.of("10.00"),
            "deposit-ref"
        );

        assertThat(transaction.type()).isEqualTo(TransactionType.DEPOSIT);
        assertThat(transaction.accountId()).isEqualTo(AccountId.of("ACCOUNT_1"));
        assertThat(transaction.amount()).isEqualTo(Amount.of("10.00"));
        assertThat(transaction.referenceId()).isEqualTo("deposit-ref");
        assertThat(transaction.id()).isNotNull();
        assertThat(transaction.createdAt()).isNotNull();
    }

    @Test
    void createsWithdrawalTransaction() {
        Transaction transaction = Transaction.withdrawal(
            AccountId.of("ACCOUNT_1"),
            Amount.of("5.25"),
            "withdraw-ref"
        );

        assertThat(transaction.type()).isEqualTo(TransactionType.WITHDRAWAL);
        assertThat(transaction.accountId()).isEqualTo(AccountId.of("ACCOUNT_1"));
        assertThat(transaction.amount()).isEqualTo(Amount.of("5.25"));
        assertThat(transaction.referenceId()).isEqualTo("withdraw-ref");
        assertThat(transaction.id()).isNotNull();
        assertThat(transaction.createdAt()).isNotNull();
    }
}
