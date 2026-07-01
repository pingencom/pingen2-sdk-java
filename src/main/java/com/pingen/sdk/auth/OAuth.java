package com.pingen.sdk.auth;

import com.pingen.sdk.PingenConfig;
import com.pingen.sdk.client.ApiClient;
import com.pingen.sdk.client.ApiRequest;
import com.pingen.sdk.client.ApiResponse;
import com.pingen.sdk.client.HttpMethod;
import com.pingen.sdk.exception.AuthenticationException;
import com.pingen.sdk.exception.PingenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Handles OAuth2 authentication for the Pingen API.
 * Supports client credentials grant type with automatic token refresh.
 * This class is thread-safe.
 */
public class OAuth {

    private static final Logger log = LoggerFactory.getLogger(OAuth.class);
    private static final long TOKEN_REFRESH_BUFFER_SECONDS = 300; // Refresh 5 minutes before expiry

    private final ApiClient apiClient;
    private final PingenConfig config;
    private final ReentrantLock lock = new ReentrantLock();

    private volatile AccessToken currentToken;

    public OAuth(ApiClient apiClient, PingenConfig config) {
        this.apiClient = apiClient;
        this.config = config;
    }

    /**
     * Gets a valid access token, refreshing if necessary.
     * This method is thread-safe and will only refresh the token once if multiple threads request it simultaneously.
     *
     * @return a valid AccessToken
     * @throws PingenException if token acquisition fails
     */
    public AccessToken getValidToken() {
        // Fast path: token exists and is still valid
        AccessToken token = currentToken;
        if (token != null && !token.isExpiringSoon(TOKEN_REFRESH_BUFFER_SECONDS)) {
            return token;
        }

        // Slow path: need to refresh token
        lock.lock();
        try {
            // Double-check after acquiring lock
            token = currentToken;
            if (token != null && !token.isExpiringSoon(TOKEN_REFRESH_BUFFER_SECONDS)) {
                return token;
            }

            log.debug("Acquiring new access token");
            currentToken = requestAccessToken();
            return currentToken;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Requests a new access token using client credentials grant.
     *
     * @return the new AccessToken
     * @throws AuthenticationException if authentication fails
     */
    public AccessToken requestAccessToken() {
        return requestAccessToken(null);
    }

    /**
     * Requests a new access token using client credentials grant with specific scopes.
     *
     * @param scopes the OAuth scopes to request (e.g., "letter batch webhook organisation_read")
     * @return the new AccessToken
     * @throws AuthenticationException if authentication fails
     */
    public AccessToken requestAccessToken(String scopes) {
        String tokenUrl = config.getIdentityUrl() + "/auth/access-tokens";

        Map<String, String> params = new HashMap<>();
        params.put("grant_type", "client_credentials");
        params.put("client_id", config.getClientId());
        params.put("client_secret", config.getClientSecret());
        if (scopes != null && !scopes.isBlank()) {
            params.put("scope", scopes);
        }

        String body = buildFormUrlEncodedBody(params);

        ApiRequest request = ApiRequest.builder()
                .method(HttpMethod.POST)
                .url(tokenUrl)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .body(body)
                .build();

        try {
            ApiResponse<AccessToken> response = apiClient.execute(request, AccessToken.class);
            AccessToken token = response.getBody();

            if (token == null || token.getAccessToken() == null) {
                throw new AuthenticationException(
                        "Token response did not contain access_token",
                        response.getRequestId()
                );
            }

            log.debug("Successfully acquired access token, expires in {} seconds", token.getExpiresIn());
            return token;
        } catch (AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            throw new AuthenticationException(
                    "Failed to acquire access token: " + e.getMessage(),
                    null
            );
        }
    }

    /**
     * Clears the current cached token, forcing a refresh on the next request.
     */
    public void clearToken() {
        lock.lock();
        try {
            currentToken = null;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Gets the current token without checking expiration or refreshing.
     *
     * @return the current token, or null if no token has been acquired
     */
    public AccessToken getCurrentToken() {
        return currentToken;
    }

    private String buildFormUrlEncodedBody(Map<String, String> params) {
        StringBuilder builder = new StringBuilder();
        boolean first = true;

        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (!first) {
                builder.append("&");
            }
            builder.append(ApiClient.urlEncode(entry.getKey()))
                    .append("=")
                    .append(ApiClient.urlEncode(entry.getValue()));
            first = false;
        }

        return builder.toString();
    }
}
