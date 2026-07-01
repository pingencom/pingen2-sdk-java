package com.pingen.sdk.models.batch;

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

    public String getName() {
        return name;
    }

    public BatchIcon getIcon() {
        return icon;
    }

    public String getFileOriginalName() {
        return fileOriginalName;
    }

    public AddressPosition getAddressPosition() {
        return addressPosition;
    }

    public GroupingType getGroupingType() {
        return groupingType;
    }

    public BatchGroupingSplitType getGroupingOptionsSplitType() { return groupingOptionsSplitType; }
    public BatchGroupingSplitPosition getGroupingOptionsSplitPosition() { return groupingOptionsSplitPosition; }
    public Integer getGroupingOptionsSplitSize() { return groupingOptionsSplitSize; }
    public String getGroupingOptionsSplitSeparator() { return groupingOptionsSplitSeparator; }

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
     * Builds the JSON:API request body for creating a batch.
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

        if (name != null) {
            attributes.put("name", name);
        }
        if (icon != null) {
            attributes.put("icon", icon.getValue());
        }
        if (addressPosition != null) {
            attributes.put("address_position", addressPosition.getValue());
        }
        if (groupingType != null) {
            attributes.put("grouping_type", groupingType.getValue());
        }
        if (groupingOptionsSplitType != null) {
            attributes.put("grouping_options_split_type", groupingOptionsSplitType.getValue());
        }
        if (groupingOptionsSplitPosition != null) {
            attributes.put("grouping_options_split_position", groupingOptionsSplitPosition.getValue());
        }
        if (groupingOptionsSplitSize != null) {
            attributes.put("grouping_options_split_size", groupingOptionsSplitSize);
        }
        if (groupingOptionsSplitSeparator != null) {
            attributes.put("grouping_options_split_separator", groupingOptionsSplitSeparator);
        }

        if (additionalAttributes != null) {
            attributes.putAll(additionalAttributes);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("type", "batches");
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

        public Builder fileOriginalName(String fileOriginalName) {
            this.fileOriginalName = fileOriginalName;
            return this;
        }

        public Builder addressPosition(AddressPosition addressPosition) {
            this.addressPosition = addressPosition;
            return this;
        }

        public Builder groupingType(GroupingType groupingType) {
            this.groupingType = groupingType;
            return this;
        }

        public Builder groupingOptionsSplitType(BatchGroupingSplitType splitType) {
            this.groupingOptionsSplitType = splitType;
            return this;
        }

        public Builder groupingOptionsSplitPosition(BatchGroupingSplitPosition splitPosition) {
            this.groupingOptionsSplitPosition = splitPosition;
            return this;
        }

        /** Number of pages per letter when split_type is page */
        public Builder groupingOptionsSplitSize(Integer splitSize) {
            this.groupingOptionsSplitSize = splitSize;
            return this;
        }

        /** Separator string when split_type is custom */
        public Builder groupingOptionsSplitSeparator(String splitSeparator) {
            this.groupingOptionsSplitSeparator = splitSeparator;
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
