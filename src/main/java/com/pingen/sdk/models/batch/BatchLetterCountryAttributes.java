package com.pingen.sdk.models.batch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BatchLetterCountryAttributes {

    @JsonProperty("country")
    private String country;

    @JsonProperty("count")
    private Integer count;

    public BatchLetterCountryAttributes() {
    }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public Integer getCount() { return count; }
    public void setCount(Integer count) { this.count = count; }
}
