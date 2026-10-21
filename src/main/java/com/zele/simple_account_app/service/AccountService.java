package com.zele.simple_account_app.service;

import com.zele.simple_account_app.entities.Transaction;
import com.zele.simple_account_app.entities.api.DefaultAPIResponse;
import com.zele.simple_account_app.entities.dto.AccountDTO;
import com.zele.simple_account_app.entities.dto.CreateTransactionRequest;
import com.zele.simple_account_app.entities.dto.TransactionDTO;

public interface AccountService {
    DefaultAPIResponse<TransactionDTO> debitAccount(CreateTransactionRequest request, String idempotencyKey, String accountNumber);
    DefaultAPIResponse<TransactionDTO> creditAccount(CreateTransactionRequest request, String idempotencyKey, String accountNumber);
    DefaultAPIResponse<AccountDTO> createAccount();
}
