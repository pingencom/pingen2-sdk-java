package com.pingen.sdk.exception;

/**
 * Exception thrown when authentication fails (401 Unauthorized).
 */
public class AuthenticationException extends ApiException {

    public AuthenticationException(String message, String requestId) {
        super(401, message, requestId);
    }

    public AuthenticationException(String message, String responseBody, String requestId) {
        super(401, message, responseBody, requestId);
    }
}
