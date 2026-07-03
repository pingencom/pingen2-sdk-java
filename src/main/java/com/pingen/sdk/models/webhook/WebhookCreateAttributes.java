package com.pingen.sdk.models.webhook;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class WebhookCreateAttributes {

    @JsonProperty("url")
    private final String url;
    @JsonProperty("event_category")
    private final WebhookEventCategory eventCategory;
    @JsonProperty("signing_key")
    private final String signingKey;

    public WebhookCreateAttributes(String url, WebhookEventCategory eventCategory, String signingKey) {
        this.url = url;
        this.eventCategory = eventCategory;
        this.signingKey = signingKey;
    }
}
