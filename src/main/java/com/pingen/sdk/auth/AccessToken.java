package com.pingen.sdk.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

/**
 * Represents an OAuth2 access token with expiration information.
 */
public class AccessToken {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("expires_in")
    private long expiresIn;

    @JsonProperty("refresh_token")
    private String refreshToken;

    private Instant issuedAt;

    public AccessToken() {
        this.issuedAt = Instant.now();
    }

    public AccessToken(String accessToken, String tokenType, long expiresIn) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
        this.issuedAt = Instant.now();
    }

    /**
     * Gets the access token string.
     *
     * @return the access token
     */
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    /**
     * Gets the token type (usually "Bearer").
     *
     * @return the token type
     */
    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    /**
     * Gets the number of seconds until the token expires.
     *
     * @return expiration time in seconds
     */
    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }

    /**
     * Gets the refresh token for obtaining a new access token.
     *
     * @return the refresh token, or null if not provided
     */
    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    /**
     * Gets the instant when this token was issued.
     *
     * @return the issued timestamp
     */
    public Instant getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(Instant issuedAt) {
        this.issuedAt = issuedAt;
    }

    /**
     * Gets the instant when this token will expire.
     *
     * @return the expiration timestamp
     */
    public Instant getExpiresAt() {
        return issuedAt.plusSeconds(expiresIn);
    }

    /**
     * Checks if the token has expired.
     *
     * @return true if the token has expired
     */
    public boolean isExpired() {
        return Instant.now().isAfter(getExpiresAt());
    }

    /**
     * Checks if the token is about to expire within the specified buffer time.
     *
     * @param bufferSeconds number of seconds before expiration to consider as "about to expire"
     * @return true if the token will expire within the buffer time
     */
    public boolean isExpiringSoon(long bufferSeconds) {
        Instant expiresAt = getExpiresAt();
        Instant threshold = Instant.now().plusSeconds(bufferSeconds);
        return threshold.isAfter(expiresAt);
    }

    /**
     * Gets the Authorization header value for this token.
     *
     * @return the formatted Authorization header value (e.g., "Bearer xxx")
     */
    public String getAuthorizationHeader() {
        return tokenType + " " + accessToken;
    }
}
