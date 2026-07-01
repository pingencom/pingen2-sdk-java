package com.pingen.sdk.exception;

/**
 * Exception thrown when the API returns an error response.
 */
public class ApiException extends PingenException {

    private final int statusCode;
    private final String responseBody;

    public ApiException(int statusCode, String message, String requestId) {
        super(message, requestId);
        this.statusCode = statusCode;
        this.responseBody = null;
    }

    public ApiException(int statusCode, String message, String responseBody, String requestId) {
        super(message, requestId);
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    public ApiException(int statusCode, String message, String responseBody, String requestId, Throwable cause) {
        super(message, requestId, cause);
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    /**
     * Gets the HTTP status code of the error response.
     *
     * @return the status code
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Gets the raw response body from the API.
     *
     * @return the response body, or null if not available
     */
    public String getResponseBody() {
        return responseBody;
    }
}
