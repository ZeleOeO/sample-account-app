package com.zele.simple_account_app.exceptions;

import com.zele.simple_account_app.entities.dto.TransactionDTO;
import lombok.Getter;

public class TransactionAlreadyExistsException extends RuntimeException {
    @Getter
    private TransactionDTO transactionDTO;
    public TransactionAlreadyExistsException(TransactionDTO transactionDTO) {
        super("Transaction already exists");
        this.transactionDTO = transactionDTO;
    }
}
