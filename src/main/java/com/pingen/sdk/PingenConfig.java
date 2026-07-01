package com.pingen.sdk;

import java.time.Duration;

/**
 * Configuration for the Pingen SDK client.
 * Manages environment settings, timeouts, and API endpoints.
 */
public class PingenConfig {

    /**
     * Enum representing the available Pingen API environments.
     */
    public enum Environment {
        /**
         * Production environment - real letters will be sent.
         */
        PRODUCTION("https://api.pingen.com", "https://identity.pingen.com"),

        /**
         * Staging environment - letters are simulated, not actually sent.
         */
        STAGING("https://api-staging.pingen.com", "https://identity-staging.pingen.com");

        private final String apiUrl;
        private final String identityUrl;

        Environment(String apiUrl, String identityUrl) {
            this.apiUrl = apiUrl;
            this.identityUrl = identityUrl;
        }

        public String getApiUrl() {
            return apiUrl;
        }

        public String getIdentityUrl() {
            return identityUrl;
        }
    }

    private final Environment environment;
    private final Duration connectTimeout;
    private final Duration requestTimeout;
    private final String clientId;
    private final String clientSecret;
    private final String apiUrlOverride;
    private final String identityUrlOverride;

    private PingenConfig(Builder builder) {
        this.environment = builder.environment;
        this.connectTimeout = builder.connectTimeout;
        this.requestTimeout = builder.requestTimeout;
        this.clientId = builder.clientId;
        this.clientSecret = builder.clientSecret;
        this.apiUrlOverride = builder.apiUrlOverride;
        this.identityUrlOverride = builder.identityUrlOverride;
    }

    /** Returns the configured environment (production or staging). */
    public Environment getEnvironment() {
        return environment;
    }

    /** Returns the base API URL, using the override if set, otherwise the environment default. */
    public String getApiUrl() {
        return apiUrlOverride != null ? apiUrlOverride : environment.getApiUrl();
    }

    /** Returns the identity/OAuth URL, using the override if set, otherwise the environment default. */
    public String getIdentityUrl() {
        return identityUrlOverride != null ? identityUrlOverride : environment.getIdentityUrl();
    }

    /** Returns the TCP connection timeout. */
    public Duration getConnectTimeout() {
        return connectTimeout;
    }

    /** Returns the per-request timeout. */
    public Duration getRequestTimeout() {
        return requestTimeout;
    }

    /** Returns the OAuth client ID. */
    public String getClientId() {
        return clientId;
    }

    /** Returns the OAuth client secret. */
    public String getClientSecret() {
        return clientSecret;
    }

    /**
     * Creates a new builder for PingenConfig.
     *
     * @return a new Builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for creating PingenConfig instances.
     */
    public static class Builder {
        private Environment environment = Environment.PRODUCTION;
        private Duration connectTimeout = Duration.ofSeconds(30);
        private Duration requestTimeout = Duration.ofMinutes(2);
        private String clientId;
        private String clientSecret;
        private String apiUrlOverride;
        private String identityUrlOverride;

        private Builder() {
        }

        /**
         * Sets the client ID for OAuth authentication.
         *
         * @param clientId the OAuth client ID
         * @return this builder
         */
        public Builder clientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        /**
         * Sets the client secret for OAuth authentication.
         *
         * @param clientSecret the OAuth client secret
         * @return this builder
         */
        public Builder clientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
            return this;
        }

        /**
         * Sets the environment to production (default).
         *
         * @return this builder
         */
        public Builder production() {
            this.environment = Environment.PRODUCTION;
            return this;
        }

        /**
         * Sets the environment to staging.
         *
         * @return this builder
         */
        public Builder staging() {
            this.environment = Environment.STAGING;
            return this;
        }

        /**
         * Sets a custom environment.
         *
         * @param environment the environment to use
         * @return this builder
         */
        public Builder environment(Environment environment) {
            this.environment = environment;
            return this;
        }

        /**
         * Sets the connection timeout.
         *
         * @param connectTimeout the connection timeout duration
         * @return this builder
         */
        public Builder connectTimeout(Duration connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }

        /**
         * Overrides the API base URL. Useful for testing or proxied deployments.
         *
         * @param apiUrl the custom API base URL
         * @return this builder
         */
        public Builder apiUrl(String apiUrl) {
            this.apiUrlOverride = apiUrl;
            return this;
        }

        /**
         * Overrides the identity/OAuth base URL. Useful for testing or proxied deployments.
         *
         * @param identityUrl the custom identity base URL
         * @return this builder
         */
        public Builder identityUrl(String identityUrl) {
            this.identityUrlOverride = identityUrl;
            return this;
        }

        /**
         * Sets the request timeout.
         *
         * @param requestTimeout the request timeout duration
         * @return this builder
         */
        public Builder requestTimeout(Duration requestTimeout) {
            this.requestTimeout = requestTimeout;
            return this;
        }

        /**
         * Builds the PingenConfig instance.
         *
         * @return a new PingenConfig instance
         * @throws IllegalArgumentException if required fields are missing
         */
        public PingenConfig build() {
            if (clientId == null || clientId.isBlank()) {
                throw new IllegalArgumentException("clientId is required");
            }
            if (clientSecret == null || clientSecret.isBlank()) {
                throw new IllegalArgumentException("clientSecret is required");
            }
            return new PingenConfig(this);
        }
    }
}
