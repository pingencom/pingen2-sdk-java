package com.pingen.sdk.webhook;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class IncomingWebhookDetailsData {

    @JsonProperty("id")
    private String id;

    @JsonProperty("type")
    private String type;

    @JsonProperty("attributes")
    private Map<String, Object> attributes;

    @JsonProperty("relationships")
    private IncomingWebhookRelationships relationships;

    @JsonProperty("links")
    private ItemLinks links;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Map<String, Object> getAttributes() { return attributes; }
    public void setAttributes(Map<String, Object> attributes) { this.attributes = attributes; }

    public IncomingWebhookRelationships getRelationships() { return relationships; }
    public void setRelationships(IncomingWebhookRelationships relationships) { this.relationships = relationships; }

    public ItemLinks getLinks() { return links; }
    public void setLinks(ItemLinks links) { this.links = links; }
}
