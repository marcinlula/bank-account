package com.marcinlula.bankaccount.domain;

import com.marcinlula.bankaccount.domain.model.BankAccount;
import com.marcinlula.bankaccount.domain.model.Operation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.marcinlula.bankaccount.domain.model.Operation.Type.DEPOSIT;
import static com.marcinlula.bankaccount.domain.model.Operation.Type.WITHDRAWAL;
import static java.math.BigDecimal.ZERO;

class BankAccountServiceImplTest {

    //happy path test - use case 1
    @Test
    public void depositOperation_shouldBeSavedInRepository() {
        //given
        UUID userId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        BigDecimal amount = BigDecimal.TEN;
        Clock clock = Clock.systemUTC();
        LocalDate now = LocalDate.now(clock);
        Optional<BankAccount> bankAccount = Optional.of(new BankAccount(userId, accountId));
        BankAccountRepository bankAccountRepository = Mockito.mock(BankAccountRepository.class);
        Mockito.when(bankAccountRepository.getAccount(userId, accountId))
                .thenReturn(bankAccount);
        ArgumentCaptor<BankAccount> bankAccountArgumentCaptor = ArgumentCaptor.forClass(BankAccount.class);
        BankAccountService bankAccountService = new BankAccountServiceImpl(bankAccountRepository, clock);

        //when
        //Use case 1
        bankAccountService.deposit(userId, accountId, amount);

        //then
        Mockito.verify(bankAccountRepository, Mockito.times(1))
                .save(bankAccountArgumentCaptor.capture());

        BankAccount savedBankAccount = bankAccountArgumentCaptor.getValue();
        List<Operation> savedOperations = savedBankAccount.getOperations();
        Assertions.assertEquals(1, savedOperations.size());
        Operation savedOperation = savedOperations.getFirst();

        Assertions.assertEquals(DEPOSIT, savedOperation.operationType());
        Assertions.assertEquals(now, savedOperation.date());
        Assertions.assertEquals(amount, savedOperation.amount());
        Assertions.assertEquals(amount, savedOperation.balance());
    }

    //happy path test - use case 2
    @Test
    public void withdrawalOperation_shouldBeSavedInRepository() {
        //given
        UUID userId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        BigDecimal amount = BigDecimal.TEN;
        Clock clock = Clock.systemUTC();
        LocalDate now = LocalDate.now(clock);
        Optional<BankAccount> bankAccount = Optional.of(new BankAccount(userId, accountId));
        BankAccountRepository bankAccountRepository = Mockito.mock(BankAccountRepository.class);
        Mockito.when(bankAccountRepository.getAccount(userId, accountId))
                .thenReturn(bankAccount);
        ArgumentCaptor<BankAccount> bankAccountArgumentCaptor = ArgumentCaptor.forClass(BankAccount.class);
        BankAccountService bankAccountService = new BankAccountServiceImpl(bankAccountRepository, clock);

        //when
        //Use case 2
        bankAccountService.withdrawal(userId, accountId, amount);

        //then
        Mockito.verify(bankAccountRepository, Mockito.times(1))
                .save(bankAccountArgumentCaptor.capture());

        BankAccount savedBankAccount = bankAccountArgumentCaptor.getValue();
        List<Operation> savedOperations = savedBankAccount.getOperations();
        Assertions.assertEquals(1, savedOperations.size());
        Operation savedOperation = savedOperations.getFirst();

        Assertions.assertEquals(WITHDRAWAL, savedOperation.operationType());
        Assertions.assertEquals(now, savedOperation.date());
        Assertions.assertEquals(amount, savedOperation.amount());
        Assertions.assertEquals(amount.negate(), savedOperation.balance());
    }

    //happy path test - use case 3
    @Test
    public void depositAndWithdrawalOperation_shouldBeVisibleInAccountHistory() {
        //given
        UUID userId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        BigDecimal amount = BigDecimal.TEN;
        Clock clock = Clock.systemUTC();
        LocalDate now = LocalDate.now(clock);
        Optional<BankAccount> bankAccount = Optional.of(new BankAccount(userId, accountId));
        BankAccountRepository bankAccountRepository = Mockito.mock(BankAccountRepository.class);
        Mockito.when(bankAccountRepository.getAccount(userId, accountId))
                .thenReturn(bankAccount);
        BankAccountService bankAccountService = new BankAccountServiceImpl(bankAccountRepository, clock);

        //when
        bankAccountService.deposit(userId, accountId, amount);
        bankAccountService.withdrawal(userId, accountId, amount);
        //when Use case 3
        Optional<List<Operation>> accountHistory = bankAccountService.getAccountHistory(userId, accountId);

        //then
        Assertions.assertTrue(accountHistory.isPresent());
        Assertions.assertEquals(2, accountHistory.get().size());

        Assertions.assertEquals(DEPOSIT, accountHistory.get().get(0).operationType());
        Assertions.assertEquals(now, accountHistory.get().get(0).date());
        Assertions.assertEquals(amount, accountHistory.get().get(0).amount());
        Assertions.assertEquals(amount, accountHistory.get().get(0).balance());

        Assertions.assertEquals(WITHDRAWAL, accountHistory.get().get(1).operationType());
        Assertions.assertEquals(now, accountHistory.get().get(1).date());
        Assertions.assertEquals(amount, accountHistory.get().get(1).amount());
        Assertions.assertEquals(ZERO, accountHistory.get().get(1).balance());
    }

    @Test
    public void depositWithNegativeAmount_shouldThrowException() {
        //given
        UUID userId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        BigDecimal amount = BigDecimal.TEN.negate();
        Clock clock = Clock.systemUTC();
        BankAccountRepository bankAccountRepository = Mockito.mock(BankAccountRepository.class);
        BankAccountService bankAccountService = new BankAccountServiceImpl(bankAccountRepository, clock);

        //when
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () ->
                bankAccountService.deposit(userId, accountId, amount));

        //then
        Assertions.assertEquals("Amount cannot be negative", exception.getMessage());
    }

    @Test
    public void withdrawalWithNegativeAmount_shouldThrowException() {
        //given
        UUID userId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        BigDecimal amount = BigDecimal.TEN.negate();
        Clock clock = Clock.systemUTC();
        BankAccountRepository bankAccountRepository = Mockito.mock(BankAccountRepository.class);
        BankAccountService bankAccountService = new BankAccountServiceImpl(bankAccountRepository, clock);

        //when
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () ->
                bankAccountService.withdrawal(userId, accountId, amount));

        //then
        Assertions.assertEquals("Amount cannot be negative", exception.getMessage());
    }

    @Test
    public void depositToNonExistingAccount_shouldThrowException() {
        //given
        UUID userId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        BigDecimal amount = BigDecimal.TEN;
        Clock clock = Clock.systemUTC();
        BankAccountRepository bankAccountRepository = Mockito.mock(BankAccountRepository.class);
        Mockito.when(bankAccountRepository.getAccount(userId, accountId))
                .thenReturn(Optional.empty());
        BankAccountService bankAccountService = new BankAccountServiceImpl(bankAccountRepository, clock);

        //when
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () ->
                bankAccountService.deposit(userId, accountId, amount));

        //then
        Assertions.assertEquals("Account not found", exception.getMessage());
    }

}
