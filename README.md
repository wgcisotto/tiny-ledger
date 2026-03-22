[![CI](https://github.com/wgcisotto/tiny-ledger/actions/workflows/ci.yml/badge.svg)](https://github.com/wgcisotto/tiny-ledger/actions/workflows/ci.yml)
[![Coverage](https://github.com/wgcisotto/tiny-ledger/actions/workflows/coverage.yml/badge.svg)](https://github.com/wgcisotto/tiny-ledger/actions/workflows/coverage.yml)

# Tiny Ledger

Tiny Ledger is a small Spring Boot REST API for deposits, withdrawals, balances, and transaction history over a predefined set of accounts.

Everything runs in memory and in a single application instance. The goal of the project is to keep the solution simple, but still show a clean architecture and clear business rules.

## Table of Contents

1. [High-level design](#high-level-design)
2. [Key decisions](#key-decisions)
3. [API](#api)
4. [How to run](#how-to-run)
5. [Happy path](#happy-path)
6. [Testing and coverage](#testing-and-coverage)
7. [Scope](#scope)

## High-level design

The project is split into three layers:

- **Domain**: business entities, value objects, repository contracts, and business rules
- **Application**: command/query handlers that orchestrate ledger operations
- **Infrastructure**: REST controllers, in-memory adapters, bootstrap configuration, and supporting runtime components

### Domain

The domain contains the core ledger concepts:

- `AccountId`
- `Amount`
- `Transaction`
- `TransactionType`
- `AccountBalance`
- `AccountTransactionHistory`

Main rules enforced by the domain and application flow:

- an account must exist
- an amount must be greater than zero
- a withdrawal cannot make the balance negative
- accepted transactions are immutable

### Application

The application layer separates writes from reads:

- Commands:
  - `DepositHandler`
  - `WithdrawHandler`
- Queries:
  - `GetBalanceHandler`
  - `GetTransactionsHandler`

This keeps responsibilities clear and makes the flow easier to follow.

This is not a full CQRS implementation. There are no separate read and write databases, no separate services, and no asynchronous projections. The project only uses the simpler idea of separating commands from queries inside the application layer.

### Infrastructure

This is the layer that exposes the API and provides the in-memory implementations used by the application:

- Spring Boot REST API
- in-memory repository implementations
- predefined account bootstrap
- in-memory account operation guard

## Key decisions

### Balance projection plus transaction history

The ledger keeps:

- transaction history for auditability
- a separate current balance projection for fast reads

This means the transaction history is still available, but `GET /accounts/{accountId}/balance` does not need to walk the full list every time. The current balance is already stored in memory, so that lookup stays O(1).

### Per-account write protection

If two requests try to update the same account at the same time, the application handles them one after the other.

This avoids ending up with the wrong balance or missing history entries. It is only a simple in-memory protection inside one running application, not something shared across multiple instances or backed by a database transaction.

### Predefined accounts

The application starts with five predefined accounts:

- `SYSTEM`
- `ACCOUNT_1`
- `ACCOUNT_2`
- `ACCOUNT_3`
- `ACCOUNT_4`

`SYSTEM` is the internal system account used in the ledger model.

## API

| Method | Path | Description |
| --- | --- | --- |
| `POST` | `/accounts/{accountId}/deposits` | Add funds to an account |
| `POST` | `/accounts/{accountId}/withdrawals` | Remove funds from an account if balance is sufficient |
| `GET` | `/accounts/{accountId}/balance` | Return the current account balance |
| `GET` | `/accounts/{accountId}/transactions` | Return the account transaction history |

## How to run

### Requirements

- Java 21
- Maven Wrapper included in the repository

### Start the application

```bash
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8080`.

## Happy path

The quickest way to try the API is:

1. Deposit into `ACCOUNT_1`
2. Withdraw part of the balance
3. Read the current balance
4. Read the transaction history

### Deposit

```bash
curl -X POST http://localhost:8080/accounts/ACCOUNT_1/deposits \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 100.00,
    "referenceId": "deposit-ref-1"
  }'
```

### Withdraw

```bash
curl -X POST http://localhost:8080/accounts/ACCOUNT_1/withdrawals \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 40.00,
    "referenceId": "withdraw-ref-1"
  }'
```

### Get balance

```bash
curl http://localhost:8080/accounts/ACCOUNT_1/balance
```

### Get transactions

```bash
curl http://localhost:8080/accounts/ACCOUNT_1/transactions
```

There are more runnable examples in [accounts.http](http/accounts.http).

## Testing and coverage

Run the test suite:

```bash
./mvnw test
```

Generate the JaCoCo coverage report:

```bash
./mvnw clean test
```

Open the HTML report at:

`target/site/jacoco/index.html`

GitHub Actions also runs CI and checks that coverage stays above `80%`.

## Scope

Included:

- in-memory ledger state
- predefined accounts
- clear layered architecture
- REST API
- automated tests and coverage reporting

Out of scope:

- authentication and authorization
- persistence across restarts
- distributed consistency
- multi-instance coordination
- production-grade observability
