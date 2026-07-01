package com.pingen.sdk.models.letter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Delivery product options for letters.
 */
public enum DeliveryProduct {
    FAST("fast"),
    CHEAP("cheap"),
    BULK("bulk"),
    PREMIUM("premium"),
    REGISTERED("registered");

    private final String value;

    DeliveryProduct(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static DeliveryProduct fromValue(String value) {
        if (value == null) return null;
        for (DeliveryProduct p : values()) {
            if (p.value.equalsIgnoreCase(value)) return p;
        }
        return null;
    }

    @Override
    public String toString() {
        return value;
    }
}
