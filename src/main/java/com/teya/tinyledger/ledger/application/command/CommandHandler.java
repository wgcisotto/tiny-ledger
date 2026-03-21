package com.teya.tinyledger.ledger.application.command;

public interface CommandHandler<C, R> {

    R handle(C command);
}
