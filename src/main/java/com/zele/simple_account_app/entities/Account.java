package com.zele.simple_account_app.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Entity
@Getter
@AllArgsConstructor
public class Account {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    @Setter
    @Column(unique = true, nullable = false, updatable = false)
    private String accountNumber;

    @Version private Long version;

    @Column(nullable = false)
    private Instant createdAt;

    private Instant updatedAt;

    @PrePersist void onCreate() { this.createdAt = Instant.now(); }
    @PreUpdate void onUpdate() { this.createdAt = Instant.now(); }

     public Account() {
        this.balance = BigDecimal.ZERO;
        this.accountNumber = generateAccountNumber();
    }


    private String generateAccountNumber() {
        return String.valueOf(1_000_000_000L + ThreadLocalRandom.current().nextLong(9_000_000_000L));
    }

}
