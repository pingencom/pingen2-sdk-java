package com.pingen.sdk.models.letter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Specifies the print mode for the letter.
 */
public enum PrintMode {
    SIMPLEX("simplex"),
    DUPLEX("duplex");

    private final String value;

    PrintMode(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static PrintMode fromValue(String value) {
        if (value == null) return null;
        for (PrintMode m : values()) {
            if (m.value.equalsIgnoreCase(value)) return m;
        }
        return null;
    }

    @Override
    public String toString() {
        return value;
    }
}
