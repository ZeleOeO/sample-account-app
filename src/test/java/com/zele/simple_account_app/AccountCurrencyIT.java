package com.zele.simple_account_app;

import com.zele.simple_account_app.entities.Account;
import com.zele.simple_account_app.entities.dto.CreateTransactionRequest;
import com.zele.simple_account_app.exceptions.InsufficientFundsException;
import com.zele.simple_account_app.repositories.AccountRepository;
import com.zele.simple_account_app.repositories.TransactionRepository;
import com.zele.simple_account_app.service.impl.AccountServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
public class AccountCurrencyIT {

    @Container
    static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:latest");

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        dynamicPropertyRegistry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        dynamicPropertyRegistry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        dynamicPropertyRegistry.add("spring.docker.compose.enabled", () -> "false");
    }


    @Autowired
    AccountServiceImpl service;
    @Autowired
    AccountRepository accountRepo;
    @Autowired
    TransactionRepository txRepo;

    @Test
    void concurrentDeflates_neverOverDraw() throws Exception {
        Account account = new Account();
        String accountNumber = account.getAccountNumber();
        service.inflateAccount(transactionRequest(BigDecimal.valueOf(100.00)), UUID.randomUUID().toString(), accountNumber);
        assertThat(accountRepo.findAccountByAccountNumber(accountNumber).orElseThrow().getBalance())
                .isEqualByComparingTo(BigDecimal.valueOf(100.00));

        int threads = 10;
        BigDecimal debitAmount = new BigDecimal("30.00");
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch start = new CountDownLatch(1);
        AtomicInteger successes = new AtomicInteger();
        AtomicInteger insufficient = new AtomicInteger();
        List<Future<?>> futures = new ArrayList<>();

        for (int i = 0; i < threads; i++) {
            String idempKey = "key-" + i;
            futures.add(pool.submit(() -> {
                start.await();
                try {
                    service.deflateAccount(transactionRequest(debitAmount), idempKey, accountNumber);
                    successes.incrementAndGet();
                } catch (InsufficientFundsException e) {
                    insufficient.incrementAndGet();
                }
                return null;
            }));
        }

        start.countDown();
        for (Future<?> future : futures) future.get(30, TimeUnit.SECONDS);
        pool.shutdown();

        assertThat(successes.get()).isEqualTo(3);
        assertThat(insufficient.get()).isEqualTo(7);
        Account reloadedAccount = accountRepo.findAccountByAccountNumber(accountNumber).orElseThrow();
        assertThat(reloadedAccount.getBalance()).isEqualTo(BigDecimal.valueOf(10.00));
        assertThat(txRepo.countByAccountId(reloadedAccount.getId())).isEqualTo(4L);


    }

    private static CreateTransactionRequest transactionRequest(BigDecimal amount) {
        CreateTransactionRequest createTransactionRequest = new CreateTransactionRequest();
        createTransactionRequest.setAmount(amount);
        return createTransactionRequest;
    }
}
