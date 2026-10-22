package com.zele.simple_account_app.service;

import com.zele.simple_account_app.entities.api.DefaultAPIResponse;
import com.zele.simple_account_app.entities.dto.AccountDTO;
import com.zele.simple_account_app.entities.dto.CreateTransactionRequest;
import com.zele.simple_account_app.entities.dto.TransactionDTO;

public interface AccountService {
    DefaultAPIResponse<TransactionDTO> deflateAccount(CreateTransactionRequest request, String idempotencyKey, String accountNumber);

    DefaultAPIResponse<TransactionDTO> inflateAccount(CreateTransactionRequest request, String idempotencyKey, String accountNumber);

    DefaultAPIResponse<AccountDTO> createAccount();

    DefaultAPIResponse<AccountDTO> getAccount(String accountNumber);
}
