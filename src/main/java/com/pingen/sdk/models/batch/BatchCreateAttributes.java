package com.pingen.sdk.models.batch;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pingen.sdk.models.letter.AddressPosition;

import java.util.Collections;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BatchCreateAttributes {

    @JsonProperty("file_original_name")
    private final String fileOriginalName;
    @JsonProperty("file_url")
    private final String fileUrl;
    @JsonProperty("file_url_signature")
    private final String fileUrlSignature;
    @JsonProperty("name")
    private final String name;
    @JsonProperty("icon")
    private final BatchIcon icon;
    @JsonProperty("address_position")
    private final AddressPosition addressPosition;
    @JsonProperty("grouping_type")
    private final GroupingType groupingType;
    @JsonProperty("grouping_options_split_type")
    private final BatchGroupingSplitType groupingOptionsSplitType;
    @JsonProperty("grouping_options_split_position")
    private final BatchGroupingSplitPosition groupingOptionsSplitPosition;
    @JsonProperty("grouping_options_split_size")
    private final Integer groupingOptionsSplitSize;
    @JsonProperty("grouping_options_split_separator")
    private final String groupingOptionsSplitSeparator;

    private final Map<String, Object> additionalAttributes;

    public BatchCreateAttributes(
            String fileOriginalName, String fileUrl, String fileUrlSignature,
            String name, BatchIcon icon, AddressPosition addressPosition,
            GroupingType groupingType,
            BatchGroupingSplitType groupingOptionsSplitType,
            BatchGroupingSplitPosition groupingOptionsSplitPosition,
            Integer groupingOptionsSplitSize, String groupingOptionsSplitSeparator,
            Map<String, Object> additionalAttributes) {
        this.fileOriginalName = fileOriginalName;
        this.fileUrl = fileUrl;
        this.fileUrlSignature = fileUrlSignature;
        this.name = name;
        this.icon = icon;
        this.addressPosition = addressPosition;
        this.groupingType = groupingType;
        this.groupingOptionsSplitType = groupingOptionsSplitType;
        this.groupingOptionsSplitPosition = groupingOptionsSplitPosition;
        this.groupingOptionsSplitSize = groupingOptionsSplitSize;
        this.groupingOptionsSplitSeparator = groupingOptionsSplitSeparator;
        this.additionalAttributes = additionalAttributes;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalAttributes() {
        return additionalAttributes != null ? additionalAttributes : Collections.emptyMap();
    }
}
