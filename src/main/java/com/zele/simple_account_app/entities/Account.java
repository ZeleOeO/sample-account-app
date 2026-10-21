package com.zele.simple_account_app.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
public class Account {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    @Column(unique = true, nullable = false, updatable = false)
    private String account_number;

    @Version private Long version;

    @Column(nullable = false)
    private Instant createdAt;

    private Instant updatedAt;

    @PrePersist void onCreate() { this.createdAt = Instant.now(); }
    @PreUpdate void onUpdate() { this.createdAt = Instant.now(); }
}
