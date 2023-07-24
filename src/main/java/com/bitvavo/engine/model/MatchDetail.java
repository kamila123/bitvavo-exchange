package com.bitvavo.engine.model;

import java.math.BigDecimal;

public record MatchDetail(BigDecimal price, BigDecimal quantity, Order takerOrder, Order makerOrder) {
}
