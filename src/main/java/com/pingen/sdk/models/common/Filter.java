package com.pingen.sdk.models.common;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Represents a JSON filter expression for collection endpoints.
 *
 * <p>Supports equality, comparators, and logical AND/OR combinations as described
 * in the Pingen API filter specification.
 *
 * <pre>{@code
 * // Simple equality
 * Filter.eq("status", "sent")
 *
 * // Comparators
 * Filter.gt("created_at", "2024-01-01")
 * Filter.lt("amount", 100)
 *
 * // Logical combinations
 * Filter.and(Filter.eq("key1", "a"), Filter.eq("key2", "b"))
 * Filter.or(Filter.eq("key1", "a"), Filter.and(Filter.eq("key2", "b"), Filter.eq("key3", "c")))
 * }</pre>
 */
public abstract class Filter {

    public abstract Map<String, Object> toStructure();

    /** Field equals value. Value may be a String or a number. */
    public static Filter eq(String field, Object value) {
        return new Leaf(field, value);
    }

    /** Field is smaller than value. */
    public static Filter lt(String field, Object value) {
        return new Leaf(field, "<" + value);
    }

    /** Field is smaller than or equal to value. */
    public static Filter lte(String field, Object value) {
        return new Leaf(field, "<=" + value);
    }

    /** Field is greater than value. */
    public static Filter gt(String field, Object value) {
        return new Leaf(field, ">" + value);
    }

    /** Field is greater than or equal to value. */
    public static Filter gte(String field, Object value) {
        return new Leaf(field, ">=" + value);
    }

    /** Field is not equal to value. */
    public static Filter notEq(String field, Object value) {
        return new Leaf(field, "!" + value);
    }

    /** Field approximately matches value. */
    public static Filter approx(String field, Object value) {
        return new Leaf(field, "~" + value);
    }

    /** All given filters must match. */
    public static Filter and(Filter... filters) {
        return new Compound("and", Arrays.asList(filters));
    }

    /** At least one of the given filters must match. */
    public static Filter or(Filter... filters) {
        return new Compound("or", Arrays.asList(filters));
    }

    private static class Leaf extends Filter {
        private final String field;
        private final Object value;

        private Leaf(String field, Object value) {
            this.field = field;
            this.value = value;
        }

        @Override
        public Map<String, Object> toStructure() {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put(field, value);
            return map;
        }
    }

    private static class Compound extends Filter {
        private final String operator;
        private final List<Filter> filters;

        private Compound(String operator, List<Filter> filters) {
            this.operator = operator;
            this.filters = filters;
        }

        @Override
        public Map<String, Object> toStructure() {
            List<Map<String, Object>> structures = filters.stream()
                    .map(Filter::toStructure)
                    .collect(Collectors.toList());
            Map<String, Object> map = new LinkedHashMap<>();
            map.put(operator, structures);
            return map;
        }
    }
}
