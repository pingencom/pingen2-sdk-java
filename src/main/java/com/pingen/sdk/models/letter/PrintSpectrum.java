package com.pingen.sdk.models.letter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Specifies the print spectrum for the letter.
 */
public enum PrintSpectrum {
    COLOR("color"),
    GRAYSCALE("grayscale");

    private final String value;

    PrintSpectrum(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static PrintSpectrum fromValue(String value) {
        if (value == null) return null;
        for (PrintSpectrum s : values()) {
            if (s.value.equalsIgnoreCase(value)) return s;
        }
        return null;
    }

    @Override
    public String toString() {
        return value;
    }
}
