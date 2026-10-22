package com.zele.simple_account_app.service.impl;

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
import com.zele.simple_account_app.repositories.AccountRepository;
import com.zele.simple_account_app.repositories.TransactionRepository;
import com.zele.simple_account_app.service.AccountService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;


    @Override
    @Transactional
    public DefaultAPIResponse<TransactionDTO> deflateAccount(CreateTransactionRequest request, String idempotencyKey, String accountNumber) {
        return new DefaultAPIResponse<>(HttpStatus.OK.value(), "Account Deflated", processTransaction(request, idempotencyKey, TransactionType.Deflate, accountNumber));
    }

    @Override
    @Transactional
    public DefaultAPIResponse<TransactionDTO> inflateAccount(CreateTransactionRequest request, String idempotencyKey, String accountNumber) {
        return new DefaultAPIResponse<>(HttpStatus.OK.value(), "Account Inflated", processTransaction(request, idempotencyKey, TransactionType.Inflate, accountNumber));
    }

    @Override
    @Transactional
    public DefaultAPIResponse<AccountDTO> createAccount() {
        // NOTE: I could do a validation check, just incase an account like that already exists,
        // and have it retry a couple time, but that's overkill
        Account account = new Account();
        account = accountRepository.save(account);
        return new DefaultAPIResponse<>(HttpStatus.CREATED.value(), "Account Created", AccountDTO.from(account));
    }

    private TransactionDTO processTransaction(CreateTransactionRequest request, String idempotencyKey, TransactionType transactionType, String accountNumber) {
        // check idempkey, we check see if this transaction has been processed before
        // idemp keys are unique to  an account
        Optional<Transaction> optionalTransaction = transactionRepository.findTransactionByIdempotencyKeyAndAccountAccountNumber(idempotencyKey, accountNumber);
        if (optionalTransaction.isPresent()) {
            return TransactionDTO.from(optionalTransaction.get());
        }

        // Check if the account exists, none of this should work if the account doesn't exist
        // this also locks the row for this transaction until it completes, see README for why i chose pessmistic over optimistic writes like version
        Account account = accountRepository.findByAccountNumberForUpdate(accountNumber).orElseThrow(() -> new AccountNotFoundException(accountNumber));

        // validate the amount, if the amount is incorrect don't bother with the database
        BigDecimal amount = request.getAmount();
        amount = setAndValidateAmount(amount);


        //check if the account has sufficient or insufficient funds
        if (transactionType == TransactionType.Deflate && account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException();
        }
        BigDecimal newBalance = transactionType == TransactionType.Inflate ? account.getBalance().add(amount) : account.getBalance().subtract(amount);

        // update balance
        account.setBalance(newBalance);

        // save transaction and account
        Transaction newTransaction = new Transaction(account, amount, transactionType, idempotencyKey);
        newTransaction = transactionRepository.save(newTransaction);
        return TransactionDTO.from(newTransaction);
    }

    private BigDecimal setAndValidateAmount(BigDecimal amount) {
        if (amount == null) throw new InvalidAmountException("Amount is required");
        if (amount.signum() <= 0) {
            throw new InvalidAmountException("Amount must be greater than 0");
        }
        if (amount.scale() > 2) {
            throw new InvalidAmountException(
                    "Amount should not be in more than 2 decimal places, make your money no loss");
        }
        return amount.setScale(2, RoundingMode.UNNECESSARY);
    }
}

