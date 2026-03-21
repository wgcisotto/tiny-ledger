package com.teya.tinyledger.ledger.infrastructure.concurrency;

import static org.assertj.core.api.Assertions.assertThat;

import com.teya.tinyledger.ledger.domain.account.AccountId;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

class InMemoryAccountOperationGuardTest {

    @Test
    void returnsSupplierResult() {
        InMemoryAccountOperationGuard guard = new InMemoryAccountOperationGuard();

        String result = guard.execute(AccountId.of("ACCOUNT_1"), () -> "ok");

        assertThat(result).isEqualTo("ok");
    }

    @Test
    void serializesOperationsForSameAccount() throws Exception {
        InMemoryAccountOperationGuard guard = new InMemoryAccountOperationGuard();
        CountDownLatch firstEntered = new CountDownLatch(1);
        CountDownLatch allowFirstToFinish = new CountDownLatch(1);
        CountDownLatch secondEntered = new CountDownLatch(1);

        try (ExecutorService executor = Executors.newFixedThreadPool(2)) {
            Future<?> first = executor.submit(() -> guard.execute(AccountId.of("ACCOUNT_1"), () -> {
                firstEntered.countDown();
                await(allowFirstToFinish);
                return null;
            }));

            assertThat(firstEntered.await(1, TimeUnit.SECONDS)).isTrue();

            Future<?> second = executor.submit(() -> guard.execute(AccountId.of("ACCOUNT_1"), () -> {
                secondEntered.countDown();
                return null;
            }));

            assertThat(secondEntered.await(200, TimeUnit.MILLISECONDS)).isFalse();

            allowFirstToFinish.countDown();

            second.get(1, TimeUnit.SECONDS);
            first.get(1, TimeUnit.SECONDS);

            assertThat(secondEntered.getCount()).isZero();
        }
    }

    private static void await(CountDownLatch latch) {
        try {
            latch.await(1, TimeUnit.SECONDS);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(exception);
        }
    }
}
