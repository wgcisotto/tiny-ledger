package com.teya.tinyledger.ledger.domain.amount;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record Amount(BigDecimal value) {

    private static final int SCALE = 2;

    public Amount {
        if (value == null) {
            throw new IllegalArgumentException("Amount must not be null");
        }

        value = value.setScale(SCALE, RoundingMode.HALF_UP);
    }

    public static Amount of(BigDecimal value) {
        Amount amount = new Amount(value);
        if (amount.value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        return amount;
    }

    public static Amount of(String value) {
        return of(new BigDecimal(value));
    }

    public static Amount zero() {
        return new Amount(BigDecimal.ZERO);
    }

    public Amount add(Amount other) {
        return new Amount(value.add(other.value));
    }

    public Amount subtract(Amount other) {
        return new Amount(value.subtract(other.value));
    }

    public boolean isLessThan(Amount other) {
        return value.compareTo(other.value) < 0;
    }
}
