package com.pingen.sdk.models.batch;

import com.pingen.sdk.models.common.internal.JsonApiRequest;
import com.pingen.sdk.models.common.internal.JsonApiRequestData;

/**
 * Request object for updating an existing batch (name and/or icon).
 * Use the builder pattern for construction.
 */
public class BatchUpdateRequest {

    private final String name;
    private final BatchIcon icon;

    private BatchUpdateRequest(Builder builder) {
        this.name = builder.name;
        this.icon = builder.icon;
    }

    public JsonApiRequest<BatchUpdateAttributes> toJsonApiRequest(String batchId) {
        return new JsonApiRequest<>(
                new JsonApiRequestData<>(batchId, "batches", new BatchUpdateAttributes(name, icon)));
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String name;
        private BatchIcon icon;

        private Builder() {
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder icon(BatchIcon icon) {
            this.icon = icon;
            return this;
        }

        public BatchUpdateRequest build() {
            if (name == null && icon == null)
                throw new IllegalArgumentException("At least one of name or icon must be set");
            return new BatchUpdateRequest(this);
        }
    }
}
