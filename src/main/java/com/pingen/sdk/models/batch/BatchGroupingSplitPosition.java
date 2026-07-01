package com.pingen.sdk.models.batch;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Specifies whether the split page/separator appears at the first or last page of each letter.
 */
public enum BatchGroupingSplitPosition {
    FIRST_PAGE("first_page"),
    LAST_PAGE("last_page");

    private final String value;

    BatchGroupingSplitPosition(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static BatchGroupingSplitPosition fromValue(String value) {
        if (value == null) return null;
        for (BatchGroupingSplitPosition t : values()) {
            if (t.value.equalsIgnoreCase(value)) return t;
        }
        return null;
    }

    @Override
    public String toString() {
        return value;
    }
}
