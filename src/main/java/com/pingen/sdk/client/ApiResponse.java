package com.pingen.sdk.client;

import java.util.Map;

/**
 * Represents an HTTP response from the Pingen API.
 *
 * @param <T> the type of the response body
 */
public class ApiResponse<T> {
    private final int statusCode;
    private final T body;
    private final Map<String, String> headers;
    private final String requestId;

    public ApiResponse(int statusCode, T body, Map<String, String> headers) {
        this.statusCode = statusCode;
        this.body = body;
        this.headers = headers;
        this.requestId = headers.get("x-request-id");
    }

    /**
     * Gets the HTTP status code of the response.
     *
     * @return the status code
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Gets the response body.
     *
     * @return the response body
     */
    public T getBody() {
        return body;
    }

    /**
     * Gets all response headers.
     *
     * @return map of header names to values
     */
    public Map<String, String> getHeaders() {
        return headers;
    }

    /**
     * Gets a specific header value.
     *
     * @param name the header name (case-insensitive)
     * @return the header value, or null if not present
     */
    public String getHeader(String name) {
        return headers.get(name.toLowerCase());
    }

    /**
     * Gets the X-Request-Id header value for support inquiries.
     *
     * @return the request ID, or null if not present
     */
    public String getRequestId() {
        return requestId;
    }

    /**
     * Gets the rate limit maximum requests per period.
     *
     * @return the rate limit, or null if not present
     */
    public Integer getRateLimit() {
        String value = headers.get("x-ratelimit-limit");
        return value != null ? Integer.parseInt(value) : null;
    }

    /**
     * Gets the remaining requests in the current rate limit period.
     *
     * @return the remaining requests, or null if not present
     */
    public Integer getRateLimitRemaining() {
        String value = headers.get("x-ratelimit-remaining");
        return value != null ? Integer.parseInt(value) : null;
    }

    /**
     * Gets the timestamp when the rate limit period resets.
     *
     * @return the reset timestamp, or null if not present
     */
    public Long getRateLimitReset() {
        String value = headers.get("x-rate-limit-reset");
        return value != null ? Long.parseLong(value) : null;
    }

    /**
     * Gets the retry-after duration in seconds for rate limited requests.
     *
     * @return the retry-after seconds, or null if not present
     */
    public Integer getRetryAfter() {
        String value = headers.get("retry-after");
        return value != null ? Integer.parseInt(value) : null;
    }

    /**
     * Checks if the response indicates success (2xx status code).
     *
     * @return true if successful, false otherwise
     */
    public boolean isSuccessful() {
        return statusCode >= 200 && statusCode < 300;
    }
}
