package com.pingen.sdk.models.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Fluent builder for JSON:API collection query parameters.
 *
 * <p>Supports pagination ({@code page[number]}, {@code page[limit]}),
 * filtering ({@code filter=<json>}), sorting ({@code sort=field} or {@code sort=-field}
 * for descending), and full-text search ({@code search=query}).
 *
 * <pre>{@code
 * // Simple filter
 * CollectionParams params = CollectionParams.builder()
 *     .page(1, 20)
 *     .filter(Filter.eq("status", "sent"))
 *     .sortDesc("created_at")
 *     .build();
 *
 * // Combined filter
 * CollectionParams params = CollectionParams.builder()
 *     .filter(Filter.and(Filter.eq("status", "sent"), Filter.gt("created_at", "2024-01-01")))
 *     .build();
 * }</pre>
 */
public class CollectionParams {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final Map<String, String> params;

    private CollectionParams(Map<String, String> params) {
        this.params = params;
    }

    public Map<String, String> toQueryParams() {
        return params;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final Map<String, String> params = new LinkedHashMap<>();
        private final List<String> sortFields = new ArrayList<>();
        private Filter filter;

        public Builder page(int number, int limit) {
            params.put("page[number]", String.valueOf(number));
            params.put("page[limit]", String.valueOf(limit));
            return this;
        }

        /** Convenience shorthand for {@code filter(Filter.eq(field, value))}. */
        public Builder filter(String field, String value) {
            this.filter = Filter.eq(field, value);
            return this;
        }

        /** Sets the filter expression. Use {@link Filter} factory methods to build it. */
        public Builder filter(Filter filter) {
            this.filter = filter;
            return this;
        }

        /** Adds an ascending sort field. Multiple calls accumulate (comma-separated). */
        public Builder sort(String field) {
            sortFields.add(field);
            return this;
        }

        /** Adds a descending sort field (prefixed with {@code -}). Multiple calls accumulate. */
        public Builder sortDesc(String field) {
            sortFields.add("-" + field);
            return this;
        }

        public Builder search(String query) {
            params.put("q", query);
            return this;
        }

        public CollectionParams build() {
            Map<String, String> result = new LinkedHashMap<>(params);
            if (!sortFields.isEmpty()) {
                result.put("sort", String.join(",", sortFields));
            }
            if (filter != null) {
                try {
                    result.put("filter", MAPPER.writeValueAsString(filter.toStructure()));
                } catch (JsonProcessingException e) {
                    throw new IllegalStateException("Failed to serialize filter to JSON", e);
                }
            }
            return new CollectionParams(result);
        }
    }
}
