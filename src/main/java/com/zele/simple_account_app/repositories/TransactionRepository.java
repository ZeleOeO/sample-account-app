package com.zele.simple_account_app.repositories;

import com.zele.simple_account_app.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
