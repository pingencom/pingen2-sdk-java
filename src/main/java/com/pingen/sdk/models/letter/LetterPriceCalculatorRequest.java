package com.pingen.sdk.models.letter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public Map<String, Object> toJsonApiRequest() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("country", country);
        attributes.put("paper_types", paperTypes);
        attributes.put("print_mode", printMode.getValue());
        attributes.put("print_spectrum", printSpectrum.getValue());
        attributes.put("delivery_product", deliveryProduct.getValue());

        Map<String, Object> data = new HashMap<>();
        data.put("type", "letter_price_calculator");
        data.put("attributes", attributes);

        Map<String, Object> request = new HashMap<>();
        request.put("data", data);

        return request;
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

        public Builder printMode(PrintMode printMode) {
            this.printMode = printMode;
            return this;
        }

        public Builder printSpectrum(PrintSpectrum printSpectrum) {
            this.printSpectrum = printSpectrum;
            return this;
        }

        public Builder deliveryProduct(DeliveryProduct deliveryProduct) {
            this.deliveryProduct = deliveryProduct;
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
