package com.pingen.sdk.webhook;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RelationshipRelatedEvent {

    @JsonProperty("data")
    private RelationshipRelatedItemData data;

    public RelationshipRelatedItemData getData() { return data; }
    public void setData(RelationshipRelatedItemData data) { this.data = data; }
}
