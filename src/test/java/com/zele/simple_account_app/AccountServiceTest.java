package com.zele.simple_account_app;


import com.zele.simple_account_app.entities.Account;
import com.zele.simple_account_app.repositories.AccountRepository;
import com.zele.simple_account_app.repositories.TransactionRepository;
import com.zele.simple_account_app.service.AccountService;
import com.zele.simple_account_app.service.impl.AccountServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    AccountRepository accountRepo;
    @Mock
    TransactionRepository txRepo;
    @InjectMocks
    AccountServiceImpl service;

    @Test
    void createAccount_persistsAndReturnsAccountNumber() {
        when(accountRepo.save(any(Account.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        var resp = service.createAccount();

        assertThat(resp.getData()).isNotNull();
        verify(accountRepo).save(any(Account.class));
    }
}
