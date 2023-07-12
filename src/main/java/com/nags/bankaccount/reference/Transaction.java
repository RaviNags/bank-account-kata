package com.nags.bankaccount.reference;

import java.math.BigDecimal;
import java.time.Instant;

public record Transaction(Operation operation, Instant date, BigDecimal amount, BigDecimal balance) {
}
