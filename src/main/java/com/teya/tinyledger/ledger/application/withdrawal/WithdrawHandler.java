package com.teya.tinyledger.ledger.application.withdrawal;

import com.teya.tinyledger.ledger.application.command.CommandHandler;
import com.teya.tinyledger.ledger.application.exception.AccountNotFoundException;
import com.teya.tinyledger.ledger.application.exception.InsufficientFundsException;
import com.teya.tinyledger.ledger.application.support.AccountOperationGuard;
import com.teya.tinyledger.ledger.domain.account.AccountRepository;
import com.teya.tinyledger.ledger.domain.balance.AccountBalance;
import com.teya.tinyledger.ledger.domain.balance.BalanceRepository;
import com.teya.tinyledger.ledger.domain.transaction.Transaction;
import com.teya.tinyledger.ledger.domain.transaction.TransactionHistoryRepository;
import org.springframework.stereotype.Component;

@Component
public class WithdrawHandler implements CommandHandler<WithdrawCommand, Transaction> {

    private final AccountRepository accountRepository;
    private final BalanceRepository balanceRepository;
    private final TransactionHistoryRepository transactionHistoryRepository;
    private final AccountOperationGuard accountOperationGuard;

    public WithdrawHandler(
        AccountRepository accountRepository,
        BalanceRepository balanceRepository,
        TransactionHistoryRepository transactionHistoryRepository,
        AccountOperationGuard accountOperationGuard
    ) {
        this.accountRepository = accountRepository;
        this.balanceRepository = balanceRepository;
        this.transactionHistoryRepository = transactionHistoryRepository;
        this.accountOperationGuard = accountOperationGuard;
    }

    @Override
    public Transaction handle(WithdrawCommand command) {
        return accountOperationGuard.execute(command.accountId(), () -> {
            if (!accountRepository.exists(command.accountId())) {
                throw new AccountNotFoundException("Account not found: " + command.accountId().value());
            }

            AccountBalance currentBalance = balanceRepository.get(command.accountId());
            if (currentBalance.amount().isLessThan(command.amount())) {
                throw new InsufficientFundsException("Insufficient funds for account: " + command.accountId().value());
            }

            Transaction transaction = Transaction.withdrawal(command.accountId(), command.amount(), command.referenceId());
            AccountBalance updatedBalance = currentBalance.subtract(command.amount());

            transactionHistoryRepository.save(transactionHistoryRepository.get(command.accountId()).append(transaction));
            balanceRepository.save(updatedBalance);

            return transaction;
        });
    }
}
