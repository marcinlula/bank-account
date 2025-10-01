package com.marcinlula.bankaccount.domain;

import com.marcinlula.bankaccount.domain.model.BankAccount;
import com.marcinlula.bankaccount.domain.model.Operation;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
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
    public Optional<List<Operation>> getAccountHistory(UUID userId, UUID accountId) {
        return bankAccountRepository.getAccount(userId, accountId)
                .map(BankAccount::getOperations);
    }

    private void addOperation(UUID userId, UUID accountId, BigDecimal amount, Operation.Type type) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        Optional<BankAccount> account = bankAccountRepository.getAccount(userId, accountId);
        if (account.isEmpty()) {
            throw new IllegalArgumentException("Account not found");
        }
        BigDecimal lastBalance = account.get().getLastBalance();
        BigDecimal newBalance = type == DEPOSIT
                ? lastBalance.add(amount)
                : lastBalance.add(amount.negate());
        LocalDate date = LocalDate.now(clock);
        Operation operation = new Operation(type, date, amount, newBalance);
        account.get().addOperation(operation);
        bankAccountRepository.save(account.get());
    }

}
