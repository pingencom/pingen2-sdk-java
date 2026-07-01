package com.pingen.sdk.models.webhook;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a webhook subscription for receiving event notifications from the Pingen API.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Webhook {

    @JsonProperty("url")
    private String url;

    @JsonProperty("event_category")
    private WebhookEventCategory eventCategory;

    @JsonProperty("signing_key")
    private String signingKey;

    public Webhook() {
    }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public WebhookEventCategory getEventCategory() { return eventCategory; }
    public void setEventCategory(WebhookEventCategory eventCategory) { this.eventCategory = eventCategory; }

    public String getSigningKey() { return signingKey; }
    public void setSigningKey(String signingKey) { this.signingKey = signingKey; }
}
