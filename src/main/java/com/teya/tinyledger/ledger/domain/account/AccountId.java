package com.teya.tinyledger.ledger.domain.account;

public record AccountId(String value) {

    public AccountId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Account ID must not be blank");
        }
    }

    public static AccountId of(String value) {
        return new AccountId(value);
    }
}