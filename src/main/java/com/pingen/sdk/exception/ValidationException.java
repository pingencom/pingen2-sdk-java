package com.pingen.sdk.exception;

/**
 * Exception thrown when request validation fails (422 Unprocessable Entity).
 */
public class ValidationException extends ApiException {

    public ValidationException(String message, String requestId) {
        super(422, message, requestId);
    }

    public ValidationException(String message, String responseBody, String requestId) {
        super(422, message, responseBody, requestId);
    }
}
