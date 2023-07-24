package com.bitvavo.engine.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Order {

    private Type type;
    private BigDecimal quantity;
    private BigDecimal price;
    private BigDecimal unfilledQuantity;
    private Status status;
    private Timestamp createdTime;
    private long sequenceId;
    private long id;
    private long userId;

    public Order() {
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Timestamp getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Timestamp createdTime) {
        this.createdTime = createdTime;
    }

    public long getSequenceId() {
        return sequenceId;
    }

    public void setSequenceId(long sequenceId) {
        this.sequenceId = sequenceId;
    }

    public BigDecimal getUnfilledQuantity() {
        return unfilledQuantity;
    }

    public void setUnfilledQuantity(BigDecimal unfilledQuantity) {
        this.unfilledQuantity = unfilledQuantity;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void updateOrder(BigDecimal unfilledQuantity, Status status, long updatedAt) {
        this.unfilledQuantity = unfilledQuantity;
        this.status = status;
    }
}
