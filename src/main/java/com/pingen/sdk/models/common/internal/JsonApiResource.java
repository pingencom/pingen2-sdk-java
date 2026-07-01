package com.pingen.sdk.models.common.internal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a single JSON:API resource response.
 * JSON:API format: { "data": { "type": "...", "id": "...", "attributes": {...}, "relationships": {...} } }
 *
 * @param <T> the type of the attributes
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JsonApiResource<T> {

    @JsonProperty("data")
    private ResourceData<T> data;

    public JsonApiResource() {
    }

    public JsonApiResource(ResourceData<T> data) {
        this.data = data;
    }

    public ResourceData<T> getData() {
        return data;
    }

    public void setData(ResourceData<T> data) {
        this.data = data;
    }

    /**
     * Convenience method to get the resource ID.
     *
     * @return the resource ID
     */
    public String getId() {
        return data != null ? data.getId() : null;
    }

    /**
     * Convenience method to get the resource type.
     *
     * @return the resource type
     */
    public String getType() {
        return data != null ? data.getType() : null;
    }

    /**
     * Convenience method to get the resource attributes.
     *
     * @return the attributes object
     */
    public T getAttributes() {
        return data != null ? data.getAttributes() : null;
    }

}
