package test;

import com.bitvavo.engine.match.MatchResult;
import com.bitvavo.engine.model.MatchDetail;
import com.bitvavo.engine.model.Order;
import com.bitvavo.engine.model.Status;
import com.bitvavo.engine.model.Type;
import com.bitvavo.engine.processor.MatchEngine;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;


class MatchEngineTest {

    static Long USER_A = 34567890L;
    long sequenceId = 0;
    MatchEngine engine = new MatchEngine();

    @Test
    void whenProcessOrdersThenNoMatching() {
        List<Order> orders = List.of(
                createOrder(Type.BUY, "99.00", "1000.00"),
                createOrder(Type.BUY, "99.00", "500.00"),
                createOrder(Type.BUY, "98.00", "1200.00"),
                createOrder(Type.SELL, "101.00", "2000.00"));

        List<MatchDetail> matches = new ArrayList<>();

        for (Order order : orders) {
            MatchResult mr = this.engine.processOrder(order.getSequenceId(), order);
            matches.addAll(mr.matchDetails);
        }

        assertArrayEquals(Collections.emptyList().toArray(), matches.toArray());
    }

    @Test
    void whenProcessOrdersThenMatchingResults() {
        List<Order> orders = List.of(
                createOrder(Type.BUY, "99.00", "1000.00"),
                createOrder(Type.BUY, "99.00", "500.00"),
                createOrder(Type.BUY, "98.00", "1200.00"),
                createOrder(Type.SELL, "95.00", "2000.00"));

        List<MatchDetail> matches = new ArrayList<>();

        for (Order order : orders) {
            MatchResult mr = this.engine.processOrder(order.getSequenceId(), order);
            matches.addAll(mr.matchDetails);
        }

        assertArrayEquals(new MatchDetail[]{
                new MatchDetail(fromBigDecimalToString("99.00"), fromBigDecimalToString("1000.00"), orders.get(3), orders.get(0)),
                new MatchDetail(fromBigDecimalToString("99.00"), fromBigDecimalToString("500.00"), orders.get(3), orders.get(1)),
                new MatchDetail(fromBigDecimalToString("98.00"), fromBigDecimalToString("500.00"), orders.get(3), orders.get(2)),
        }, matches.toArray(MatchDetail[]::new));
    }

    Order createOrder(Type type, String price, String quantity) {
        this.sequenceId++;
        var order = new Order();
        order.setId(this.sequenceId << 4);
        order.setSequenceId(this.sequenceId);
        order.setType(type);
        order.setPrice(fromBigDecimalToString(price));
        order.setQuantity(fromBigDecimalToString(quantity));
        order.setStatus(Status.CREATED);
        order.setUserId(USER_A);
        order.setCreatedTime(Timestamp.from(Instant.now()));
        return order;
    }

    BigDecimal fromBigDecimalToString(String s) {
        return new BigDecimal(s);
    }

}
