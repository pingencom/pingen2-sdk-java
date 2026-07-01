package com.pingen.sdk.models.batch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/**
 * Statistical breakdown of a batch's letters by country, region, and delivery group.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BatchStatistics {

    @JsonProperty("letter_countries")
    private List<Map<String, Object>> letterCountries;

    @JsonProperty("letter_groups")
    private List<Map<String, Object>> letterGroups;

    @JsonProperty("letter_regions")
    private List<Map<String, Object>> letterRegions;

    @JsonProperty("letter_validating")
    private Integer letterValidating;

    public BatchStatistics() {
    }

    public List<Map<String, Object>> getLetterCountries() { return letterCountries; }
    public void setLetterCountries(List<Map<String, Object>> letterCountries) { this.letterCountries = letterCountries; }

    public List<Map<String, Object>> getLetterGroups() { return letterGroups; }
    public void setLetterGroups(List<Map<String, Object>> letterGroups) { this.letterGroups = letterGroups; }

    public List<Map<String, Object>> getLetterRegions() { return letterRegions; }
    public void setLetterRegions(List<Map<String, Object>> letterRegions) { this.letterRegions = letterRegions; }

    public Integer getLetterValidating() { return letterValidating; }
    public void setLetterValidating(Integer letterValidating) { this.letterValidating = letterValidating; }
}
