package com.zele.simple_account_app.service.impl;

import com.zele.simple_account_app.entities.Account;
import com.zele.simple_account_app.entities.api.DefaultAPIResponse;
import com.zele.simple_account_app.entities.dto.AccountDTO;
import com.zele.simple_account_app.entities.dto.CreateTransactionRequest;
import com.zele.simple_account_app.repositories.AccountRepository;
import com.zele.simple_account_app.service.AccountService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;

    @Override
    public DefaultAPIResponse<Object> debitAccount(int amount, int id) {
        return null;
    }

    @Override
    public DefaultAPIResponse<Object> creditAccount(int amount, int id) {
        return null;
    }

    @Override
    @Transactional
    public DefaultAPIResponse<Object> createAccount() {
        // NOTE: I could do a validation check, just incase and have it retry a couple time
        // to check and make sure the account doesn't already exist, but that's overkill for 10!
        // I also opted not to use a try catch because of transaction rollback
       Account account = new Account();
       account = accountRepository.save(account);
       return new DefaultAPIResponse<>(HttpStatus.CREATED.value(), "Account Created", AccountDTO.from(account));
    }

}

