package com.pingen.sdk.models.letter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Specifies where the address window is located on the letter.
 */
public enum AddressPosition {
    LEFT("left"),
    RIGHT("right");

    private final String value;

    AddressPosition(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static AddressPosition fromValue(String value) {
        if (value == null) return null;
        for (AddressPosition p : values()) {
            if (p.value.equalsIgnoreCase(value)) return p;
        }
        return null;
    }

    @Override
    public String toString() {
        return value;
    }
}
