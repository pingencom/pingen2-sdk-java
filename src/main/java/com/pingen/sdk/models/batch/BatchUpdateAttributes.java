package com.pingen.sdk.models.batch;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BatchUpdateAttributes {

    @JsonProperty("name")
    private final String name;
    @JsonProperty("icon")
    private final BatchIcon icon;

    public BatchUpdateAttributes(String name, BatchIcon icon) {
        this.name = name;
        this.icon = icon;
    }
}
