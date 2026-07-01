package com.pingen.sdk.models.common;

import com.pingen.sdk.models.common.internal.ResourceData;

/**
 * Represents a single resource with ID, type, and attributes.
 *
 * @param <T> the type of the attributes
 */
public class Resource<T> {
    private final ResourceData<T> data;

    public Resource(ResourceData<T> data) {
        this.data = data;
    }

    public String getId() {
        return data.getId();
    }

    public String getType() {
        return data.getType();
    }

    public T getAttributes() {
        return data.getAttributes();
    }

    public ResourceData<T> getData() {
        return data;
    }
}
