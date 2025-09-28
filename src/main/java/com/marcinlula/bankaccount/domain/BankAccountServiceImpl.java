package com.marcinlula.bankaccount.domain;

import com.marcinlula.bankaccount.domain.model.BankAccount;
import com.marcinlula.bankaccount.domain.model.Operation;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static com.marcinlula.bankaccount.domain.model.Operation.Type.DEPOSIT;
import static com.marcinlula.bankaccount.domain.model.Operation.Type.WITHDRAWAL;

class BankAccountServiceImpl implements BankAccountService {

    private final BankAccountRepository bankAccountRepository;

    private final Clock clock;

    public BankAccountServiceImpl(BankAccountRepository bankAccountRepository, Clock clock) {
        this.bankAccountRepository = bankAccountRepository;
        this.clock = clock;
    }

    @Override
    public void deposit(UUID userId, UUID accountId, BigDecimal amount) {
        addOperation(userId, accountId, amount, DEPOSIT);
    }

    @Override
    public void withdrawal(UUID userId, UUID accountId, BigDecimal amount) {
        addOperation(userId, accountId, amount, WITHDRAWAL);
    }

    @Override
    public List<Operation> getAccountHistory(UUID userId, UUID accountId) {
        return bankAccountRepository.getAccount(userId, accountId).getOperations();
    }

    private void addOperation(UUID userId, UUID accountId, BigDecimal amount, Operation.Type type) {
        BankAccount account = bankAccountRepository.getAccount(userId, accountId);
        BigDecimal lastBalance = account.getLastBalance();
        BigDecimal newBalance = type == DEPOSIT
                ? lastBalance.add(amount)
                : lastBalance.add(amount.negate());
        LocalDate date = LocalDate.now(clock);
        Operation operation = new Operation(type, date, amount, newBalance);
        account.addOperation(operation);
        bankAccountRepository.save(account);
    }

}
