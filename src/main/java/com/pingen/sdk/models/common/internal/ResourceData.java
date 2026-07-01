package com.pingen.sdk.models.common.internal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * Represents the "data" object in JSON:API format.
 *
 * @param <T> the type of the attributes
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResourceData<T> {

    @JsonProperty("type")
    private String type;

    @JsonProperty("id")
    private String id;

    @JsonProperty("attributes")
    private T attributes;

    @JsonProperty("relationships")
    private Map<String, Object> relationships;

    public ResourceData() {
    }

    public ResourceData(String type, String id, T attributes) {
        this.type = type;
        this.id = id;
        this.attributes = attributes;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public T getAttributes() {
        return attributes;
    }

    public void setAttributes(T attributes) {
        this.attributes = attributes;
    }

    public Map<String, Object> getRelationships() {
        return relationships;
    }

    public void setRelationships(Map<String, Object> relationships) {
        this.relationships = relationships;
    }
}
