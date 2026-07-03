package com.pingen.sdk.models.letter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LetterSendAttributes {

    @JsonProperty("delivery_product")
    private final DeliveryProduct deliveryProduct;
    @JsonProperty("print_mode")
    private final PrintMode printMode;
    @JsonProperty("print_spectrum")
    private final PrintSpectrum printSpectrum;
    @JsonProperty("meta_data")
    private final LetterMetaData metaData;

    public LetterSendAttributes(DeliveryProduct deliveryProduct, PrintMode printMode,
                                PrintSpectrum printSpectrum, LetterMetaData metaData) {
        this.deliveryProduct = deliveryProduct;
        this.printMode = printMode;
        this.printSpectrum = printSpectrum;
        this.metaData = metaData;
    }
}
