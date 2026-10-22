package com.zele.simple_account_app.controller;

import com.zele.simple_account_app.entities.api.DefaultAPIResponse;
import com.zele.simple_account_app.entities.dto.AccountDTO;
import com.zele.simple_account_app.entities.dto.CreateTransactionRequest;
import com.zele.simple_account_app.entities.dto.TransactionDTO;
import com.zele.simple_account_app.service.AccountService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/accounts")
public class AccountController {
    private final AccountService accountService;

    @PostMapping("/")
    public ResponseEntity<DefaultAPIResponse<AccountDTO>> createAccount() {
        return ResponseEntity.status(HttpStatus.CREATED.value()).body(accountService.createAccount());
    }

    @PostMapping("/deflate/{accountNumber}")
    public ResponseEntity<DefaultAPIResponse<TransactionDTO>> deflateAccount(
            @PathVariable String accountNumber,
            @RequestBody CreateTransactionRequest request,
            @RequestHeader String idempotencyKey
    ) {
        return ResponseEntity.status(HttpStatus.OK.value()).body(accountService.deflateAccount(request, idempotencyKey, accountNumber));
    }

    @PostMapping("/inflate/{account}")
    public ResponseEntity<DefaultAPIResponse<TransactionDTO>> inflateAccount(
            @PathVariable String account,
            @RequestBody CreateTransactionRequest request,
            @RequestHeader("Idempotency-Key") String idempotencyKey
    ) {
        return ResponseEntity.status(HttpStatus.OK.value()).body(accountService.inflateAccount(request, idempotencyKey, account));
    }
}
