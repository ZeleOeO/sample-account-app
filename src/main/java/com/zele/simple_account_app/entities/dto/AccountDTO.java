package com.zele.simple_account_app.entities.dto;

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
}
