package com.pingen.sdk.models.batch;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pingen.sdk.models.letter.PrintMode;
import com.pingen.sdk.models.letter.PrintSpectrum;

import java.util.List;

public class BatchSendAttributes {

    @JsonProperty("delivery_products")
    private final List<BatchSendDeliveryProductAttributes> deliveryProducts;
    @JsonProperty("print_mode")
    private final PrintMode printMode;
    @JsonProperty("print_spectrum")
    private final PrintSpectrum printSpectrum;

    public BatchSendAttributes(List<BatchSendDeliveryProductAttributes> deliveryProducts,
                               PrintMode printMode, PrintSpectrum printSpectrum) {
        this.deliveryProducts = deliveryProducts;
        this.printMode = printMode;
        this.printSpectrum = printSpectrum;
    }
}
