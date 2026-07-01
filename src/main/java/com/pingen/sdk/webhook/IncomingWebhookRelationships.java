package com.pingen.sdk.webhook;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class IncomingWebhookRelationships {

    @JsonProperty("organisation")
    private RelationshipRelatedItem organisation;

    @JsonProperty("letter")
    private RelationshipRelatedItem letter;

    @JsonProperty("event")
    private RelationshipRelatedEvent event;

    public RelationshipRelatedItem getOrganisation() { return organisation; }
    public void setOrganisation(RelationshipRelatedItem organisation) { this.organisation = organisation; }

    public RelationshipRelatedItem getLetter() { return letter; }
    public void setLetter(RelationshipRelatedItem letter) { this.letter = letter; }

    public RelationshipRelatedEvent getEvent() { return event; }
    public void setEvent(RelationshipRelatedEvent event) { this.event = event; }
}
