package com.pingen.sdk.models.letter;

import com.pingen.sdk.models.common.internal.JsonApiRequest;
import com.pingen.sdk.models.common.internal.JsonApiRequestData;

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

    public boolean isAutoSend() {
        return autoSend;
    }

    public boolean hasFilePath() {
        return filePath != null;
    }

    public boolean hasFileBytes() {
        return fileBytes != null && fileBytes.length > 0;
    }

    public JsonApiRequest<LetterCreateAttributes> toJsonApiRequest(String fileUrl, String fileUrlSignature) {
        LetterCreateAttributes attributes = new LetterCreateAttributes(
                fileOriginalName, fileUrl, fileUrlSignature,
                autoSend, addressPosition, deliveryProduct, printMode, printSpectrum,
                metaData, additionalAttributes);
        return new JsonApiRequest<>(new JsonApiRequestData<>("letters", attributes));
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

        public Builder fileOriginalName(String name) {
            this.fileOriginalName = name;
            return this;
        }

        public Builder addressPosition(AddressPosition p) {
            this.addressPosition = p;
            return this;
        }

        public Builder autoSend(boolean autoSend) {
            this.autoSend = autoSend;
            return this;
        }

        public Builder deliveryProduct(DeliveryProduct p) {
            this.deliveryProduct = p;
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

        public Builder additionalAttribute(String key, Object value) {
            this.additionalAttributes.put(key, value);
            return this;
        }

        public LetterCreateRequest build() {
            if (filePath == null && (fileBytes == null || fileBytes.length == 0))
                throw new IllegalArgumentException("Either filePath or fileBytes must be provided");
            if (fileOriginalName == null || fileOriginalName.isBlank())
                throw new IllegalArgumentException("fileOriginalName is required");
            return new LetterCreateRequest(this);
        }
    }
}
