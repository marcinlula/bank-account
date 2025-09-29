package com.marcinlula.bankaccount.domain;

import com.marcinlula.bankaccount.domain.model.BankAccount;

import java.util.Optional;
import java.util.UUID;

public interface BankAccountRepository {

    Optional<BankAccount> getAccount(UUID userId, UUID accountId);

    void save(BankAccount account);

}
