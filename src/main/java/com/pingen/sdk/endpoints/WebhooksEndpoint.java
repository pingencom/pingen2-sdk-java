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
import com.pingen.sdk.models.webhook.Webhook;
import com.pingen.sdk.models.webhook.WebhookCreateRequest;

import java.util.Optional;

/**
 * Endpoint for managing webhooks.
 */
public class WebhooksEndpoint extends BaseEndpoint {

    public WebhooksEndpoint(ApiClient apiClient, OAuth oauth, String organisationId) {
        super(apiClient, oauth, organisationId);
    }

    /**
     * Retrieves the first page of webhooks for this organisation using default parameters.
     *
     * @return a paged response containing webhooks
     */
    public PagedResponse<Webhook> getCollection() {
        return getCollection(CollectionParams.builder().build());
    }

    /**
     * Retrieves a page of webhooks for this organisation.
     *
     * @param params pagination, filtering, and sorting options
     * @return a paged response containing webhooks
     */
    public PagedResponse<Webhook> getCollection(CollectionParams params) {
        ApiRequest.Builder builder = newRequest()
                .method(HttpMethod.GET)
                .url(buildUrl("webhooks"));

        params.toQueryParams().forEach(builder::queryParam);

        ApiResponse<JsonApiCollection<Webhook>> response =
                apiClient.execute(builder.build(), new TypeReference<JsonApiCollection<Webhook>>() {});

        return new PagedResponse<>(response.getBody());
    }

    /**
     * Retrieves a single webhook by ID.
     *
     * @param webhookId the webhook UUID
     * @return the webhook wrapped in an Optional, or empty if not found
     */
    public Optional<Resource<Webhook>> get(String webhookId) {
        ApiRequest request = newRequest()
                .method(HttpMethod.GET)
                .url(buildUrl("webhooks", webhookId))
                .build();

        ApiResponse<JsonApiResource<Webhook>> response =
                apiClient.execute(request, new TypeReference<JsonApiResource<Webhook>>() {});

        JsonApiResource<Webhook> raw = response.getBody();
        return Optional.ofNullable(raw).map(r -> new Resource<>(r.getData()));
    }

    /**
     * Creates a new webhook for this organisation.
     *
     * @param createRequest the webhook creation parameters (URL, event category, signing key)
     * @return the created webhook resource
     */
    public Resource<Webhook> create(WebhookCreateRequest createRequest) {
        ApiRequest request = newRequest()
                .method(HttpMethod.POST)
                .url(buildUrl("webhooks"))
                .body(apiClient.toJson(createRequest.toJsonApiRequest()))
                .build();

        ApiResponse<JsonApiResource<Webhook>> response =
                apiClient.execute(request, new TypeReference<JsonApiResource<Webhook>>() {});

        return new Resource<>(response.getBody().getData());
    }

    /**
     * Deletes a webhook permanently.
     *
     * @param webhookId the webhook UUID
     */
    public void delete(String webhookId) {
        ApiRequest request = newRequest()
                .method(HttpMethod.DELETE)
                .url(buildUrl("webhooks", webhookId))
                .build();

        apiClient.execute(request);
    }
}
