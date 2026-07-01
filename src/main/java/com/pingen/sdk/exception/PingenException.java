package com.pingen.sdk.exception;

/**
 * Base exception for all Pingen SDK errors.
 */
public class PingenException extends RuntimeException {

    private final String requestId;

    public PingenException(String message) {
        super(message);
        this.requestId = null;
    }

    public PingenException(String message, Throwable cause) {
        super(message, cause);
        this.requestId = null;
    }

    public PingenException(String message, String requestId) {
        super(message);
        this.requestId = requestId;
    }

    public PingenException(String message, String requestId, Throwable cause) {
        super(message, cause);
        this.requestId = requestId;
    }

    /**
     * Gets the X-Request-Id from the API response for support inquiries.
     *
     * @return the request ID, or null if not available
     */
    public String getRequestId() {
        return requestId;
    }

    @Override
    public String getMessage() {
        String message = super.getMessage();
        if (requestId != null) {
            return message + " (Request ID: " + requestId + ")";
        }
        return message;
    }
}
