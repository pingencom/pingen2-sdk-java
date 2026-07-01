package com.pingen.sdk.client;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents an HTTP request to the Pingen API.
 */
public class ApiRequest {
    private final HttpMethod method;
    private final String url;
    private final Map<String, String> headers;
    private final Map<String, String> queryParams;
    private final String body;
    private final byte[] binaryBody;

    private ApiRequest(Builder builder) {
        this.method = builder.method;
        this.url = builder.url;
        this.headers = builder.headers;
        this.queryParams = builder.queryParams;
        this.body = builder.body;
        this.binaryBody = builder.binaryBody;
    }

    /** Returns the HTTP method for this request. */
    public HttpMethod getMethod() {
        return method;
    }

    /** Returns the base URL (without query parameters). */
    public String getUrl() {
        return url;
    }

    /** Returns the request headers. */
    public Map<String, String> getHeaders() {
        return headers;
    }

    /** Returns the query parameters. */
    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    /** Returns the text request body, or null if not set. */
    public String getBody() {
        return body;
    }

    /** Returns the binary request body, or null if not set. */
    public byte[] getBinaryBody() {
        return binaryBody;
    }

    /** Returns true if a non-empty binary body is set. */
    public boolean hasBinaryBody() {
        return binaryBody != null && binaryBody.length > 0;
    }

    /** Returns true if either a text or binary body is set. */
    public boolean hasBody() {
        return body != null || hasBinaryBody();
    }

    /**
     * Builds the complete URL with query parameters.
     *
     * @return the complete URL
     */
    public String getFullUrl() {
        if (queryParams.isEmpty()) {
            return url;
        }

        StringBuilder urlBuilder = new StringBuilder(url);
        urlBuilder.append("?");

        boolean first = true;
        for (Map.Entry<String, String> entry : queryParams.entrySet()) {
            if (!first) {
                urlBuilder.append("&");
            }
            urlBuilder.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8))
                      .append("=")
                      .append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
            first = false;
        }

        return urlBuilder.toString();
    }

    /**
     * Creates a new builder for constructing an ApiRequest.
     *
     * @return a new Builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for creating ApiRequest instances.
     */
    public static class Builder {
        private HttpMethod method;
        private String url;
        private Map<String, String> headers = new HashMap<>();
        private Map<String, String> queryParams = new HashMap<>();
        private String body;
        private byte[] binaryBody;

        private Builder() {
        }

        /** Sets the HTTP method. Required. */
        public Builder method(HttpMethod method) {
            this.method = method;
            return this;
        }

        /** Sets the request URL (without query parameters). Required. */
        public Builder url(String url) {
            this.url = url;
            return this;
        }

        /** Adds a single request header. */
        public Builder header(String name, String value) {
            this.headers.put(name, value);
            return this;
        }

        /** Adds all entries from the given map as request headers. */
        public Builder headers(Map<String, String> headers) {
            this.headers.putAll(headers);
            return this;
        }

        /** Adds a query parameter; null values are silently ignored. */
        public Builder queryParam(String name, String value) {
            if (value != null) {
                this.queryParams.put(name, value);
            }
            return this;
        }

        /** Adds all entries from the given map as query parameters. */
        public Builder queryParams(Map<String, String> queryParams) {
            this.queryParams.putAll(queryParams);
            return this;
        }

        /** Sets the text (string) request body. */
        public Builder body(String body) {
            this.body = body;
            return this;
        }

        /** Sets the binary request body (e.g., for file uploads). */
        public Builder binaryBody(byte[] binaryBody) {
            this.binaryBody = binaryBody;
            return this;
        }

        /**
         * Builds the ApiRequest instance.
         *
         * @return a new ApiRequest
         * @throws IllegalArgumentException if method or url is missing
         */
        public ApiRequest build() {
            if (method == null) {
                throw new IllegalArgumentException("method is required");
            }
            if (url == null || url.isBlank()) {
                throw new IllegalArgumentException("url is required");
            }
            return new ApiRequest(this);
        }
    }
}
