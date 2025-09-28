package com.marcinlula.bankaccount.domain;

import com.marcinlula.bankaccount.domain.model.Operation;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface BankAccountService {

    void deposit(UUID userId, UUID accountId, BigDecimal amount);

    void withdrawal(UUID userId, UUID accountId, BigDecimal amount);

    List<Operation> getAccountHistory(UUID userId, UUID accountId);
}
