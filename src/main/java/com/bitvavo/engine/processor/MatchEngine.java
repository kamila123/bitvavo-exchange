package com.bitvavo.engine.processor;

import com.bitvavo.engine.match.MatchResult;
import com.bitvavo.engine.model.Order;
import com.bitvavo.engine.model.OrderBook;
import com.bitvavo.engine.model.Status;
import com.bitvavo.engine.model.Type;

import java.math.BigDecimal;

import static com.bitvavo.engine.model.Type.BUY;
import static com.bitvavo.engine.model.Type.SELL;

public class MatchEngine {

    public final OrderBook buyBook = new OrderBook(Type.BUY);
    public final OrderBook sellBook = new OrderBook(Type.SELL);

    public BigDecimal marketPrice = BigDecimal.ZERO;
    private long sequenceId;

    public MatchResult processOrder(long sequenceId, Order order) {
        return switch (order.getType()) {
            case BUY -> processOrder(sequenceId, order, this.sellBook, this.buyBook);
            case SELL -> processOrder(sequenceId, order, this.buyBook, this.sellBook);
        };
    }

    /**
     * @param takerOrder
     * @param makerBook
     * @param anotherBook
     * @return MatchResult
     */
    private MatchResult processOrder(long sequenceId, Order takerOrder, OrderBook makerBook, OrderBook anotherBook) {
        this.sequenceId = sequenceId;
        long ts = takerOrder.getCreatedTime().toInstant().toEpochMilli();

        MatchResult matchResult = new MatchResult(takerOrder);
        BigDecimal takerUnfilledQuantity = takerOrder.getQuantity();

        do {
            Order makerOrder = makerBook.getFirst();

            if (makerOrder == null) {
                break;
            }

            if (takerOrder.getType() == BUY && takerOrder.getPrice().compareTo(makerOrder.getPrice()) < 0) {
                break;
            } else if (takerOrder.getType() == SELL && takerOrder.getPrice().compareTo(makerOrder.getPrice()) > 0)
                break;

            this.marketPrice = makerOrder.getPrice();

            BigDecimal matchedQuantity = takerUnfilledQuantity.min(makerOrder.getUnfilledQuantity());

            matchResult.add(makerOrder.getPrice(), matchedQuantity, makerOrder);

            takerUnfilledQuantity = takerUnfilledQuantity.subtract(matchedQuantity);

            BigDecimal makerUnfilledQuantity = makerOrder.getUnfilledQuantity().subtract(matchedQuantity);

            if (makerUnfilledQuantity.signum() == 0) {
                makerOrder.updateOrder(makerUnfilledQuantity, Status.DONE, ts);
                makerBook.remove(makerOrder);
            } else {
                makerOrder.updateOrder(makerUnfilledQuantity, Status.PROCESSING, ts);
            }
            if (takerUnfilledQuantity.signum() == 0) {
                takerOrder.updateOrder(takerUnfilledQuantity, Status.DONE, ts);
                break;
            }
        } while (true);

        if (takerUnfilledQuantity.signum() > 0) {
            takerOrder.updateOrder(takerUnfilledQuantity, takerUnfilledQuantity.compareTo(takerOrder.getQuantity()) == 0 ? Status.CREATED : Status.PROCESSING, ts);
            anotherBook.add(takerOrder);
        }
        return matchResult;
    }
}
