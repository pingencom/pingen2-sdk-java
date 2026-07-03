package com.pingen.sdk.models.common.internal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonApiRequestData<T> {

    @JsonProperty("id")
    private final String id;

    @JsonProperty("type")
    private final String type;

    @JsonProperty("attributes")
    private final T attributes;

    public JsonApiRequestData(String type, T attributes) {
        this(null, type, attributes);
    }

    public JsonApiRequestData(String id, String type, T attributes) {
        this.id = id;
        this.type = type;
        this.attributes = attributes;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public T getAttributes() {
        return attributes;
    }
}
