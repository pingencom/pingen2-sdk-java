package com.pingen.sdk.models.email;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmailCreateAttributes {

    @JsonProperty("file_original_name")
    private final String fileOriginalName;
    @JsonProperty("file_url")
    private final String fileUrl;
    @JsonProperty("file_url_signature")
    private final String fileUrlSignature;
    @JsonProperty("auto_send")
    private final boolean autoSend;
    @JsonProperty("meta_data")
    private final EmailMetaData metaData;

    public EmailCreateAttributes(String fileOriginalName, String fileUrl, String fileUrlSignature,
                                 boolean autoSend, EmailMetaData metaData) {
        this.fileOriginalName = fileOriginalName;
        this.fileUrl = fileUrl;
        this.fileUrlSignature = fileUrlSignature;
        this.autoSend = autoSend;
        this.metaData = metaData;
    }
}
