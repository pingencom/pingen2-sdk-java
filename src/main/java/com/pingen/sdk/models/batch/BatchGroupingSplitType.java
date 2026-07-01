package com.pingen.sdk.models.batch;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Specifies how a merged PDF batch file should be split into individual letters.
 * FILE splits one letter per attached file (ZIP only), PAGE splits by a fixed number of pages,
 * CUSTOM splits on a separator string, and QR_INVOICE splits on embedded QR invoice codes.
 */
public enum BatchGroupingSplitType {
    FILE("file"),
    PAGE("page"),
    CUSTOM("custom"),
    QR_INVOICE("qr_invoice");

    private final String value;

    BatchGroupingSplitType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static BatchGroupingSplitType fromValue(String value) {
        if (value == null) return null;
        for (BatchGroupingSplitType t : values()) {
            if (t.value.equalsIgnoreCase(value)) return t;
        }
        return null;
    }

    @Override
    public String toString() {
        return value;
    }
}
