package com.zele.simple_account_app.entities;

import com.zele.simple_account_app.entities.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Getter
@NoArgsConstructor
@Table(indexes = @Index(columnList = "account_id"))
public class Transaction {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType transactionType;

    @Column(nullable = false, unique = true, length = 64)
     private String idempotencyKey;

    private Instant createdAt;

    @PrePersist void onCreate() { this.createdAt = Instant.now(); }

    public Transaction(Account account, BigDecimal amount, TransactionType transactionType, String idempotencyKey) {
        this.account = account;
        this.amount = amount;
        this.transactionType = transactionType;
        this.idempotencyKey = idempotencyKey;
    }
}
