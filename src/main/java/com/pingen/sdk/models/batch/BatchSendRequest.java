package com.pingen.sdk.models.batch;

import com.pingen.sdk.models.common.internal.JsonApiRequest;
import com.pingen.sdk.models.common.internal.JsonApiRequestData;
import com.pingen.sdk.models.letter.DeliveryProduct;
import com.pingen.sdk.models.letter.PrintMode;
import com.pingen.sdk.models.letter.PrintSpectrum;

import java.util.ArrayList;
import java.util.List;

public class BatchSendRequest {

    private final List<BatchSendDeliveryProductAttributes> deliveryProducts;
    private final PrintMode printMode;
    private final PrintSpectrum printSpectrum;

    private BatchSendRequest(Builder builder) {
        this.deliveryProducts = List.copyOf(builder.deliveryProducts);
        this.printMode = builder.printMode;
        this.printSpectrum = builder.printSpectrum;
    }

    public JsonApiRequest<BatchSendAttributes> toJsonApiRequest(String batchId) {
        BatchSendAttributes attributes = new BatchSendAttributes(deliveryProducts, printMode, printSpectrum);
        return new JsonApiRequest<>(new JsonApiRequestData<>(batchId, "batches", attributes));
    }

    public List<BatchSendDeliveryProductAttributes> getDeliveryProducts() { return deliveryProducts; }

    public PrintMode getPrintMode() {
        return printMode;
    }
    public PrintSpectrum getPrintSpectrum() { return printSpectrum; }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final List<BatchSendDeliveryProductAttributes> deliveryProducts = new ArrayList<>();
        private PrintMode printMode;
        private PrintSpectrum printSpectrum;

        private Builder() {
        }

        public Builder addDeliveryProduct(String country, DeliveryProduct deliveryProduct) {
            this.deliveryProducts.add(new BatchSendDeliveryProductAttributes(country, deliveryProduct));
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

        public BatchSendRequest build() {
            if (deliveryProducts.isEmpty()) throw new IllegalArgumentException("at least one deliveryProduct entry is required");
            if (printMode == null) throw new IllegalArgumentException("printMode is required");
            if (printSpectrum == null) throw new IllegalArgumentException("printSpectrum is required");
            return new BatchSendRequest(this);
        }
    }
}
