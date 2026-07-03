package com.pingen.sdk.endpoints;

import com.fasterxml.jackson.core.type.TypeReference;
import com.pingen.sdk.auth.OAuth;
import com.pingen.sdk.client.ApiClient;
import com.pingen.sdk.client.ApiRequest;
import com.pingen.sdk.client.ApiResponse;
import com.pingen.sdk.client.HttpMethod;
import com.pingen.sdk.exception.PingenException;
import com.pingen.sdk.models.batch.Batch;
import com.pingen.sdk.models.batch.BatchCreateRequest;
import com.pingen.sdk.models.batch.BatchEvent;
import com.pingen.sdk.models.batch.BatchSendRequest;
import com.pingen.sdk.models.batch.BatchStatistics;
import com.pingen.sdk.models.batch.BatchUpdateRequest;
import com.pingen.sdk.models.batch.BatchDeleteAttributes;
import com.pingen.sdk.models.common.CollectionParams;
import com.pingen.sdk.models.common.Resource;
import com.pingen.sdk.models.common.internal.JsonApiCollection;
import com.pingen.sdk.models.common.internal.JsonApiRequest;
import com.pingen.sdk.models.common.internal.JsonApiRequestData;
import com.pingen.sdk.models.common.internal.JsonApiResource;
import com.pingen.sdk.models.common.PagedResponse;
import com.pingen.sdk.upload.FileUploader;
import com.pingen.sdk.upload.UploadResponse;

import java.io.IOException;
import java.util.Optional;

/**
 * Endpoint for managing batches.
 */
public class BatchesEndpoint extends BaseEndpoint {

    private final FileUploader fileUploader;

    public BatchesEndpoint(ApiClient apiClient, OAuth oauth, String organisationId) {
        super(apiClient, oauth, organisationId);
        this.fileUploader = new FileUploader(apiClient, oauth);
    }

    /**
     * Retrieves the first page of batches for this organisation using default parameters.
     *
     * @return a paged response containing batches
     */
    public PagedResponse<Batch> getCollection() {
        return getCollection(CollectionParams.builder().build());
    }

    /**
     * Retrieves a page of batches for this organisation.
     *
     * @param params pagination, filtering, and sorting options
     * @return a paged response containing batches
     */
    public PagedResponse<Batch> getCollection(CollectionParams params) {
        ApiRequest.Builder builder = newRequest()
                .method(HttpMethod.GET)
                .url(buildUrl("batches"));

        params.toQueryParams().forEach(builder::queryParam);

        ApiResponse<JsonApiCollection<Batch>> response =
                apiClient.execute(builder.build(), new TypeReference<JsonApiCollection<Batch>>() {
                });

        return new PagedResponse<>(response.getBody());
    }

    /**
     * Retrieves a single batch by ID.
     *
     * @param batchId the batch UUID
     * @return the batch wrapped in an Optional, or empty if not found
     */
    public Optional<Resource<Batch>> get(String batchId) {
        ApiRequest request = newRequest()
                .method(HttpMethod.GET)
                .url(buildUrl("batches", batchId))
                .build();

        ApiResponse<JsonApiResource<Batch>> response =
                apiClient.execute(request, new TypeReference<JsonApiResource<Batch>>() {
                });

        JsonApiResource<Batch> raw = response.getBody();
        return Optional.ofNullable(raw).map(r -> new Resource<>(r.getData()));
    }

    /**
     * Creates a new batch by uploading a file and submitting the create request.
     * Performs the 3-step upload process internally.
     *
     * @param createRequest the batch creation parameters including file path or bytes
     * @return the created batch resource
     * @throws PingenException if the file upload or API call fails
     */
    public Resource<Batch> create(BatchCreateRequest createRequest) {
        UploadResponse uploadResponse;
        try {
            if (createRequest.hasFilePath()) {
                uploadResponse = fileUploader.uploadFile(createRequest.getFilePath());
            } else if (createRequest.hasFileBytes()) {
                uploadResponse = fileUploader.uploadFile(createRequest.getFileBytes());
            } else {
                throw new IllegalArgumentException("BatchCreateRequest must have either filePath or fileBytes");
            }
        } catch (IOException e) {
            throw new PingenException("Failed to read file: " + e.getMessage(), e);
        }

        ApiRequest request = newRequest()
                .method(HttpMethod.POST)
                .url(buildUrl("batches"))
                .body(apiClient.toJson(createRequest.toJsonApiRequest(
                        uploadResponse.getUrl(), uploadResponse.getUrlSignature())))
                .build();

        ApiResponse<JsonApiResource<Batch>> response =
                apiClient.execute(request, new TypeReference<JsonApiResource<Batch>>() {
                });

        return new Resource<>(response.getBody().getData());
    }

    /**
     * Updates an existing batch's name and/or icon.
     *
     * @param batchId       the batch UUID
     * @param updateRequest the fields to update
     */
    public void update(String batchId, BatchUpdateRequest updateRequest) {
        ApiRequest request = newRequest()
                .method(HttpMethod.PATCH)
                .url(buildUrl("batches", batchId))
                .body(apiClient.toJson(updateRequest.toJsonApiRequest(batchId)))
                .build();

        apiClient.execute(request);
    }

    /**
     * Sends a batch that is in draft state.
     *
     * @param batchId     the batch UUID
     * @param sendRequest the send parameters (delivery products per country, print mode, spectrum)
     * @return the updated batch resource
     */
    public Resource<Batch> send(String batchId, BatchSendRequest sendRequest) {
        ApiRequest request = newRequest()
                .method(HttpMethod.PATCH)
                .url(buildUrl("batches", batchId, "send"))
                .body(apiClient.toJson(sendRequest.toJsonApiRequest(batchId)))
                .build();

        ApiResponse<JsonApiResource<Batch>> response =
                apiClient.execute(request, new TypeReference<JsonApiResource<Batch>>() {
                });

        return new Resource<>(response.getBody().getData());
    }

    /**
     * Cancels a batch that has not yet been sent.
     *
     * @param batchId the batch UUID
     */
    public void cancel(String batchId) {
        ApiRequest request = newRequest()
                .method(HttpMethod.PATCH)
                .url(buildUrl("batches", batchId, "cancel"))
                .build();

        apiClient.execute(request);
    }

    /**
     * Deletes the batch but keeps its individual letters.
     *
     * @param batchId the batch UUID
     */
    public void deleteWithoutLetters(String batchId) {
        delete(batchId, false);
    }

    /**
     * Deletes the batch and all its individual letters.
     *
     * @param batchId the batch UUID
     */
    public void deleteWithLetters(String batchId) {
        delete(batchId, true);
    }

    /**
     * Deletes a batch, optionally also deleting its letters.
     *
     * @param batchId     the batch UUID
     * @param withLetters if true, all letters in the batch are also deleted
     */
    public void delete(String batchId, boolean withLetters) {
        ApiRequest request = newRequest()
                .method(HttpMethod.DELETE)
                .url(buildUrl("batches", batchId))
                .body(apiClient.toJson(new JsonApiRequest<>(
                        new JsonApiRequestData<>(batchId, "batches", new BatchDeleteAttributes(withLetters)))))
                .build();

        apiClient.execute(request);
    }

    /**
     * Retrieves the lifecycle events for a specific batch.
     *
     * @param batchId the batch UUID
     * @return a paged response containing batch events
     */
    public PagedResponse<BatchEvent> getEvents(String batchId) {
        return getEvents(batchId, CollectionParams.builder().build());
    }

    /**
     * Retrieves the lifecycle events for a specific batch.
     *
     * @param batchId the batch UUID
     * @param params  pagination and filtering options
     * @return a paged response containing batch events
     */
    public PagedResponse<BatchEvent> getEvents(String batchId, CollectionParams params) {
        ApiRequest.Builder builder = newRequest()
                .method(HttpMethod.GET)
                .url(buildUrl("batches", batchId, "events"));

        params.toQueryParams().forEach(builder::queryParam);

        ApiResponse<JsonApiCollection<BatchEvent>> response =
                apiClient.execute(builder.build(), new TypeReference<JsonApiCollection<BatchEvent>>() {
                });

        return new PagedResponse<>(response.getBody());
    }

    /**
     * Retrieves statistical information about a batch (letter counts by country, region, group).
     *
     * @param batchId the batch UUID
     * @return the batch statistics resource
     */
    public Resource<BatchStatistics> getStatistics(String batchId) {
        ApiRequest request = newRequest()
                .method(HttpMethod.GET)
                .url(buildUrl("batches", batchId, "statistics"))
                .build();

        ApiResponse<JsonApiResource<BatchStatistics>> response =
                apiClient.execute(request, new TypeReference<JsonApiResource<BatchStatistics>>() {
                });

        return new Resource<>(response.getBody().getData());
    }
}
