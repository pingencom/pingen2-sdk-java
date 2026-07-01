package com.pingen.sdk.webhook;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemLinks {

    @JsonProperty("self")
    private String self;

    public String getSelf() { return self; }
    public void setSelf(String self) { this.self = self; }
}
