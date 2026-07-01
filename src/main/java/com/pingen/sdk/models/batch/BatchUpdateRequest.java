package com.pingen.sdk.models.batch;

import java.util.HashMap;
import java.util.Map;

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

    public Map<String, Object> toJsonApiRequest(String batchId) {
        Map<String, Object> attributes = new HashMap<>();
        if (name != null) attributes.put("name", name);
        if (icon != null) attributes.put("icon", icon.getValue());

        Map<String, Object> data = new HashMap<>();
        data.put("id", batchId);
        data.put("type", "batches");
        data.put("attributes", attributes);

        Map<String, Object> request = new HashMap<>();
        request.put("data", data);

        return request;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String name;
        private BatchIcon icon;

        private Builder() {
        }

        /**
         * Sets the batch name (5–100 characters).
         */
        public Builder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * Sets the batch icon.
         */
        public Builder icon(BatchIcon icon) {
            this.icon = icon;
            return this;
        }

        public BatchUpdateRequest build() {
            if (name == null && icon == null) {
                throw new IllegalArgumentException("At least one of name or icon must be set");
            }
            return new BatchUpdateRequest(this);
        }
    }
}
