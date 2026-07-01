package com.pingen.sdk.models.webhook;

import java.util.HashMap;
import java.util.Map;

/**
 * Request object for creating a new webhook.
 * Use the builder pattern for construction.
 */
public class WebhookCreateRequest {

    private final String url;
    private final WebhookEventCategory eventCategory;
    private final String signingKey;

    private WebhookCreateRequest(Builder builder) {
        this.url = builder.url;
        this.eventCategory = builder.eventCategory;
        this.signingKey = builder.signingKey;
    }

    public String getUrl() {
        return url;
    }

    public WebhookEventCategory getEventCategory() {
        return eventCategory;
    }

    public String getSigningKey() {
        return signingKey;
    }

    public Map<String, Object> toJsonApiRequest() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("url", url);

        if (eventCategory != null) {
            attributes.put("event_category", eventCategory.getValue());
        }
        if (signingKey != null) {
            attributes.put("signing_key", signingKey);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("type", "webhooks");
        data.put("attributes", attributes);

        Map<String, Object> request = new HashMap<>();
        request.put("data", data);

        return request;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String url;
        private WebhookEventCategory eventCategory;
        private String signingKey;

        private Builder() {
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder eventCategory(WebhookEventCategory eventCategory) {
            this.eventCategory = eventCategory;
            return this;
        }

        /**
         * Sets the signing key used to verify webhook payloads (20–32 characters).
         */
        public Builder signingKey(String signingKey) {
            this.signingKey = signingKey;
            return this;
        }

        public WebhookCreateRequest build() {
            if (url == null || url.isBlank()) throw new IllegalArgumentException("url is required");
            if (eventCategory == null) throw new IllegalArgumentException("eventCategory is required");
            if (signingKey == null || signingKey.isBlank()) throw new IllegalArgumentException("signingKey is required");
            return new WebhookCreateRequest(this);
        }
    }
}
