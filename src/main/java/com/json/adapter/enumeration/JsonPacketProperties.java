package com.json.adapter.enumeration;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum JsonPacketProperties {
    MSG_ID("msgId"),
    OPERATION("operation"),
    TYPE("type"),
    TIMESTAMP("timestamp"),
    NAME("name"),
    OUTCOME("outcome"),
    MARKET_ID("marketId"),
    OUTCOME_ID("outcomeId"),
    PRICE("price"),
    DISPLAYED("displayed"),
    SUSPENDED("suspended"),
    MARKET("market"),
    EVENT("event"),
    EVENT_ID("eventId"),
    CATEGORY("category"),
    SUB_CATEGORY("subCategory"),
    START_TIME("startTime"),
    BODY("body");

    private final String value;

    public String getValue() {
        return value;
    }
}
