package com.pingen.sdk;

import com.pingen.sdk.auth.OAuth;
import com.pingen.sdk.client.ApiClient;
import com.pingen.sdk.endpoints.BatchesEndpoint;
import com.pingen.sdk.endpoints.EBillsEndpoint;
import com.pingen.sdk.endpoints.EmailsEndpoint;
import com.pingen.sdk.endpoints.LettersEndpoint;
import com.pingen.sdk.endpoints.OrganisationsEndpoint;
import com.pingen.sdk.endpoints.UserEndpoint;
import com.pingen.sdk.endpoints.WebhooksEndpoint;

/**
 * Main entry point for the Pingen Java SDK.
 * <p>
 * Usage example:
 * <pre>{@code
 * Pingen pingen = Pingen.builder()
 *     .clientId("your-client-id")
 *     .clientSecret("your-client-secret")
 *     .staging()  // Optional: use staging environment
 *     .build();
 *
 * // List organisations
 * var organisations = pingen.organisations().getCollection();
 *
 * // Create a letter
 * String orgId = "organisation-uuid";
 * LetterCreateRequest request = LetterCreateRequest.builder()
 *     .filePath("path/to/document.pdf")
 *     .fileOriginalName("document.pdf")
 *     .addressPosition(AddressPosition.LEFT)
 *     .autoSend(true)
 *     .build();
 *
 * var letter = pingen.letters(orgId).create(request);
 * }</pre>
 */
public class Pingen {

    private final PingenConfig config;
    private final ApiClient apiClient;
    private final OAuth oauth;

    private Pingen(PingenConfig config) {
        this.config = config;
        this.apiClient = new ApiClient(config);
        this.oauth = new OAuth(apiClient, config);
    }

    /**
     * Gets the organisations endpoint for managing organisations.
     *
     * @return the organisations endpoint
     */
    public OrganisationsEndpoint organisations() {
        return new OrganisationsEndpoint(apiClient, oauth);
    }

    /**
     * Gets the letters endpoint for a specific organisation.
     *
     * @param organisationId the organisation ID
     * @return the letters endpoint
     */
    public LettersEndpoint letters(String organisationId) {
        return new LettersEndpoint(apiClient, oauth, organisationId);
    }

    /**
     * Gets the batches endpoint for a specific organisation.
     *
     * @param organisationId the organisation ID
     * @return the batches endpoint
     */
    public BatchesEndpoint batches(String organisationId) {
        return new BatchesEndpoint(apiClient, oauth, organisationId);
    }

    /**
     * Gets the webhooks endpoint for a specific organisation.
     *
     * @param organisationId the organisation ID
     * @return the webhooks endpoint
     */
    public WebhooksEndpoint webhooks(String organisationId) {
        return new WebhooksEndpoint(apiClient, oauth, organisationId);
    }

    /**
     * Gets the emails endpoint for a specific organisation.
     *
     * @param organisationId the organisation ID
     * @return the emails endpoint
     */
    public EmailsEndpoint emails(String organisationId) {
        return new EmailsEndpoint(apiClient, oauth, organisationId);
    }

    /**
     * Gets the e-bills endpoint for a specific organisation.
     *
     * @param organisationId the organisation ID
     * @return the e-bills endpoint
     */
    public EBillsEndpoint ebills(String organisationId) {
        return new EBillsEndpoint(apiClient, oauth, organisationId);
    }

    /**
     * Gets the user endpoint for the authenticated user's profile.
     *
     * @return the user endpoint
     */
    public UserEndpoint user() {
        return new UserEndpoint(apiClient, oauth);
    }

    /**
     * Gets the configuration for this Pingen client.
     *
     * @return the configuration
     */
    public PingenConfig getConfig() {
        return config;
    }

    /**
     * Gets the API client for advanced usage.
     *
     * @return the API client
     */
    public ApiClient getApiClient() {
        return apiClient;
    }

    /**
     * Gets the OAuth manager for advanced usage.
     *
     * @return the OAuth manager
     */
    public OAuth getOAuth() {
        return oauth;
    }

    /**
     * Creates a new Pingen builder.
     *
     * @return a new builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for creating Pingen instances.
     */
    public static class Builder {
        private final PingenConfig.Builder configBuilder = PingenConfig.builder();

        private Builder() {
        }

        /**
         * Sets the OAuth client ID.
         *
         * @param clientId the client ID
         * @return this builder
         */
        public Builder clientId(String clientId) {
            configBuilder.clientId(clientId);
            return this;
        }

        /**
         * Sets the OAuth client secret.
         *
         * @param clientSecret the client secret
         * @return this builder
         */
        public Builder clientSecret(String clientSecret) {
            configBuilder.clientSecret(clientSecret);
            return this;
        }

        /**
         * Configures the client to use the production environment (default).
         *
         * @return this builder
         */
        public Builder production() {
            configBuilder.production();
            return this;
        }

        /**
         * Configures the client to use the staging environment.
         * Staging is a test environment where letters are simulated, not actually sent.
         *
         * @return this builder
         */
        public Builder staging() {
            configBuilder.staging();
            return this;
        }

        /**
         * Sets the environment.
         *
         * @param environment the environment
         * @return this builder
         */
        public Builder environment(PingenConfig.Environment environment) {
            configBuilder.environment(environment);
            return this;
        }

        /**
         * Sets the connection timeout.
         *
         * @param timeout the timeout duration
         * @return this builder
         */
        public Builder connectTimeout(java.time.Duration timeout) {
            configBuilder.connectTimeout(timeout);
            return this;
        }

        /**
         * Sets the request timeout.
         *
         * @param timeout the timeout duration
         * @return this builder
         */
        public Builder requestTimeout(java.time.Duration timeout) {
            configBuilder.requestTimeout(timeout);
            return this;
        }

        /**
         * Builds the Pingen client instance.
         *
         * @return a new Pingen instance
         * @throws IllegalArgumentException if required configuration is missing
         */
        public Pingen build() {
            PingenConfig config = configBuilder.build();
            return new Pingen(config);
        }
    }
}
