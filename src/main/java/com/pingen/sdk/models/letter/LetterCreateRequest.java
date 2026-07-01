package com.pingen.sdk.models.letter;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Request object for creating a new letter.
 * Use the builder pattern for construction.
 */
public class LetterCreateRequest {

    private final Path filePath;
    private final byte[] fileBytes;
    private final String fileOriginalName;
    private final AddressPosition addressPosition;
    private final boolean autoSend;
    private final DeliveryProduct deliveryProduct;
    private final PrintMode printMode;
    private final PrintSpectrum printSpectrum;
    private final LetterMetaData metaData;
    private final Map<String, Object> additionalAttributes;

    private LetterCreateRequest(Builder builder) {
        this.filePath = builder.filePath;
        this.fileBytes = builder.fileBytes;
        this.fileOriginalName = builder.fileOriginalName;
        this.addressPosition = builder.addressPosition;
        this.autoSend = builder.autoSend;
        this.deliveryProduct = builder.deliveryProduct;
        this.printMode = builder.printMode;
        this.printSpectrum = builder.printSpectrum;
        this.metaData = builder.metaData;
        this.additionalAttributes = builder.additionalAttributes;
    }

    public Path getFilePath() {
        return filePath;
    }

    public byte[] getFileBytes() {
        return fileBytes;
    }

    public String getFileOriginalName() {
        return fileOriginalName;
    }

    public AddressPosition getAddressPosition() {
        return addressPosition;
    }

    public boolean isAutoSend() {
        return autoSend;
    }

    public DeliveryProduct getDeliveryProduct() {
        return deliveryProduct;
    }

    public PrintMode getPrintMode() {
        return printMode;
    }

    public PrintSpectrum getPrintSpectrum() {
        return printSpectrum;
    }

    public LetterMetaData getMetaData() {
        return metaData;
    }

    public Map<String, Object> getAdditionalAttributes() {
        return additionalAttributes;
    }

    public boolean hasFilePath() {
        return filePath != null;
    }

    public boolean hasFileBytes() {
        return fileBytes != null && fileBytes.length > 0;
    }

    /**
     * Builds the JSON:API request body for creating a letter.
     *
     * @param fileUrl the file URL from the upload step
     * @param fileUrlSignature the file URL signature from the upload step
     * @return the JSON:API request object
     */
    public Map<String, Object> toJsonApiRequest(String fileUrl, String fileUrlSignature) {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("file_original_name", fileOriginalName);
        attributes.put("file_url", fileUrl);
        attributes.put("file_url_signature", fileUrlSignature);
        attributes.put("auto_send", autoSend);

        if (addressPosition != null) {
            attributes.put("address_position", addressPosition.getValue());
        }
        if (deliveryProduct != null) {
            attributes.put("delivery_product", deliveryProduct.getValue());
        }
        if (printMode != null) {
            attributes.put("print_mode", printMode.getValue());
        }
        if (printSpectrum != null) {
            attributes.put("print_spectrum", printSpectrum.getValue());
        }
        if (metaData != null) {
            attributes.put("meta_data", metaData.toMap());
        }

        if (additionalAttributes != null) {
            attributes.putAll(additionalAttributes);
        }

        Map<String, Object> data = new HashMap<>();
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
        private Path filePath;
        private byte[] fileBytes;
        private String fileOriginalName;
        private AddressPosition addressPosition;
        private boolean autoSend = false;
        private DeliveryProduct deliveryProduct;
        private PrintMode printMode;
        private PrintSpectrum printSpectrum;
        private LetterMetaData metaData;
        private Map<String, Object> additionalAttributes = new HashMap<>();

        private Builder() {
        }

        public Builder filePath(Path filePath) {
            this.filePath = filePath;
            return this;
        }

        public Builder filePath(String filePath) {
            this.filePath = Path.of(filePath);
            return this;
        }

        public Builder fileBytes(byte[] fileBytes) {
            this.fileBytes = fileBytes;
            return this;
        }

        public Builder fileOriginalName(String fileOriginalName) {
            this.fileOriginalName = fileOriginalName;
            return this;
        }

        public Builder addressPosition(AddressPosition addressPosition) {
            this.addressPosition = addressPosition;
            return this;
        }

        public Builder autoSend(boolean autoSend) {
            this.autoSend = autoSend;
            return this;
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

        public Builder additionalAttribute(String key, Object value) {
            this.additionalAttributes.put(key, value);
            return this;
        }

        public LetterCreateRequest build() {
            if (filePath == null && (fileBytes == null || fileBytes.length == 0)) {
                throw new IllegalArgumentException("Either filePath or fileBytes must be provided");
            }
            if (fileOriginalName == null || fileOriginalName.isBlank()) {
                throw new IllegalArgumentException("fileOriginalName is required");
            }
            return new LetterCreateRequest(this);
        }
    }
}
