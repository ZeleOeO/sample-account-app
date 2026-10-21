package com.zele.simple_account_app.entities.dto;

import com.zele.simple_account_app.entities.Transaction;
import com.zele.simple_account_app.entities.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDTO {
    String accountNumber;
    BigDecimal amount;
    TransactionType transactionType;
    Instant createdAt;

    public static TransactionDTO from(Transaction transaction) {
        return new TransactionDTO(transaction.getAccount().getAccountNumber(), transaction.getAmount(), transaction.getTransactionType(), transaction.getCreatedAt());
    }

}
