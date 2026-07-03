package com.pingen.sdk.models.letter;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class LetterPriceCalculatorAttributes {

    @JsonProperty("country")
    private final String country;
    @JsonProperty("paper_types")
    private final List<String> paperTypes;
    @JsonProperty("print_mode")
    private final PrintMode printMode;
    @JsonProperty("print_spectrum")
    private final PrintSpectrum printSpectrum;
    @JsonProperty("delivery_product")
    private final DeliveryProduct deliveryProduct;

    public LetterPriceCalculatorAttributes(String country, List<String> paperTypes,
                                           PrintMode printMode, PrintSpectrum printSpectrum,
                                           DeliveryProduct deliveryProduct) {
        this.country = country;
        this.paperTypes = paperTypes;
        this.printMode = printMode;
        this.printSpectrum = printSpectrum;
        this.deliveryProduct = deliveryProduct;
    }
}
