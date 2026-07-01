package com.pingen.sdk.models.email;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Request object for creating a new email delivery.
 * Use the builder pattern for construction.
 */
public class EmailCreateRequest {

    private final Path filePath;
    private final byte[] fileBytes;
    private final String fileOriginalName;
    private final boolean autoSend;
    private final EmailMetaData metaData;

    private EmailCreateRequest(Builder builder) {
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

    public String getFileOriginalName() {
        return fileOriginalName;
    }

    public boolean isAutoSend() {
        return autoSend;
    }

    public EmailMetaData getMetaData() {
        return metaData;
    }

    public boolean hasFilePath() {
        return filePath != null;
    }

    public boolean hasFileBytes() {
        return fileBytes != null && fileBytes.length > 0;
    }

    public Map<String, Object> toJsonApiRequest(String fileUrl, String fileUrlSignature) {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("file_original_name", fileOriginalName);
        attributes.put("file_url", fileUrl);
        attributes.put("file_url_signature", fileUrlSignature);
        attributes.put("auto_send", autoSend);

        if (metaData != null) {
            attributes.put("meta_data", metaData.toMap());
        }

        Map<String, Object> data = new HashMap<>();
        data.put("type", "emails");
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
        private boolean autoSend = false;
        private EmailMetaData metaData;

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

        public Builder fileOriginalName(String fileOriginalName) {
            this.fileOriginalName = fileOriginalName;
            return this;
        }

        public Builder autoSend(boolean autoSend) {
            this.autoSend = autoSend;
            return this;
        }

        public Builder metaData(EmailMetaData metaData) {
            this.metaData = metaData;
            return this;
        }

        public EmailCreateRequest build() {
            if (filePath == null && (fileBytes == null || fileBytes.length == 0)) {
                throw new IllegalArgumentException("Either filePath or fileBytes must be provided");
            }
            if (fileOriginalName == null || fileOriginalName.isBlank()) {
                throw new IllegalArgumentException("fileOriginalName is required");
            }
            return new EmailCreateRequest(this);
        }
    }
}
