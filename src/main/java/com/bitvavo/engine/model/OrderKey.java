package com.bitvavo.engine.model;

import java.math.BigDecimal;

public record OrderKey(long sequenceId, BigDecimal price) {
}
