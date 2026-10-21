package com.zele.simple_account_app.service.impl;

import com.zele.simple_account_app.entities.api.DefaultAPIResponse;
import com.zele.simple_account_app.service.AccountService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@AllArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService {

    @Override
    public DefaultAPIResponse<Object> debitAccount(int amount, int id) {
        return null;
    }

    @Override
    public DefaultAPIResponse<Object> creditAccount(int amount, int id) {
        return null;
    }

    @Override
    public DefaultAPIResponse<Object> createAccount() {
        return null;
    }


}

