package com.pingen.sdk.models.batch;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Indicates how a batch was created (via the Pingen web app or the API).
 */
public enum BatchSource {
    APP("app"),
    API("api");

    private final String value;

    BatchSource(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static BatchSource fromValue(String value) {
        if (value == null) return null;
        for (BatchSource s : values()) {
            if (s.value.equalsIgnoreCase(value)) return s;
        }
        return null;
    }

    @Override
    public String toString() {
        return value;
    }
}
