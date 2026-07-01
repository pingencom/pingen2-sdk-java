package com.pingen.sdk.exception;

/**
 * Exception thrown when the rate limit is exceeded (429 Too Many Requests).
 */
public class RateLimitException extends ApiException {

    private final Integer retryAfter;
    private final Long rateLimitReset;

    public RateLimitException(String message, String requestId, Integer retryAfter, Long rateLimitReset) {
        super(429, message, requestId);
        this.retryAfter = retryAfter;
        this.rateLimitReset = rateLimitReset;
    }

    public RateLimitException(String message, String responseBody, String requestId, Integer retryAfter, Long rateLimitReset) {
        super(429, message, responseBody, requestId);
        this.retryAfter = retryAfter;
        this.rateLimitReset = rateLimitReset;
    }

    /**
     * Gets the number of seconds to wait before retrying.
     *
     * @return retry-after seconds, or null if not provided
     */
    public Integer getRetryAfter() {
        return retryAfter;
    }

    /**
     * Gets the timestamp when the rate limit resets.
     *
     * @return the reset timestamp, or null if not provided
     */
    public Long getRateLimitReset() {
        return rateLimitReset;
    }

    @Override
    public String getMessage() {
        String message = super.getMessage();
        if (retryAfter != null) {
            message += " Retry after " + retryAfter + " seconds.";
        }
        return message;
    }
}
