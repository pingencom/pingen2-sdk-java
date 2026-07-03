package com.pingen.sdk.models.letter;

import com.pingen.sdk.models.common.internal.JsonApiRequest;
import com.pingen.sdk.models.common.internal.JsonApiRequestData;

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

    public JsonApiRequest<LetterSendAttributes> toJsonApiRequest(String letterId) {
        LetterSendAttributes attributes = new LetterSendAttributes(
                deliveryProduct, printMode, printSpectrum, metaData);
        return new JsonApiRequest<>(new JsonApiRequestData<>(letterId, "letters", attributes));
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

        public Builder deliveryProduct(DeliveryProduct d) {
            this.deliveryProduct = d;
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

        public Builder metaData(LetterMetaData m) {
            this.metaData = m;
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
