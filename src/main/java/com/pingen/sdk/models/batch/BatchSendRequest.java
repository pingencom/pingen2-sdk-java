package com.pingen.sdk.models.batch;

import com.pingen.sdk.models.letter.DeliveryProduct;
import com.pingen.sdk.models.letter.PrintMode;
import com.pingen.sdk.models.letter.PrintSpectrum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Request object for sending a batch that is currently in draft state.
 * Specifies delivery products per destination country, print mode, and print spectrum.
 * Use the builder pattern for construction.
 */
public class BatchSendRequest {

    private final List<DeliveryProductEntry> deliveryProducts;
    private final PrintMode printMode;
    private final PrintSpectrum printSpectrum;

    private BatchSendRequest(Builder builder) {
        this.deliveryProducts = List.copyOf(builder.deliveryProducts);
        this.printMode = builder.printMode;
        this.printSpectrum = builder.printSpectrum;
    }

    public Map<String, Object> toJsonApiRequest(String batchId) {
        List<Map<String, String>> products = new ArrayList<>();
        for (DeliveryProductEntry entry : deliveryProducts) {
            Map<String, String> product = new HashMap<>();
            product.put("country", entry.country());
            product.put("delivery_product", entry.deliveryProduct().getValue());
            products.add(product);
        }

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("delivery_products", products);
        attributes.put("print_mode", printMode.getValue());
        attributes.put("print_spectrum", printSpectrum.getValue());

        Map<String, Object> data = new HashMap<>();
        data.put("id", batchId);
        data.put("type", "batches");
        data.put("attributes", attributes);

        Map<String, Object> request = new HashMap<>();
        request.put("data", data);
        return request;
    }

    public record DeliveryProductEntry(String country, DeliveryProduct deliveryProduct) {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final List<DeliveryProductEntry> deliveryProducts = new ArrayList<>();
        private PrintMode printMode;
        private PrintSpectrum printSpectrum;

        private Builder() {
        }

        public Builder addDeliveryProduct(String country, DeliveryProduct deliveryProduct) {
            this.deliveryProducts.add(new DeliveryProductEntry(country, deliveryProduct));
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

        public BatchSendRequest build() {
            if (deliveryProducts.isEmpty()) throw new IllegalArgumentException("at least one deliveryProduct entry is required");
            if (printMode == null) throw new IllegalArgumentException("printMode is required");
            if (printSpectrum == null) throw new IllegalArgumentException("printSpectrum is required");
            return new BatchSendRequest(this);
        }
    }
}
