package com.zele.simple_account_app.exceptions;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(String accountNumber) {
        super("Account Not Found for "+ accountNumber);
    }
}
