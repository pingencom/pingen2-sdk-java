package com.pingen.sdk.models.webhook;

import com.pingen.sdk.models.common.internal.JsonApiRequest;
import com.pingen.sdk.models.common.internal.JsonApiRequestData;

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

    public JsonApiRequest<WebhookCreateAttributes> toJsonApiRequest() {
        return new JsonApiRequest<>(
                new JsonApiRequestData<>("webhooks", new WebhookCreateAttributes(url, eventCategory, signingKey)));
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

        public Builder eventCategory(WebhookEventCategory cat) {
            this.eventCategory = cat;
            return this;
        }

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
