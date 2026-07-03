package com.pingen.sdk.models.ebill;

import com.pingen.sdk.models.common.internal.JsonApiRequest;
import com.pingen.sdk.models.common.internal.JsonApiRequestData;

import java.nio.file.Path;

/**
 * Request object for creating a new e-bill delivery.
 * Use the builder pattern for construction.
 */
public class EBillCreateRequest {

    private final Path filePath;
    private final byte[] fileBytes;
    private final String fileOriginalName;
    private final boolean autoSend;
    private final EBillMetaData metaData;

    private EBillCreateRequest(Builder builder) {
        this.filePath = builder.filePath;
        this.fileBytes = builder.fileBytes;
        this.fileOriginalName = builder.fileOriginalName;
        this.autoSend = builder.autoSend;
        this.metaData = builder.metaData;
    }

    public Path getFilePath() {
        return filePath;
    }

    public byte[] getFileBytes() {
        return fileBytes;
    }

    public boolean hasFilePath() {
        return filePath != null;
    }

    public boolean hasFileBytes() {
        return fileBytes != null && fileBytes.length > 0;
    }

    public JsonApiRequest<EBillCreateAttributes> toJsonApiRequest(String fileUrl, String fileUrlSignature) {
        EBillCreateAttributes attributes = new EBillCreateAttributes(
                fileOriginalName, fileUrl, fileUrlSignature, autoSend, metaData);
        return new JsonApiRequest<>(new JsonApiRequestData<>("ebills", attributes));
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Path filePath;
        private byte[] fileBytes;
        private String fileOriginalName;
        private boolean autoSend = false;
        private EBillMetaData metaData;

        private Builder() {
        }

        public Builder filePath(String filePath) {
            this.filePath = Path.of(filePath);
            return this;
        }

        public Builder filePath(Path filePath) {
            this.filePath = filePath;
            return this;
        }

        public Builder fileBytes(byte[] fileBytes) {
            this.fileBytes = fileBytes;
            return this;
        }

        public Builder fileOriginalName(String n) {
            this.fileOriginalName = n;
            return this;
        }

        public Builder autoSend(boolean autoSend) {
            this.autoSend = autoSend;
            return this;
        }

        public Builder metaData(EBillMetaData m) {
            this.metaData = m;
            return this;
        }

        public EBillCreateRequest build() {
            if (filePath == null && (fileBytes == null || fileBytes.length == 0))
                throw new IllegalArgumentException("Either filePath or fileBytes must be provided");
            if (fileOriginalName == null || fileOriginalName.isBlank())
                throw new IllegalArgumentException("fileOriginalName is required");
            return new EBillCreateRequest(this);
        }
    }
}
