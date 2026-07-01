package com.pingen.sdk.webhook;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RelationshipRelatedItemLinks {

    @JsonProperty("related")
    private String related;

    public String getRelated() { return related; }
    public void setRelated(String related) { this.related = related; }
}
