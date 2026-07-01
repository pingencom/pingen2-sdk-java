package com.pingen.sdk.models.common.internal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * JSON:API meta object containing pagination metadata (total count, current page, etc.).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Meta {

    @JsonProperty("total")
    private Integer total;

    @JsonProperty("current_page")
    private Integer currentPage;

    @JsonProperty("per_page")
    private Integer perPage;

    @JsonProperty("from")
    private Integer from;

    @JsonProperty("last_page")
    private Integer lastPage;

    @JsonProperty("to")
    private Integer to;

    public Integer getTotal() { return total; }
    public void setTotal(Integer total) { this.total = total; }

    public Integer getCurrentPage() { return currentPage; }
    public void setCurrentPage(Integer currentPage) { this.currentPage = currentPage; }

    public Integer getPerPage() { return perPage; }
    public void setPerPage(Integer perPage) { this.perPage = perPage; }

    public Integer getFrom() { return from; }
    public void setFrom(Integer from) { this.from = from; }

    public Integer getLastPage() { return lastPage; }
    public void setLastPage(Integer lastPage) { this.lastPage = lastPage; }

    public Integer getTo() { return to; }
    public void setTo(Integer to) { this.to = to; }
}
