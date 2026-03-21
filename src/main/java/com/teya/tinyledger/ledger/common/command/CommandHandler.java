package com.teya.tinyledger.ledger.common.command;

public interface CommandHandler<C, R> {

    R handle(C command);
}
