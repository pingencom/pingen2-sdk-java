package com.pingen.sdk.models.webhook;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Event category options for webhooks.
 */
public enum WebhookEventCategory {
    ISSUES("issues"),
    SENT("sent"),
    UNDELIVERABLE("undeliverable"),
    DELIVERED("delivered"),
    CHANNEL_SUBSCRIPTIONS("channel_subscriptions");

    private final String value;

    WebhookEventCategory(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static WebhookEventCategory fromValue(String value) {
        if (value == null) return null;
        for (WebhookEventCategory c : values()) {
            if (c.value.equalsIgnoreCase(value)) return c;
        }
        return null;
    }

    @Override
    public String toString() {
        return value;
    }
}
