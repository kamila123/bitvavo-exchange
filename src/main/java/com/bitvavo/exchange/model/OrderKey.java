package com.bitvavo.exchange.model;

import java.math.BigDecimal;

public record OrderKey(long sequenceId, BigDecimal price) {
}
