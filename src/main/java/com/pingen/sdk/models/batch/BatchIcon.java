package com.pingen.sdk.models.batch;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Icon options for batches.
 */
public enum BatchIcon {
    CAMPAIGN("campaign"),
    MEGAPHONE("megaphone"),
    WAVE_HAND("wave-hand"),
    FLASH("flash"),
    ROCKET("rocket"),
    BELL("bell"),
    PERCENT_TAG("percent-tag"),
    PERCENT_BADGE("percent-badge"),
    PRESENT("present"),
    RECEIPT("receipt"),
    DOCUMENT("document"),
    INFORMATION("information"),
    CALENDAR("calendar"),
    NEWSPAPER("newspaper"),
    CROWN("crown"),
    VIRUS("virus");

    private final String value;

    BatchIcon(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static BatchIcon fromValue(String value) {
        if (value == null) return null;
        for (BatchIcon icon : values()) {
            if (icon.value.equalsIgnoreCase(value)) return icon;
        }
        return null;
    }

    @Override
    public String toString() {
        return value;
    }
}
