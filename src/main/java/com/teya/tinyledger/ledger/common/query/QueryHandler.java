package com.teya.tinyledger.ledger.common.query;

public interface QueryHandler<Q, R> {

    R handle(Q query);
}
