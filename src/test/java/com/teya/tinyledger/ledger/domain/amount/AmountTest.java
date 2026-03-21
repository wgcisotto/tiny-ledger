package com.teya.tinyledger.ledger.domain.amount;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class AmountTest {

    @Test
    void rejectsNullAmount() {
        assertThatThrownBy(() -> Amount.of((BigDecimal) null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Amount must not be null");
    }

    @Test
    void rejectsZeroAmount() {
        assertThatThrownBy(() -> Amount.of(new BigDecimal("0.00")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Amount must be greater than zero");
    }

    @Test
    void normalizesScaleToTwoDecimals() {
        assertThat(Amount.of(new BigDecimal("10")).value())
            .isEqualByComparingTo(new BigDecimal("10.00"));
    }
}
