package com.eventus.bookanalyser.model;

import java.util.Objects;

//use factory pattern or builder pattern
public class LimitOrderEntry {

    private Long timestamp;
    private final String orderType;
    private final String orderId;
    private final String side;
    private final Double price;
    private Integer size;


    public LimitOrderEntry(Long timestamp, String orderType, String orderId, Integer size) {
        this(timestamp, orderType, orderId, null, null, size);
    }

    public LimitOrderEntry(Long timestamp, String orderType, String orderId, String side, Double price, Integer size) {
        this.timestamp = timestamp;
        this.orderType = orderType;
        this.orderId = orderId;
        this.side = side;
        this.price = price;
        this.size = size;
    }

    // Two Orders are equal if their IDs are equal
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LimitOrderEntry limitOrderEntry = (LimitOrderEntry) o;
        return this.orderId.equalsIgnoreCase(limitOrderEntry.orderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId);
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public String getOrderType() {
        return orderType;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getSide() {
        return side;
    }

    public Double getPrice() {
        return price;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "LimitOrderEntry{" +
                "timestamp=" + timestamp +
                ", orderType='" + orderType + '\'' +
                ", orderId='" + orderId + '\'' +
                ", side='" + side + '\'' +
                ", price=" + price +
                ", size=" + size +
                '}';
    }
}
