package com.pingen.sdk.models.batch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BatchLetterGroupAttributes {

    @JsonProperty("name")
    private String name;

    @JsonProperty("count")
    private Integer count;

    public BatchLetterGroupAttributes() {
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getCount() { return count; }
    public void setCount(Integer count) { this.count = count; }
}
