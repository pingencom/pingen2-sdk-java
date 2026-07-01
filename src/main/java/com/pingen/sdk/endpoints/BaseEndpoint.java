package com.pingen.sdk.endpoints;

import com.pingen.sdk.auth.AccessToken;
import com.pingen.sdk.auth.OAuth;
import com.pingen.sdk.client.ApiClient;
import com.pingen.sdk.client.ApiRequest;

/**
 * Base class for all API endpoint classes.
 * Provides common functionality for making authenticated requests.
 */
public abstract class BaseEndpoint {

    protected final ApiClient apiClient;
    protected final OAuth oauth;
    protected final String organisationId;

    private static final String JSON_API_CONTENT_TYPE = "application/vnd.api+json";

    protected BaseEndpoint(ApiClient apiClient, OAuth oauth, String organisationId) {
        this.apiClient = apiClient;
        this.oauth = oauth;
        this.organisationId = organisationId;
    }

    /**
     * Creates a new API request builder with common headers already set.
     *
     * @return a new ApiRequest.Builder with authentication and content-type headers
     */
    protected ApiRequest.Builder newRequest() {
        AccessToken token = oauth.getValidToken();

        return ApiRequest.builder()
                .header("Authorization", token.getAuthorizationHeader())
                .header("Content-Type", JSON_API_CONTENT_TYPE)
                .header("Accept", JSON_API_CONTENT_TYPE);
    }

    /**
     * Builds the base API URL for the organisation's resources.
     *
     * @param resource the resource path (e.g., "letters", "batches")
     * @return the full URL
     */
    protected String buildUrl(String resource) {
        return apiClient.getConfig().getApiUrl() + "/organisations/" + organisationId + "/" + resource;
    }

    /**
     * Builds a URL for a specific resource item.
     *
     * @param resource the resource path (e.g., "letters", "batches")
     * @param resourceId the resource ID
     * @return the full URL
     */
    protected String buildUrl(String resource, String resourceId) {
        return buildUrl(resource) + "/" + resourceId;
    }

    /**
     * Builds a URL for a resource action.
     *
     * @param resource the resource path (e.g., "letters", "batches")
     * @param resourceId the resource ID
     * @param action the action path (e.g., "send", "cancel")
     * @return the full URL
     */
    protected String buildUrl(String resource, String resourceId, String action) {
        return buildUrl(resource, resourceId) + "/" + action;
    }

    /**
     * Gets the organisation ID for this endpoint.
     *
     * @return the organisation ID
     */
    public String getOrganisationId() {
        return organisationId;
    }
}
