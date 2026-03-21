package com.teya.tinyledger.ledger.infrastructure.rest;

import com.teya.tinyledger.ledger.application.balance.GetBalanceHandler;
import com.teya.tinyledger.ledger.application.balance.GetBalanceQuery;
import com.teya.tinyledger.ledger.application.deposit.DepositHandler;
import com.teya.tinyledger.ledger.application.transaction.GetTransactionsHandler;
import com.teya.tinyledger.ledger.application.transaction.GetTransactionsQuery;
import com.teya.tinyledger.ledger.application.withdrawal.WithdrawHandler;
import com.teya.tinyledger.ledger.domain.account.AccountId;
import com.teya.tinyledger.ledger.infrastructure.rest.request.DepositRequest;
import com.teya.tinyledger.ledger.infrastructure.rest.request.WithdrawalRequest;
import com.teya.tinyledger.ledger.infrastructure.rest.response.BalanceResponse;
import com.teya.tinyledger.ledger.infrastructure.rest.response.TransactionResponse;
import com.teya.tinyledger.ledger.infrastructure.rest.response.TransactionsResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final DepositHandler depositHandler;
    private final WithdrawHandler withdrawHandler;
    private final GetBalanceHandler getBalanceHandler;
    private final GetTransactionsHandler getTransactionsHandler;

    public AccountController(
        DepositHandler depositHandler,
        WithdrawHandler withdrawHandler,
        GetBalanceHandler getBalanceHandler,
        GetTransactionsHandler getTransactionsHandler
    ) {
        this.depositHandler = depositHandler;
        this.withdrawHandler = withdrawHandler;
        this.getBalanceHandler = getBalanceHandler;
        this.getTransactionsHandler = getTransactionsHandler;
    }

    @PostMapping("/{accountId}/deposits")
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse deposit(
        @PathVariable String accountId,
        @Valid @RequestBody DepositRequest request
    ) {
        return TransactionResponse.from(depositHandler.handle(request.toCommand(AccountId.of(accountId))));
    }

    @PostMapping("/{accountId}/withdrawals")
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse withdraw(
        @PathVariable String accountId,
        @Valid @RequestBody WithdrawalRequest request
    ) {
        return TransactionResponse.from(withdrawHandler.handle(request.toCommand(AccountId.of(accountId))));
    }

    @GetMapping("/{accountId}/balance")
    public BalanceResponse getBalance(@PathVariable String accountId) {
        return BalanceResponse.from(getBalanceHandler.handle(new GetBalanceQuery(AccountId.of(accountId))));
    }

    @GetMapping("/{accountId}/transactions")
    public TransactionsResponse getTransactions(@PathVariable String accountId) {
        return TransactionsResponse.from(
            getTransactionsHandler.handle(new GetTransactionsQuery(AccountId.of(accountId)))
        );
    }
}
