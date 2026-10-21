package com.zele.simple_account_app.repositories;

import com.zele.simple_account_app.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository  extends JpaRepository<Account, Long> {
}
