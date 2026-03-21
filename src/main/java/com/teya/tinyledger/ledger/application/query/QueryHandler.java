package com.teya.tinyledger.ledger.application.query;

public interface QueryHandler<Q, R> {

    R handle(Q query);
}
