package com.bitvavo.exchange.model;

import java.math.BigDecimal;

public record MatchDetail(BigDecimal price, BigDecimal quantity, Order takerOrder, Order makerOrder) {
}
