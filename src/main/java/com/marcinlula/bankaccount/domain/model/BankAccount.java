package com.marcinlula.bankaccount.domain.model;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import static java.util.Collections.unmodifiableList;

public class BankAccount {

    private final UUID userId;
    private final UUID accountId;
    private final List<Operation> operations;

    public BankAccount(UUID userId, UUID accountId, List<Operation> operations) {
        this.userId = userId;
        this.accountId = accountId;
        this.operations = new LinkedList<>(operations);
    }

    public BankAccount(UUID userId, UUID accountId) {
        this.userId = userId;
        this.accountId = accountId;
        this.operations = new LinkedList<>();
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public BigDecimal getLastBalance() {
        return operations.isEmpty()
                ? BigDecimal.ZERO
                : operations.get(operations.size() - 1).balance();
    }

    public void addOperation(Operation operation) {
        operations.add(operation);
    }

    public List<Operation> getOperations() {
        return unmodifiableList(operations);
    }

}
