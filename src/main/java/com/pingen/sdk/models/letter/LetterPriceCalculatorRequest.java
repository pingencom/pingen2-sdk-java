package com.pingen.sdk.models.letter;

import com.pingen.sdk.models.common.internal.JsonApiRequest;
import com.pingen.sdk.models.common.internal.JsonApiRequestData;

import java.util.List;

/**
 * Request object for calculating a letter's price.
 * Use the builder pattern for construction.
 */
public class LetterPriceCalculatorRequest {

    private final String country;
    private final List<String> paperTypes;
    private final PrintMode printMode;
    private final PrintSpectrum printSpectrum;
    private final DeliveryProduct deliveryProduct;

    private LetterPriceCalculatorRequest(Builder builder) {
        this.country = builder.country;
        this.paperTypes = builder.paperTypes;
        this.printMode = builder.printMode;
        this.printSpectrum = builder.printSpectrum;
        this.deliveryProduct = builder.deliveryProduct;
    }

    public JsonApiRequest<LetterPriceCalculatorAttributes> toJsonApiRequest() {
        LetterPriceCalculatorAttributes attributes = new LetterPriceCalculatorAttributes(
                country, paperTypes, printMode, printSpectrum, deliveryProduct);
        return new JsonApiRequest<>(new JsonApiRequestData<>("letter_price_calculator", attributes));
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String country;
        private List<String> paperTypes;
        private PrintMode printMode;
        private PrintSpectrum printSpectrum;
        private DeliveryProduct deliveryProduct;

        private Builder() {
        }

        public Builder country(String country) {
            this.country = country;
            return this;
        }

        public Builder paperTypes(List<String> paperTypes) {
            this.paperTypes = paperTypes;
            return this;
        }

        public Builder printMode(PrintMode m) {
            this.printMode = m;
            return this;
        }

        public Builder printSpectrum(PrintSpectrum s) {
            this.printSpectrum = s;
            return this;
        }

        public Builder deliveryProduct(DeliveryProduct d) {
            this.deliveryProduct = d;
            return this;
        }

        public LetterPriceCalculatorRequest build() {
            if (country == null || country.isBlank()) throw new IllegalArgumentException("country is required");
            if (paperTypes == null || paperTypes.isEmpty()) throw new IllegalArgumentException("paperTypes is required");
            if (printMode == null) throw new IllegalArgumentException("printMode is required");
            if (printSpectrum == null) throw new IllegalArgumentException("printSpectrum is required");
            if (deliveryProduct == null) throw new IllegalArgumentException("deliveryProduct is required");
            return new LetterPriceCalculatorRequest(this);
        }
    }
}
