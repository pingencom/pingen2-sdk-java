package com.pingen.sdk.models.common;

import com.pingen.sdk.models.common.internal.JsonApiCollection;
import com.pingen.sdk.models.common.internal.ResourceData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A convenient wrapper around JsonApiCollection for easier access to paginated data.
 *
 * @param <T> the type of the resource attributes
 */
public class PagedResponse<T> {

    private final JsonApiCollection<T> collection;

    public PagedResponse(JsonApiCollection<T> collection) {
        this.collection = collection;
    }

    /**
     * Gets the list of resources in this page.
     *
     * @return list of resources with their IDs, types, and attributes
     */
    public List<Resource<T>> getItems() {
        if (collection == null || collection.getData() == null) {
            return Collections.emptyList();
        }

        List<Resource<T>> items = new ArrayList<>();
        for (ResourceData<T> data : collection.getData()) {
            items.add(new Resource<>(data));
        }
        return items;
    }

    /**
     * Gets the total number of items across all pages.
     *
     * @return the total count, or null if not available
     */
    public Integer getTotal() {
        return collection != null && collection.getMeta() != null
                ? collection.getMeta().getTotal()
                : null;
    }

    /**
     * Gets the current page number.
     *
     * @return the current page number, or null if not available
     */
    public Integer getCurrentPage() {
        return collection != null && collection.getMeta() != null
                ? collection.getMeta().getCurrentPage()
                : null;
    }

    /**
     * Gets the page size (items per page).
     *
     * @return the per-page count, or null if not available
     */
    public Integer getPageLimit() {
        return collection != null && collection.getMeta() != null
                ? collection.getMeta().getPerPage()
                : null;
    }

    /**
     * Gets the last page number.
     *
     * @return the last page number, or null if not available
     */
    public Integer getLastPage() {
        return collection != null && collection.getMeta() != null
                ? collection.getMeta().getLastPage()
                : null;
    }

    /**
     * Checks if there is a next page available.
     *
     * @return true if there is a next page
     */
    public boolean hasNext() {
        return collection != null && collection.getLinks() != null
                && collection.getLinks().hasNext();
    }

    /**
     * Checks if there is a previous page available.
     *
     * @return true if there is a previous page
     */
    public boolean hasPrev() {
        return collection != null && collection.getLinks() != null
                && collection.getLinks().hasPrev();
    }

    /**
     * Gets the URL for the next page.
     *
     * @return the next page URL, or null if not available
     */
    public String getNextUrl() {
        return collection != null && collection.getLinks() != null
                ? collection.getLinks().getNext()
                : null;
    }

    /**
     * Gets the URL for the previous page.
     *
     * @return the previous page URL, or null if not available
     */
    public String getPrevUrl() {
        return collection != null && collection.getLinks() != null
                ? collection.getLinks().getPrev()
                : null;
    }

    /**
     * Gets the number of items in this page.
     *
     * @return the number of items
     */
    public int size() {
        return collection != null ? collection.size() : 0;
    }

    /**
     * Checks if this page is empty.
     *
     * @return true if the page is empty
     */
    public boolean isEmpty() {
        return collection == null || collection.isEmpty();
    }

    /**
     * Gets the underlying JSON:API collection.
     *
     * @return the JsonApiCollection
     */
    public JsonApiCollection<T> getCollection() {
        return collection;
    }

}
