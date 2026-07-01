package com.pingen.sdk.webhook;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RelationshipRelatedItem {

    @JsonProperty("links")
    private RelationshipRelatedItemLinks links;

    @JsonProperty("data")
    private RelationshipRelatedItemData data;

    public RelationshipRelatedItemLinks getLinks() { return links; }
    public void setLinks(RelationshipRelatedItemLinks links) { this.links = links; }

    public RelationshipRelatedItemData getData() { return data; }
    public void setData(RelationshipRelatedItemData data) { this.data = data; }
}
