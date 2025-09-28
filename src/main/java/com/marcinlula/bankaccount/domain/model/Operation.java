package com.marcinlula.bankaccount.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record Operation(
        Type operationType,
        LocalDate date,
        BigDecimal amount,
        BigDecimal balance
) {

    public enum Type {
        DEPOSIT, WITHDRAWAL
    }

}
