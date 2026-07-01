package com.pingen.sdk.upload;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the response from requesting a file upload URL.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UploadResponse {

    @JsonProperty("data")
    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    /**
     * Gets the upload URL.
     *
     * @return the URL where the file should be uploaded
     */
    public String getUrl() {
        return data != null && data.getAttributes() != null ? data.getAttributes().getUrl() : null;
    }

    /**
     * Gets the URL signature for creating resources.
     *
     * @return the URL signature
     */
    public String getUrlSignature() {
        return data != null && data.getAttributes() != null ? data.getAttributes().getUrlSignature() : null;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Data {

        @JsonProperty("type")
        private String type;

        @JsonProperty("attributes")
        private Attributes attributes;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Attributes getAttributes() {
            return attributes;
        }

        public void setAttributes(Attributes attributes) {
            this.attributes = attributes;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Attributes {

        @JsonProperty("url")
        private String url;

        @JsonProperty("url_signature")
        private String urlSignature;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getUrlSignature() {
            return urlSignature;
        }

        public void setUrlSignature(String urlSignature) {
            this.urlSignature = urlSignature;
        }
    }
}
