package com.zele.simple_account_app;

import com.zele.simple_account_app.entities.Account;
import com.zele.simple_account_app.entities.Transaction;
import com.zele.simple_account_app.entities.api.DefaultAPIResponse;
import com.zele.simple_account_app.entities.dto.AccountDTO;
import com.zele.simple_account_app.entities.dto.CreateTransactionRequest;
import com.zele.simple_account_app.entities.dto.TransactionDTO;
import com.zele.simple_account_app.entities.enums.TransactionType;
import com.zele.simple_account_app.exceptions.AccountNotFoundException;
import com.zele.simple_account_app.exceptions.InsufficientFundsException;
import com.zele.simple_account_app.exceptions.InvalidAmountException;
import com.zele.simple_account_app.exceptions.TransactionAlreadyExistsException;
import com.zele.simple_account_app.repositories.AccountRepository;
import com.zele.simple_account_app.repositories.TransactionRepository;
import com.zele.simple_account_app.service.impl.AccountServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock AccountRepository accountRepository;
    @Mock TransactionRepository transactionRepository;
    @InjectMocks
    AccountServiceImpl service;


    @Test
    void createAccount_persistsAndReturnsAccount() {
        when(accountRepository.save(any(Account.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        DefaultAPIResponse<AccountDTO> resp = service.createAccount();

        assertThat(resp.getStatus()).isEqualTo(201);
        assertThat(resp.getData()).isNotNull();
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void createAccount_generatesDistinctAccountNumbers() {
        when(accountRepository.save(any(Account.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        String first  = service.createAccount().getData().getAccountNumber();
        String second = service.createAccount().getData().getAccountNumber();

        assertThat(first).matches("\\d{10}");
        assertThat(second).matches("\\d{10}");
        assertThat(first).isNotEqualTo(second);
    }


    @Test
    void creditAccount_increasesBalance() {
        Account acc = accountWithBalance("100.00");
        when(accountRepository.findByAccountNumberForUpdate("ACC1"))
                .thenReturn(Optional.of(acc));
        when(transactionRepository.findTransactionByIdempotencyKeyAndAccountAccountNumber("key1", "ACC1"))
                .thenReturn(Optional.empty());
        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        DefaultAPIResponse<TransactionDTO> resp =
                service.inflateAccount(request("50.00"), "key1", "ACC1");

        assertThat(acc.getBalance()).isEqualByComparingTo("150.00");
        assertThat(resp.getData().getTransactionType()).isEqualTo(TransactionType.Inflate);
    }


    @Test
    void debitAccount_decreasesBalance() {
        Account acc = accountWithBalance("100.00");
        when(accountRepository.findByAccountNumberForUpdate("ACC1")).thenReturn(Optional.of(acc));
        when(transactionRepository.findTransactionByIdempotencyKeyAndAccountAccountNumber("key1", "ACC1"))
                .thenReturn(Optional.empty());
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(inv -> inv.getArgument(0));

        service.deflateAccount(request("40.00"), "key1", "ACC1");

        assertThat(acc.getBalance()).isEqualByComparingTo("60.00");
    }

    @Test
    void debitAccount_insufficientFunds_throws() {
        Account acc = accountWithBalance("30.00");
        when(accountRepository.findByAccountNumberForUpdate("ACC1")).thenReturn(Optional.of(acc));
        when(transactionRepository.findTransactionByIdempotencyKeyAndAccountAccountNumber("key1", "ACC1"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deflateAccount(request("100.00"), "key1", "ACC1"))
                .isInstanceOf(InsufficientFundsException.class);

        assertThat(acc.getBalance()).isEqualByComparingTo("30.00");
    }

    @Test
    void debitAccount_exactlyBalance_succeeds() {
        Account acc = accountWithBalance("100.00");
        when(accountRepository.findByAccountNumberForUpdate("ACC1")).thenReturn(Optional.of(acc));
        when(transactionRepository.findTransactionByIdempotencyKeyAndAccountAccountNumber("key1", "ACC1"))
                .thenReturn(Optional.empty());
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(inv -> inv.getArgument(0));

        service.deflateAccount(request("100.00"), "key1", "ACC1");

        assertThat(acc.getBalance()).isEqualByComparingTo("0.00");
    }

    @Test
    void repeatedKey_throwsTransactionAlreadyExists_andDoesNotTouchBalance() {
        Account acc = accountWithBalance("100.00");
        Transaction existing = new Transaction(
                acc,
                new BigDecimal("50.00"),
                TransactionType.Inflate,
                "key1");

        when(transactionRepository.findTransactionByIdempotencyKeyAndAccountAccountNumber("key1", "ACC1"))
                .thenReturn(Optional.of(existing));


        assertThatThrownBy(() ->service.inflateAccount(request("50.00"), "key1", "ACC1")).isInstanceOf(TransactionAlreadyExistsException.class);
        assertThat(acc.getBalance()).isEqualByComparingTo("100.00");
        verify(accountRepository, never()).findByAccountNumberForUpdate(any());
        verify(transactionRepository, never()).save(any());
    }


    @Test
    void nullAmount_throws() {
        CreateTransactionRequest req = new CreateTransactionRequest();
        req.setAmount(null);
        when(transactionRepository.findTransactionByIdempotencyKeyAndAccountAccountNumber("key1", "ACC1"))
                .thenReturn(Optional.empty());
        when(accountRepository.findByAccountNumberForUpdate("ACC1"))
                .thenReturn(Optional.of(accountWithBalance("100.00")));

        assertThatThrownBy(() -> service.deflateAccount(req, "key1", "ACC1"))
                .isInstanceOf(InvalidAmountException.class)
                .hasMessageContaining("required");
    }

    @Test
    void zeroOrNegativeAmount_throws() {
        Account acc = accountWithBalance("100.00");
        when(accountRepository.findByAccountNumberForUpdate("ACC1")).thenReturn(Optional.of(acc));
        when(transactionRepository.findTransactionByIdempotencyKeyAndAccountAccountNumber(any(), eq("ACC1")))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deflateAccount(request("0.00"), "k", "ACC1"))
                .isInstanceOf(InvalidAmountException.class);
        assertThatThrownBy(() -> service.deflateAccount(request("-1.00"), "k", "ACC1"))
                .isInstanceOf(InvalidAmountException.class);
    }

    @Test
    void tooManyDecimalPlaces_throws() {
        Account acc = accountWithBalance("100.00");
        when(accountRepository.findByAccountNumberForUpdate("ACC1")).thenReturn(Optional.of(acc));
        when(transactionRepository.findTransactionByIdempotencyKeyAndAccountAccountNumber(any(), eq("ACC1")))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deflateAccount(request("10.005"), "k", "ACC1"))
                .isInstanceOf(InvalidAmountException.class);
    }

    @Test
    void unknownAccount_throws() {
        when(transactionRepository.findTransactionByIdempotencyKeyAndAccountAccountNumber("k", "NOPE"))
                .thenReturn(Optional.empty());
        when(accountRepository.findByAccountNumberForUpdate("NOPE"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deflateAccount(request("10.00"), "k", "NOPE"))
                .isInstanceOf(AccountNotFoundException.class);
    }


    private static Account accountWithBalance(String balance) {
        Account a = new Account();
        a.setAccountNumber("ACC1");
        a.setBalance(new BigDecimal(balance));
        return a;
    }

    private static CreateTransactionRequest request(String amount) {
        CreateTransactionRequest r = new CreateTransactionRequest();
        r.setAmount(new BigDecimal(amount));
        return r;
    }
}
