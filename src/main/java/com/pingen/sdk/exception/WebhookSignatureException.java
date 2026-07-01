package com.pingen.sdk.exception;

/**
 * Exception thrown when an incoming webhook request fails signature verification.
 */
public class WebhookSignatureException extends RuntimeException {

    private final int statusCode;

    public WebhookSignatureException(String message) {
        super(message);
        this.statusCode = 403;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
