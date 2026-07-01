package com.pingen.sdk.models.letter;

import java.util.HashMap;
import java.util.Map;

/**
 * Request object for sending a letter that is currently in draft state.
 * Use the builder pattern for construction.
 */
public class LetterSendRequest {

    private final DeliveryProduct deliveryProduct;
    private final PrintMode printMode;
    private final PrintSpectrum printSpectrum;
    private final LetterMetaData metaData;

    private LetterSendRequest(Builder builder) {
        this.deliveryProduct = builder.deliveryProduct;
        this.printMode = builder.printMode;
        this.printSpectrum = builder.printSpectrum;
        this.metaData = builder.metaData;
    }

    public Map<String, Object> toJsonApiRequest(String letterId) {
        Map<String, Object> attributes = new HashMap<>();
        if (deliveryProduct != null) attributes.put("delivery_product", deliveryProduct.getValue());
        if (printMode != null) attributes.put("print_mode", printMode.getValue());
        if (printSpectrum != null) attributes.put("print_spectrum", printSpectrum.getValue());
        if (metaData != null) attributes.put("meta_data", metaData.toMap());

        Map<String, Object> data = new HashMap<>();
        data.put("id", letterId);
        data.put("type", "letters");
        data.put("attributes", attributes);

        Map<String, Object> request = new HashMap<>();
        request.put("data", data);
        return request;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private DeliveryProduct deliveryProduct;
        private PrintMode printMode;
        private PrintSpectrum printSpectrum;
        private LetterMetaData metaData;

        private Builder() {
        }

        public Builder deliveryProduct(DeliveryProduct deliveryProduct) {
            this.deliveryProduct = deliveryProduct;
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

        public Builder metaData(LetterMetaData metaData) {
            this.metaData = metaData;
            return this;
        }

        public LetterSendRequest build() {
            if (deliveryProduct == null) throw new IllegalArgumentException("deliveryProduct is required");
            if (printMode == null) throw new IllegalArgumentException("printMode is required");
            if (printSpectrum == null) throw new IllegalArgumentException("printSpectrum is required");
            return new LetterSendRequest(this);
        }
    }
}
