package com.pingen.sdk.endpoints;

import com.fasterxml.jackson.core.type.TypeReference;
import com.pingen.sdk.auth.OAuth;
import com.pingen.sdk.client.ApiClient;
import com.pingen.sdk.client.ApiRequest;
import com.pingen.sdk.client.ApiResponse;
import com.pingen.sdk.client.HttpMethod;
import com.pingen.sdk.models.common.Resource;
import com.pingen.sdk.models.common.internal.JsonApiCollection;
import com.pingen.sdk.models.common.internal.JsonApiResource;
import com.pingen.sdk.models.common.PagedResponse;
import com.pingen.sdk.models.user.User;
import com.pingen.sdk.models.user.UserAssociation;

/**
 * Endpoint for accessing the authenticated user's profile and organisation associations.
 */
public class UserEndpoint {

    private final ApiClient apiClient;
    private final OAuth oauth;

    public UserEndpoint(ApiClient apiClient, OAuth oauth) {
        this.apiClient = apiClient;
        this.oauth = oauth;
    }

    /**
     * Retrieves the profile of the currently authenticated user.
     *
     * @return the user resource
     */
    public Resource<User> get() {
        String url = apiClient.getConfig().getApiUrl() + "/user";

        ApiRequest request = ApiRequest.builder()
                .method(HttpMethod.GET)
                .url(url)
                .header("Authorization", oauth.getValidToken().getAuthorizationHeader())
                .header("Accept", "application/vnd.api+json")
                .build();

        ApiResponse<JsonApiResource<User>> response =
                apiClient.execute(request, new TypeReference<JsonApiResource<User>>() {});

        return new Resource<>(response.getBody().getData());
    }

    /**
     * Retrieves the organisation associations (roles) for the authenticated user.
     *
     * @return a paged response containing user-organisation associations
     */
    public PagedResponse<UserAssociation> getAssociations() {
        String url = apiClient.getConfig().getApiUrl() + "/user/associations";

        ApiRequest request = ApiRequest.builder()
                .method(HttpMethod.GET)
                .url(url)
                .header("Authorization", oauth.getValidToken().getAuthorizationHeader())
                .header("Accept", "application/vnd.api+json")
                .build();

        ApiResponse<JsonApiCollection<UserAssociation>> response =
                apiClient.execute(request, new TypeReference<JsonApiCollection<UserAssociation>>() {});

        return new PagedResponse<>(response.getBody());
    }
}
