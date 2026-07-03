package com.pingen.sdk.models.batch;

import com.pingen.sdk.models.common.internal.JsonApiRequest;
import com.pingen.sdk.models.common.internal.JsonApiRequestData;
import com.pingen.sdk.models.letter.AddressPosition;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Request object for creating a new batch.
 * Use the builder pattern for construction.
 */
public class BatchCreateRequest {

    private final Path filePath;
    private final byte[] fileBytes;
    private final String name;
    private final BatchIcon icon;
    private final String fileOriginalName;
    private final AddressPosition addressPosition;
    private final GroupingType groupingType;
    private final BatchGroupingSplitType groupingOptionsSplitType;
    private final BatchGroupingSplitPosition groupingOptionsSplitPosition;
    private final Integer groupingOptionsSplitSize;
    private final String groupingOptionsSplitSeparator;
    private final Map<String, Object> additionalAttributes;

    private BatchCreateRequest(Builder builder) {
        this.filePath = builder.filePath;
        this.fileBytes = builder.fileBytes;
        this.name = builder.name;
        this.icon = builder.icon;
        this.fileOriginalName = builder.fileOriginalName;
        this.addressPosition = builder.addressPosition;
        this.groupingType = builder.groupingType;
        this.groupingOptionsSplitType = builder.groupingOptionsSplitType;
        this.groupingOptionsSplitPosition = builder.groupingOptionsSplitPosition;
        this.groupingOptionsSplitSize = builder.groupingOptionsSplitSize;
        this.groupingOptionsSplitSeparator = builder.groupingOptionsSplitSeparator;
        this.additionalAttributes = builder.additionalAttributes;
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

    public JsonApiRequest<BatchCreateAttributes> toJsonApiRequest(String fileUrl, String fileUrlSignature) {
        BatchCreateAttributes attributes = new BatchCreateAttributes(
                fileOriginalName, fileUrl, fileUrlSignature,
                name, icon, addressPosition, groupingType,
                groupingOptionsSplitType, groupingOptionsSplitPosition,
                groupingOptionsSplitSize, groupingOptionsSplitSeparator,
                additionalAttributes);
        return new JsonApiRequest<>(new JsonApiRequestData<>("batches", attributes));
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Path filePath;
        private byte[] fileBytes;
        private String name;
        private BatchIcon icon;
        private String fileOriginalName;
        private AddressPosition addressPosition;
        private GroupingType groupingType;
        private BatchGroupingSplitType groupingOptionsSplitType;
        private BatchGroupingSplitPosition groupingOptionsSplitPosition;
        private Integer groupingOptionsSplitSize;
        private String groupingOptionsSplitSeparator;
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

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder icon(BatchIcon icon) {
            this.icon = icon;
            return this;
        }

        public Builder fileOriginalName(String n) {
            this.fileOriginalName = n;
            return this;
        }

        public Builder addressPosition(AddressPosition p) {
            this.addressPosition = p;
            return this;
        }

        public Builder groupingType(GroupingType t) {
            this.groupingType = t;
            return this;
        }

        public Builder groupingOptionsSplitType(BatchGroupingSplitType t) {
            this.groupingOptionsSplitType = t;
            return this;
        }

        public Builder groupingOptionsSplitPosition(BatchGroupingSplitPosition p) {
            this.groupingOptionsSplitPosition = p;
            return this;
        }

        public Builder groupingOptionsSplitSize(Integer size) {
            this.groupingOptionsSplitSize = size;
            return this;
        }

        public Builder groupingOptionsSplitSeparator(String sep) {
            this.groupingOptionsSplitSeparator = sep;
            return this;
        }
        public Builder additionalAttribute(String key, Object value) {
            this.additionalAttributes.put(key, value);
            return this;
        }

        public BatchCreateRequest build() {
            if (filePath == null && (fileBytes == null || fileBytes.length == 0))
                throw new IllegalArgumentException("Either filePath or fileBytes must be provided");
            if (fileOriginalName == null || fileOriginalName.isBlank())
                throw new IllegalArgumentException("fileOriginalName is required");
            if (name == null || name.isBlank())
                throw new IllegalArgumentException("name is required");
            if (addressPosition == null)
                throw new IllegalArgumentException("addressPosition is required");
            if (groupingType == null)
                throw new IllegalArgumentException("groupingType is required");
            if (groupingOptionsSplitType == null)
                throw new IllegalArgumentException("groupingOptionsSplitType is required");
            if (icon == null)
                throw new IllegalArgumentException("icon is required");
            return new BatchCreateRequest(this);
        }
    }
}
