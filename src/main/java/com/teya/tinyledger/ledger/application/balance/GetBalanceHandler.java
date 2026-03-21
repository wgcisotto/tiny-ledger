package com.teya.tinyledger.ledger.application.balance;

import com.teya.tinyledger.ledger.common.exception.AccountNotFoundException;
import com.teya.tinyledger.ledger.common.query.QueryHandler;
import com.teya.tinyledger.ledger.domain.account.AccountRepository;
import com.teya.tinyledger.ledger.domain.balance.AccountBalance;
import com.teya.tinyledger.ledger.domain.balance.BalanceRepository;
import org.springframework.stereotype.Component;

@Component
public class GetBalanceHandler implements QueryHandler<GetBalanceQuery, AccountBalance> {

    private final AccountRepository accountRepository;
    private final BalanceRepository balanceRepository;

    public GetBalanceHandler(AccountRepository accountRepository, BalanceRepository balanceRepository) {
        this.accountRepository = accountRepository;
        this.balanceRepository = balanceRepository;
    }

    @Override
    public AccountBalance handle(GetBalanceQuery query) {
        if (!accountRepository.exists(query.accountId())) {
            throw new AccountNotFoundException("Account not found: " + query.accountId().value());
        }

        return balanceRepository.get(query.accountId());
    }
}
