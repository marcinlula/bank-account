package com.marcinlula.bankaccount.domain;

import com.marcinlula.bankaccount.domain.model.BankAccount;
import com.marcinlula.bankaccount.domain.model.Operation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static com.marcinlula.bankaccount.domain.model.Operation.Type.DEPOSIT;
import static com.marcinlula.bankaccount.domain.model.Operation.Type.WITHDRAWAL;
import static java.math.BigDecimal.ZERO;

class BankAccountServiceImplTest {



    @Test
    public void test() {
        //given
        UUID userId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        BigDecimal amount = BigDecimal.TEN;
        Clock clock = Clock.systemUTC();
        LocalDate now = LocalDate.now(clock);
        BankAccount bankAccount = new BankAccount(userId, accountId);
        BankAccountRepository bankAccountRepository = Mockito.mock(BankAccountRepository.class);
        Mockito.when(bankAccountRepository.getAccount(userId, accountId))
                .thenReturn(bankAccount);
        BankAccountService bankAccountService = new BankAccountServiceImpl(bankAccountRepository, clock);

        //when
        bankAccountService.deposit(userId, accountId, amount);
        bankAccountService.withdrawal(userId, accountId, amount);
        List<Operation> accountHistory = bankAccountService.getAccountHistory(userId, accountId);

        //then
        Assertions.assertEquals(2, accountHistory.size());

        Assertions.assertEquals(DEPOSIT, accountHistory.get(0).operationType());
        Assertions.assertEquals(now, accountHistory.get(0).date());
        Assertions.assertEquals(amount, accountHistory.get(0).amount());
        Assertions.assertEquals(amount, accountHistory.get(0).balance());

        Assertions.assertEquals(WITHDRAWAL, accountHistory.get(1).operationType());
        Assertions.assertEquals(now, accountHistory.get(1).date());
        Assertions.assertEquals(amount, accountHistory.get(1).amount());
        Assertions.assertEquals(ZERO, accountHistory.get(1).balance());
    }

}
