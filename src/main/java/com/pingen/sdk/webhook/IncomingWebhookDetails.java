package com.pingen.sdk.webhook;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Typed representation of a verified incoming Pingen webhook payload.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class IncomingWebhookDetails {

    @JsonProperty("data")
    private IncomingWebhookDetailsData data;

    public IncomingWebhookDetails() {
    }

    public IncomingWebhookDetailsData getData() {
        return data;
    }

    public void setData(IncomingWebhookDetailsData data) {
        this.data = data;
    }
}
