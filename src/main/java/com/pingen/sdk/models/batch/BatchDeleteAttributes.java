package com.pingen.sdk.models.batch;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BatchDeleteAttributes {

    @JsonProperty("with_deliverables")
    private final boolean withDeliverables;

    public BatchDeleteAttributes(boolean withDeliverables) {
        this.withDeliverables = withDeliverables;
    }
}
