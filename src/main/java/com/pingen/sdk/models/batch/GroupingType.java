package com.pingen.sdk.models.batch;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Grouping strategy for batch files.
 */
public enum GroupingType {
    MERGE("merge"),
    ZIP("zip");

    private final String value;

    GroupingType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static GroupingType fromValue(String value) {
        if (value == null) return null;
        for (GroupingType t : values()) {
            if (t.value.equalsIgnoreCase(value)) return t;
        }
        return null;
    }

    @Override
    public String toString() {
        return value;
    }
}
