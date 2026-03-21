package com.teya.tinyledger.ledger.infrastructure.rest.response;

import com.teya.tinyledger.ledger.domain.balance.AccountBalance;
import java.math.BigDecimal;

public record BalanceResponse(String accountId, BigDecimal amount) {

    public static BalanceResponse from(AccountBalance balance) {
        return new BalanceResponse(balance.accountId().value(), balance.amount().value());
    }
}
