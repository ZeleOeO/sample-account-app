package com.zele.simple_account_app.exceptions;


import com.zele.simple_account_app.entities.api.DefaultAPIResponse;
import com.zele.simple_account_app.entities.dto.TransactionDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<DefaultAPIResponse<Object>> handleAccountNotFound(AccountNotFoundException e) {
        log.error("Account not Found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new DefaultAPIResponse(HttpStatus.NOT_FOUND.value(), e.getMessage(), null));
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<DefaultAPIResponse<Object>> handleInsufficientFunds(InsufficientFundsException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new DefaultAPIResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), null));
    }

    @ExceptionHandler(InvalidAmountException.class)
    public ResponseEntity<DefaultAPIResponse<Object>> handleInvalidAmountException(InvalidAmountException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new DefaultAPIResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), null));
    }

    @ExceptionHandler(TransactionAlreadyExistsException.class)
    public ResponseEntity<DefaultAPIResponse<TransactionDTO>> handleTransactionAlreadyExists(TransactionAlreadyExistsException e) {
        log.warn("Transaction already exists");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new DefaultAPIResponse(HttpStatus.CONFLICT.value(), e.getMessage(), e.getTransactionDTO()));
    }

    // General Exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<DefaultAPIResponse<Object>> handleGeneralExceptions(Exception e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).body(new DefaultAPIResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred", null));
    }
}
