package com.bitvavo.engine.model;

import java.util.*;

public class OrderBook {
    public final Type direction;
    public final TreeMap<OrderKey, Order> book;

    public OrderBook(Type type) {
        this.direction = type;
        this.book = new TreeMap<>(type == Type.BUY ? SORT_BUY : SORT_SELL);
    }

    public Order getFirst() {
        return this.book.isEmpty() ? null : this.book.firstEntry().getValue();
    }

    public boolean remove(Order order) {
        return this.book.remove(new OrderKey(order.getSequenceId(), order.getPrice())) != null;
    }

    public boolean add(Order order) {
        return this.book.put(new OrderKey(order.getSequenceId(), order.getPrice()), order) == null;
    }

    public int size() {
        return this.book.size();
    }

    @Override
    public String toString() {
        if (this.book.isEmpty()) {
            return "-- empty --";
        }
        List<String> orders = new ArrayList<>();
        for (Map.Entry<OrderKey, Order> entry : this.book.entrySet()) {
            Order order = entry.getValue();
            orders.add("  " + order.getPrice() + " " + order.getUnfilledQuantity() + " " + order.toString());
        }
        if (direction == Type.SELL) {
            Collections.reverse(orders);
        }
        return String.join("\n", orders);
    }

    private static final Comparator<OrderKey> SORT_SELL = Comparator.comparing(OrderKey::price).thenComparingLong(OrderKey::sequenceId);

    private static final Comparator<OrderKey> SORT_BUY = (o1, o2) -> {
        int cmp = o2.price().compareTo(o1.price());
        return cmp == 0 ? Long.compare(o1.sequenceId(), o2.sequenceId()) : cmp;
    };
}
