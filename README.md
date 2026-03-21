# Tiny Ledger

Tiny Ledger is a small in-memory ledger API. 
The goal is clarity over completeness: a clean architecture, explicit business rules, and a simple REST API for deposits, withdrawals, balances, and transaction history.

## Requirements

- Java 21
- Maven Wrapper included in the repository

## Run

```bash
./mvnw spring-boot:run
```

## Test

```bash
./mvnw test
```

## Coverage

Generate the test coverage report with:

```bash
./mvnw clean test
```

Open the HTML report at:

`target/site/jacoco/index.html`

## API

Implemented endpoints:

- `POST /accounts/{accountId}/deposits`
- `POST /accounts/{accountId}/withdrawals`
- `GET /accounts/{accountId}/balance`
- `GET /accounts/{accountId}/transactions`

## Available Accounts

The application starts with five predefined accounts:

- `SYSTEM`
- `ACCOUNT_1`
- `ACCOUNT_2`
- `ACCOUNT_3`
- `ACCOUNT_4`

`SYSTEM` represents the internal system account used to describe funding semantics in the ledger model.

## Manual Scenarios

Runnable HTTP examples live in [http/accounts.http](/Users/williamcisotto/workspace/tiny-ledger/ledger/http/accounts.http).

The file includes:

- deposit into an account
- withdraw from a funded account
- get current balance
- get transaction history
- unknown account
- insufficient funds

## Architecture

The implementation follows a clear layered architecture:

- Domain layer: core entities, value objects, repository contracts, and business rules
- Application layer: command/query handlers and supporting coordination contracts
- Infrastructure layer: REST controllers, in-memory adapters, bootstrap configuration, and concurrency adapters

### Domain Layer

The domain contains the business concepts:

- `AccountId`
- `Amount`
- `Transaction`
- `TransactionType`
- `AccountBalance`
- `AccountTransactionHistory`

Core business rules:

- account must exist
- amount must be greater than zero
- withdrawals cannot make the balance negative
- transactions are immutable once accepted

### Application Layer

The application layer orchestrates the use cases:

- deposit
- withdrawal
- get balance
- get transactions

It also contains supporting technical contracts needed by the use cases, such as the per-account operation guard.

### Infrastructure Layer

The infrastructure layer adapts the application to the outside world:

- Spring Boot REST API
- in-memory repository implementations
- predefined account bootstrap
- per-account locking implementation

## Design Patterns

The project intentionally uses a small set of patterns.

### Repository

Where:

- `AccountRepository`
- `BalanceRepository`
- `TransactionHistoryRepository`

Why:

- keeps storage access behind small contracts
- allows the application flow to ignore in-memory storage details

### Value Object

Where:

- `AccountId`
- `Amount`

Why:

- centralizes validation and invariants
- avoids spreading raw strings and `BigDecimal` handling through the codebase

### Ports and Adapters

Where:

- application code depends on interfaces
- infrastructure provides Spring and in-memory implementations

Why:

- keeps the core model isolated from delivery and storage concerns
- makes the architecture easier to explain and evolve

### Command / Query Separation

Where:

- write side: deposit and withdrawal
- read side: balance and transaction history

Why:

- keeps responsibilities explicit
- simplifies reasoning about state changes versus reads

### Factory Method

Where:

- `Transaction.deposit(...)`
- `Transaction.withdrawal(...)`

Why:

- makes transaction creation explicit
- prevents ambiguous or partially initialized transaction construction

## Balance Strategy

The ledger keeps two in-memory data structures per account:

- transaction history for auditability
- current balance projection for fast reads

Current balance is intentionally stored separately instead of being recalculated from the full transaction list on every request.

Why:

- `GET /accounts/{accountId}/balance` stays O(1)

Trade-off:

- transaction history and balance projection must be updated together
- this is handled with per-account write serialization inside a single application instance

## Concurrency and Scope

This project is intentionally single-node and in-memory.

What it does:

- uses a per-account in-memory lock to serialize concurrent writes for the same account

What it does not try to provide:

- database transactions
- distributed atomicity
- multi-instance consistency
- persistence across restarts

## Scenarios Covered

These are the scenarios currently documented for manual verification:

- deposit into an empty account
- multiple deposits into the same account
- withdraw from a funded account
- withdraw exact available balance
- withdraw with insufficient funds
- read balance after a sequence of operations
- read transactions after a sequence of operations
- access an unknown account
- duplicate `referenceId` retry if that enhancement is added later

## Current Scope

Included:

- in-memory storage
- predefined accounts
- clear layered architecture
- REST API
- unit and integration-style tests for the main behaviors

Explicitly out of scope:

- authentication and authorization
- logging and monitoring
- persistence
- distributed transactions
- production-grade fault tolerance
