package com.bitvavo.exchange.match;

import com.bitvavo.exchange.model.MatchDetail;
import com.bitvavo.exchange.model.Order;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MatchResult {
    public final Order takerOrder;
    public final List<MatchDetail> matchDetails = new ArrayList<>();

    public MatchResult(Order takerOrder) {
        this.takerOrder = takerOrder;
    }

    public void add(BigDecimal price, BigDecimal matchedQuantity, Order makerOrder) {
        matchDetails.add(new MatchDetail(price, matchedQuantity, this.takerOrder, makerOrder));
    }

    @Override
    public String toString() {
        if (matchDetails.isEmpty()) {
            return "no matches.";
        }
        return matchDetails.size() + " matched: "
                + String.join(", ", this.matchDetails.stream().map(MatchDetail::toString).toArray(String[]::new));
    }
}
