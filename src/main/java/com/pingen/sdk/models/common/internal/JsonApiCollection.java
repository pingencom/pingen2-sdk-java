package com.pingen.sdk.models.common.internal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Represents a JSON:API collection response with pagination.
 * JSON:API format: { "data": [...], "links": {...}, "meta": {...} }
 *
 * @param <T> the type of the resource attributes
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JsonApiCollection<T> {

    @JsonProperty("data")
    private List<ResourceData<T>> data;

    @JsonProperty("links")
    private Links links;

    @JsonProperty("meta")
    private Meta meta;

    public JsonApiCollection() {
    }

    public List<ResourceData<T>> getData() {
        return data;
    }

    public void setData(List<ResourceData<T>> data) {
        this.data = data;
    }

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    /**
     * Gets the number of items in this collection page.
     *
     * @return the number of items
     */
    public int size() {
        return data != null ? data.size() : 0;
    }

    /**
     * Checks if this collection is empty.
     *
     * @return true if the collection is empty
     */
    public boolean isEmpty() {
        return data == null || data.isEmpty();
    }

    /**
     * JSON:API links object for pagination.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Links {

        @JsonProperty("first")
        private String first;

        @JsonProperty("last")
        private String last;

        @JsonProperty("prev")
        private String prev;

        @JsonProperty("next")
        private String next;

        @JsonProperty("self")
        private String self;

        public String getFirst() {
            return first;
        }

        public void setFirst(String first) {
            this.first = first;
        }

        public String getLast() {
            return last;
        }

        public void setLast(String last) {
            this.last = last;
        }

        public String getPrev() {
            return prev;
        }

        public void setPrev(String prev) {
            this.prev = prev;
        }

        public String getNext() {
            return next;
        }

        public void setNext(String next) {
            this.next = next;
        }

        public String getSelf() {
            return self;
        }

        public void setSelf(String self) {
            this.self = self;
        }

        public boolean hasNext() {
            return next != null && !next.isEmpty();
        }

        public boolean hasPrev() {
            return prev != null && !prev.isEmpty();
        }
    }

}
