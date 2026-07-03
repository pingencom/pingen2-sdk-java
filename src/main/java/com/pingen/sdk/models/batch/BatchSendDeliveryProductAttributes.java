package com.pingen.sdk.models.batch;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pingen.sdk.models.letter.DeliveryProduct;

public class BatchSendDeliveryProductAttributes {

    @JsonProperty("country")
    private final String country;

    @JsonProperty("delivery_product")
    private final DeliveryProduct deliveryProduct;

    public BatchSendDeliveryProductAttributes(String country, DeliveryProduct deliveryProduct) {
        this.country = country;
        this.deliveryProduct = deliveryProduct;
    }

    public String getCountry() { return country; }
    public DeliveryProduct getDeliveryProduct() { return deliveryProduct; }
}
