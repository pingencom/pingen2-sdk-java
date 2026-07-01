package com.pingen.sdk.models.letter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Result of the letter price calculation.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LetterPriceCalculatorResult {

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("price")
    private Double price;

    public LetterPriceCalculatorResult() {
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
