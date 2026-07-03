package com.pingen.sdk.models.batch;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BatchDeleteAttributes {

    @JsonProperty("with_letters")
    private final boolean withLetters;

    public BatchDeleteAttributes(boolean withLetters) {
        this.withLetters = withLetters;
    }
}
