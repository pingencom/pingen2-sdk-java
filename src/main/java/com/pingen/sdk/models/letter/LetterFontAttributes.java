package com.pingen.sdk.models.letter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LetterFontAttributes {

    @JsonProperty("name")
    private String name;

    @JsonProperty("is_embedded")
    private Boolean isEmbedded;

    public LetterFontAttributes() {
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Boolean getEmbedded() {
        return isEmbedded;
    }

    public void setEmbedded(Boolean embedded) {
        isEmbedded = embedded;
    }
}
