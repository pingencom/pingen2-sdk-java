package com.pingen.sdk.endpoints;

import com.fasterxml.jackson.core.type.TypeReference;
import com.pingen.sdk.auth.OAuth;
import com.pingen.sdk.client.ApiClient;
import com.pingen.sdk.client.ApiRequest;
import com.pingen.sdk.client.ApiResponse;
import com.pingen.sdk.client.HttpMethod;
import com.pingen.sdk.exception.PingenException;
import com.pingen.sdk.models.common.CollectionParams;
import com.pingen.sdk.models.common.DeliveryEvent;
import com.pingen.sdk.models.common.Resource;
import com.pingen.sdk.models.common.internal.JsonApiCollection;
import com.pingen.sdk.models.common.internal.JsonApiResource;
import com.pingen.sdk.models.common.PagedResponse;
import com.pingen.sdk.models.letter.Letter;
import com.pingen.sdk.models.letter.LetterCreateRequest;
import com.pingen.sdk.models.letter.LetterPriceCalculatorRequest;
import com.pingen.sdk.models.letter.LetterPriceCalculatorResult;
import com.pingen.sdk.models.letter.LetterSendRequest;
import com.pingen.sdk.upload.FileUploader;
import com.pingen.sdk.upload.UploadResponse;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

/**
 * Endpoint for managing letters.
 */
public class LettersEndpoint extends BaseEndpoint {

    private final FileUploader fileUploader;

    public LettersEndpoint(ApiClient apiClient, OAuth oauth, String organisationId) {
        super(apiClient, oauth, organisationId);
        this.fileUploader = new FileUploader(apiClient, oauth);
    }

    /**
     * Retrieves the first page of letters for this organisation using default parameters.
     *
     * @return a paged response containing letters
     */
    public PagedResponse<Letter> getCollection() {
        return getCollection(CollectionParams.builder().build());
    }

    /**
     * Retrieves a page of letters for this organisation.
     *
     * @param params pagination, filtering, and sorting options
     * @return a paged response containing letters
     */
    public PagedResponse<Letter> getCollection(CollectionParams params) {
        ApiRequest.Builder builder = newRequest()
                .method(HttpMethod.GET)
                .url(buildUrl("deliveries/letters"));

        params.toQueryParams().forEach(builder::queryParam);

        ApiResponse<JsonApiCollection<Letter>> response =
                apiClient.execute(builder.build(), new TypeReference<JsonApiCollection<Letter>>() {
                });

        return new PagedResponse<>(response.getBody());
    }

    /**
     * Retrieves a single letter by ID.
     *
     * @param letterId the letter UUID
     * @return the letter wrapped in an Optional, or empty if not found
     */
    public Optional<Resource<Letter>> get(String letterId) {
        ApiRequest request = newRequest()
                .method(HttpMethod.GET)
                .url(buildUrl("deliveries/letters", letterId))
                .build();

        ApiResponse<JsonApiResource<Letter>> response =
                apiClient.execute(request, new TypeReference<JsonApiResource<Letter>>() {
                });

        JsonApiResource<Letter> raw = response.getBody();
        return Optional.ofNullable(raw).map(r -> new Resource<>(r.getData()));
    }

    /**
     * Creates a new letter by uploading a file and submitting the create request.
     * Performs the 3-step upload process internally.
     *
     * @param createRequest the letter creation parameters including file path or bytes
     * @return the created letter resource
     * @throws PingenException if the file upload or API call fails
     */
    public Resource<Letter> create(LetterCreateRequest createRequest) {
        UploadResponse uploadResponse;
        try {
            if (createRequest.hasFilePath()) {
                uploadResponse = fileUploader.uploadFile(createRequest.getFilePath());
            } else if (createRequest.hasFileBytes()) {
                uploadResponse = fileUploader.uploadFile(createRequest.getFileBytes());
            } else {
                throw new IllegalArgumentException("LetterCreateRequest must have either filePath or fileBytes");
            }
        } catch (IOException e) {
            throw new PingenException("Failed to read file: " + e.getMessage(), e);
        }

        Map<String, Object> requestBody = createRequest.toJsonApiRequest(
                uploadResponse.getUrl(),
                uploadResponse.getUrlSignature()
        );

        ApiRequest request = newRequest()
                .method(HttpMethod.POST)
                .url(buildUrl("deliveries/letters"))
                .body(apiClient.toJson(requestBody))
                .build();

        ApiResponse<JsonApiResource<Letter>> response =
                apiClient.execute(request, new TypeReference<JsonApiResource<Letter>>() {
                });

        return new Resource<>(response.getBody().getData());
    }

    /**
     * Sends a letter that is in draft state.
     *
     * @param letterId    the letter UUID
     * @param sendRequest the send parameters (delivery product, print mode, spectrum)
     * @return the updated letter resource
     */
    public Resource<Letter> send(String letterId, LetterSendRequest sendRequest) {
        ApiRequest request = newRequest()
                .method(HttpMethod.PATCH)
                .url(buildUrl("deliveries/letters", letterId, "send"))
                .body(apiClient.toJson(sendRequest.toJsonApiRequest(letterId)))
                .build();

        ApiResponse<JsonApiResource<Letter>> response =
                apiClient.execute(request, new TypeReference<JsonApiResource<Letter>>() {
                });

        return new Resource<>(response.getBody().getData());
    }

    /**
     * Cancels a letter that has not yet been sent.
     *
     * @param letterId the letter UUID
     */
    public void cancel(String letterId) {
        ApiRequest request = newRequest()
                .method(HttpMethod.PATCH)
                .url(buildUrl("deliveries/letters", letterId, "cancel"))
                .build();

        apiClient.execute(request);
    }

    /**
     * Deletes a letter permanently.
     *
     * @param letterId the letter UUID
     */
    public void delete(String letterId) {
        ApiRequest request = newRequest()
                .method(HttpMethod.DELETE)
                .url(buildUrl("deliveries/letters", letterId))
                .build();

        apiClient.execute(request);
    }

    /**
     * Retrieves the lifecycle events for a specific letter.
     *
     * @param letterId the letter UUID
     * @return a paged response containing delivery events
     */
    public PagedResponse<DeliveryEvent> getEvents(String letterId) {
        return getEvents(letterId, CollectionParams.builder().build());
    }

    /**
     * Retrieves the lifecycle events for a specific letter.
     *
     * @param letterId the letter UUID
     * @param params   pagination and filtering options
     * @return a paged response containing delivery events
     */
    public PagedResponse<DeliveryEvent> getEvents(String letterId, CollectionParams params) {
        ApiRequest.Builder builder = newRequest()
                .method(HttpMethod.GET)
                .url(buildUrl("deliveries/letters", letterId, "events"));

        params.toQueryParams().forEach(builder::queryParam);

        ApiResponse<JsonApiCollection<DeliveryEvent>> response =
                apiClient.execute(builder.build(), new TypeReference<JsonApiCollection<DeliveryEvent>>() {
                });

        return new PagedResponse<>(response.getBody());
    }

    /**
     * Retrieves a pre-signed URL to download the letter PDF.
     *
     * @param letterId the letter UUID
     * @return the download URL
     */
    public String getFile(String letterId) {
        ApiRequest request = newRequest()
                .method(HttpMethod.GET)
                .url(buildUrl("deliveries/letters", letterId, "file"))
                .build();

        return apiClient.executeForFileUrl(request);
    }

    /**
     * Retrieves a pre-signed URL to download the scan/delivery image for a specific event.
     *
     * @param letterId the letter UUID
     * @param eventId  the event UUID
     * @return the image download URL
     */
    public String getEventImage(String letterId, String eventId) {
        String url = buildUrl("deliveries/letters", letterId, "events") + "/" + eventId + "/image";

        ApiRequest request = newRequest()
                .method(HttpMethod.GET)
                .url(url)
                .build();

        return apiClient.executeForFileUrl(request);
    }

    /**
     * Calculates the price for sending a letter with the given parameters.
     *
     * @param calculatorRequest the parameters to estimate the price for
     * @return the price calculator result with currency and price value
     */
    public Resource<LetterPriceCalculatorResult> calculatePrice(LetterPriceCalculatorRequest calculatorRequest) {
        ApiRequest request = newRequest()
                .method(HttpMethod.POST)
                .url(buildUrl("deliveries/letters/price-calculator"))
                .body(apiClient.toJson(calculatorRequest.toJsonApiRequest()))
                .build();

        ApiResponse<JsonApiResource<LetterPriceCalculatorResult>> response =
                apiClient.execute(request, new TypeReference<JsonApiResource<LetterPriceCalculatorResult>>() {
                });

        return new Resource<>(response.getBody().getData());
    }

    /**
     * Retrieves the global feed of delivered events across all letters in this organisation.
     *
     * @return a paged response containing delivered events
     */
    public PagedResponse<DeliveryEvent> getDeliveredEvents() {
        return getGlobalEvents("delivered", CollectionParams.builder().build());
    }

    /**
     * Retrieves the global feed of delivered events across all letters in this organisation.
     *
     * @param params pagination and filtering options
     * @return a paged response containing delivered events
     */
    public PagedResponse<DeliveryEvent> getDeliveredEvents(CollectionParams params) {
        return getGlobalEvents("delivered", params);
    }

    /**
     * Retrieves the global feed of sent events across all letters in this organisation.
     *
     * @return a paged response containing sent events
     */
    public PagedResponse<DeliveryEvent> getSentEvents() {
        return getGlobalEvents("sent", CollectionParams.builder().build());
    }

    /**
     * Retrieves the global feed of sent events across all letters in this organisation.
     *
     * @param params pagination and filtering options
     * @return a paged response containing sent events
     */
    public PagedResponse<DeliveryEvent> getSentEvents(CollectionParams params) {
        return getGlobalEvents("sent", params);
    }

    /**
     * Retrieves the global feed of issue events across all letters in this organisation.
     *
     * @return a paged response containing issue events
     */
    public PagedResponse<DeliveryEvent> getIssueEvents() {
        return getGlobalEvents("issues", CollectionParams.builder().build());
    }

    /**
     * Retrieves the global feed of issue events across all letters in this organisation.
     *
     * @param params pagination and filtering options
     * @return a paged response containing issue events
     */
    public PagedResponse<DeliveryEvent> getIssueEvents(CollectionParams params) {
        return getGlobalEvents("issues", params);
    }

    /**
     * Retrieves the global feed of undeliverable events across all letters in this organisation.
     *
     * @return a paged response containing undeliverable events
     */
    public PagedResponse<DeliveryEvent> getUndeliverableEvents() {
        return getGlobalEvents("undeliverable", CollectionParams.builder().build());
    }

    /**
     * Retrieves the global feed of undeliverable events across all letters in this organisation.
     *
     * @param params pagination and filtering options
     * @return a paged response containing undeliverable events
     */
    public PagedResponse<DeliveryEvent> getUndeliverableEvents(CollectionParams params) {
        return getGlobalEvents("undeliverable", params);
    }

    private PagedResponse<DeliveryEvent> getGlobalEvents(String type, CollectionParams params) {
        ApiRequest.Builder builder = newRequest()
                .method(HttpMethod.GET)
                .url(buildUrl("deliveries/letters/events/" + type));

        params.toQueryParams().forEach(builder::queryParam);

        ApiResponse<JsonApiCollection<DeliveryEvent>> response =
                apiClient.execute(builder.build(), new TypeReference<JsonApiCollection<DeliveryEvent>>() {
                });

        return new PagedResponse<>(response.getBody());
    }
}
