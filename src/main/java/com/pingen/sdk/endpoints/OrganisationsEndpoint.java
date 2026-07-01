package com.pingen.sdk.endpoints;

import com.fasterxml.jackson.core.type.TypeReference;
import com.pingen.sdk.auth.OAuth;
import com.pingen.sdk.client.ApiClient;
import com.pingen.sdk.client.ApiRequest;
import com.pingen.sdk.client.ApiResponse;
import com.pingen.sdk.client.HttpMethod;
import com.pingen.sdk.models.common.CollectionParams;
import com.pingen.sdk.models.common.Resource;
import com.pingen.sdk.models.common.internal.JsonApiCollection;
import com.pingen.sdk.models.common.internal.JsonApiResource;
import com.pingen.sdk.models.common.PagedResponse;
import com.pingen.sdk.models.organisation.Organisation;

import java.util.Optional;

/**
 * Endpoint for managing organisations.
 * Note: This endpoint doesn't require an organisationId since it's for listing/getting organisations.
 */
public class OrganisationsEndpoint {

    private final ApiClient apiClient;
    private final OAuth oauth;

    public OrganisationsEndpoint(ApiClient apiClient, OAuth oauth) {
        this.apiClient = apiClient;
        this.oauth = oauth;
    }

    /**
     * Retrieves the first page of organisations the authenticated user has access to.
     *
     * @return a paged response containing organisations
     */
    public PagedResponse<Organisation> getCollection() {
        return getCollection(CollectionParams.builder().build());
    }

    /**
     * Retrieves a page of organisations the authenticated user has access to.
     *
     * @param params pagination, filtering, and sorting options
     * @return a paged response containing organisations
     */
    public PagedResponse<Organisation> getCollection(CollectionParams params) {
        String url = apiClient.getConfig().getApiUrl() + "/organisations";

        ApiRequest.Builder builder = ApiRequest.builder()
                .method(HttpMethod.GET)
                .url(url)
                .header("Authorization", oauth.getValidToken().getAuthorizationHeader())
                .header("Accept", "application/vnd.api+json");

        params.toQueryParams().forEach(builder::queryParam);

        ApiResponse<JsonApiCollection<Organisation>> response =
                apiClient.execute(builder.build(), new TypeReference<JsonApiCollection<Organisation>>() {});

        return new PagedResponse<>(response.getBody());
    }

    /**
     * Retrieves a single organisation by ID.
     *
     * @param organisationId the organisation UUID
     * @return the organisation wrapped in an Optional, or empty if not found
     */
    public Optional<Resource<Organisation>> get(String organisationId) {
        String url = apiClient.getConfig().getApiUrl() + "/organisations/" + organisationId;

        ApiRequest request = ApiRequest.builder()
                .method(HttpMethod.GET)
                .url(url)
                .header("Authorization", oauth.getValidToken().getAuthorizationHeader())
                .header("Accept", "application/vnd.api+json")
                .build();

        ApiResponse<JsonApiResource<Organisation>> response =
                apiClient.execute(request, new TypeReference<JsonApiResource<Organisation>>() {});

        JsonApiResource<Organisation> raw = response.getBody();
        return Optional.ofNullable(raw).map(r -> new Resource<>(r.getData()));
    }
}
