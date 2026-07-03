package com.pingen.sdk.models.letter;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LetterCreateAttributes {

    @JsonProperty("file_original_name")
    private final String fileOriginalName;
    @JsonProperty("file_url")
    private final String fileUrl;
    @JsonProperty("file_url_signature")
    private final String fileUrlSignature;
    @JsonProperty("auto_send")
    private final boolean autoSend;
    @JsonProperty("address_position")
    private final AddressPosition addressPosition;
    @JsonProperty("delivery_product")
    private final DeliveryProduct deliveryProduct;
    @JsonProperty("print_mode")
    private final PrintMode printMode;
    @JsonProperty("print_spectrum")
    private final PrintSpectrum printSpectrum;
    @JsonProperty("meta_data")
    private final LetterMetaData metaData;

    public LetterCreateAttributes(
            String fileOriginalName, String fileUrl, String fileUrlSignature,
            boolean autoSend, AddressPosition addressPosition,
            DeliveryProduct deliveryProduct, PrintMode printMode, PrintSpectrum printSpectrum,
            LetterMetaData metaData) {
        this.fileOriginalName = fileOriginalName;
        this.fileUrl = fileUrl;
        this.fileUrlSignature = fileUrlSignature;
        this.autoSend = autoSend;
        this.addressPosition = addressPosition;
        this.deliveryProduct = deliveryProduct;
        this.printMode = printMode;
        this.printSpectrum = printSpectrum;
        this.metaData = metaData;
    }

    public LetterMetaData getMetaData() {
        return metaData;
    }
}
