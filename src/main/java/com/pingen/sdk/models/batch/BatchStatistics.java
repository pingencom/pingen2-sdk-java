package com.pingen.sdk.models.batch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Statistical breakdown of a batch's letters by country, region, and delivery group.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BatchStatistics {

    @JsonProperty("letter_countries")
    private List<BatchLetterCountryAttributes> letterCountries;

    @JsonProperty("letter_groups")
    private List<BatchLetterGroupAttributes> letterGroups;

    @JsonProperty("letter_regions")
    private List<BatchLetterCountryAttributes> letterRegions;

    @JsonProperty("letter_validating")
    private Integer letterValidating;

    public BatchStatistics() {
    }

    public List<BatchLetterCountryAttributes> getLetterCountries() { return letterCountries; }
    public void setLetterCountries(List<BatchLetterCountryAttributes> letterCountries) { this.letterCountries = letterCountries; }

    public List<BatchLetterGroupAttributes> getLetterGroups() { return letterGroups; }
    public void setLetterGroups(List<BatchLetterGroupAttributes> letterGroups) { this.letterGroups = letterGroups; }

    public List<BatchLetterCountryAttributes> getLetterRegions() { return letterRegions; }
    public void setLetterRegions(List<BatchLetterCountryAttributes> letterRegions) { this.letterRegions = letterRegions; }

    public Integer getLetterValidating() { return letterValidating; }
    public void setLetterValidating(Integer letterValidating) { this.letterValidating = letterValidating; }
}
