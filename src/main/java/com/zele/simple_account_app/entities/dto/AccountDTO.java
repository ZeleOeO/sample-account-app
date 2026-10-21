package com.zele.simple_account_app.entities.dto;

import com.zele.simple_account_app.entities.Account;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDTO {
    String accountNumber;
    BigDecimal balance;
    Instant createdAt;
    Instant updatedAt;

    public static AccountDTO from(Account account) {
        return new AccountDTO(account.getAccountNumber(), account.getBalance(), account.getCreatedAt(), account.getUpdatedAt());
    }
}
