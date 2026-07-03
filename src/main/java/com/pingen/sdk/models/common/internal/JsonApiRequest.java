package com.pingen.sdk.models.common.internal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JsonApiRequest<T> {

    @JsonProperty("data")
    private final JsonApiRequestData<T> data;

    public JsonApiRequest(JsonApiRequestData<T> data) {
        this.data = data;
    }

    public JsonApiRequestData<T> getData() {
        return data;
    }
}
