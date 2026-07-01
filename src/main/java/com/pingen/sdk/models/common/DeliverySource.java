package com.pingen.sdk.models.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum DeliverySource {
    APP("app"),
    API("api"),
    BATCH("batch"),
    INTEGRATION_EMAIL("integration_email"),
    INTEGRATION_S3("integration_s3"),
    INTEGRATION_DROPBOX("integration_dropbox"),
    INTEGRATION_GOOGLEDRIVE("integration_googledrive"),
    INTEGRATION_ONEDRIVE("integration_onedrive");

    private final String value;

    DeliverySource(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static DeliverySource fromValue(String value) {
        if (value == null) return null;
        for (DeliverySource s : values()) {
            if (s.value.equalsIgnoreCase(value)) return s;
        }
        return null;
    }

    @Override
    public String toString() {
        return value;
    }
}
