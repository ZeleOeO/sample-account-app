package com.zele.simple_account_app.service;

import com.zele.simple_account_app.entities.api.DefaultAPIResponse;

public interface AccountService {
    DefaultAPIResponse<Object> debitAccount(int amount, int id);
    DefaultAPIResponse<Object> creditAccount(int amount, int id);
    DefaultAPIResponse<Object> createAccount();
}
