package com.teya.tinyledger.ledger.application.deposit;

import com.teya.tinyledger.ledger.common.command.CommandHandler;
import com.teya.tinyledger.ledger.common.exception.AccountNotFoundException;
import com.teya.tinyledger.ledger.common.support.AccountOperationGuard;
import com.teya.tinyledger.ledger.domain.account.AccountRepository;
import com.teya.tinyledger.ledger.domain.balance.AccountBalance;
import com.teya.tinyledger.ledger.domain.balance.BalanceRepository;
import com.teya.tinyledger.ledger.domain.transaction.Transaction;
import com.teya.tinyledger.ledger.domain.transaction.TransactionHistoryRepository;
import org.springframework.stereotype.Component;

@Component
public class DepositHandler implements CommandHandler<DepositCommand, Transaction> {

    private final AccountRepository accountRepository;
    private final BalanceRepository balanceRepository;
    private final TransactionHistoryRepository transactionHistoryRepository;
    private final AccountOperationGuard accountOperationGuard;

    public DepositHandler(
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
    public Transaction handle(DepositCommand command) {
        return accountOperationGuard.execute(command.accountId(), () -> {
            if (!accountRepository.exists(command.accountId())) {
                throw new AccountNotFoundException("Account not found: " + command.accountId().value());
            }

            Transaction transaction = Transaction.deposit(command.accountId(), command.amount(), command.referenceId());
            AccountBalance updatedBalance = balanceRepository.get(command.accountId()).add(command.amount());

            transactionHistoryRepository.save(transactionHistoryRepository.get(command.accountId()).append(transaction));
            balanceRepository.save(updatedBalance);

            return transaction;
        });
    }
}
