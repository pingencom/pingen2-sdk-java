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
import com.pingen.sdk.models.email.Email;
import com.pingen.sdk.models.email.EmailCreateRequest;
import com.pingen.sdk.upload.FileUploader;
import com.pingen.sdk.upload.UploadResponse;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

/**
 * Endpoint for managing email deliveries.
 */
public class EmailsEndpoint extends BaseEndpoint {

    private final FileUploader fileUploader;

    public EmailsEndpoint(ApiClient apiClient, OAuth oauth, String organisationId) {
        super(apiClient, oauth, organisationId);
        this.fileUploader = new FileUploader(apiClient, oauth);
    }

    /**
     * Retrieves the first page of email deliveries for this organisation using default parameters.
     *
     * @return a paged response containing email deliveries
     */
    public PagedResponse<Email> getCollection() {
        return getCollection(CollectionParams.builder().build());
    }

    /**
     * Retrieves a page of email deliveries for this organisation.
     *
     * @param params pagination, filtering, and sorting options
     * @return a paged response containing email deliveries
     */
    public PagedResponse<Email> getCollection(CollectionParams params) {
        ApiRequest.Builder builder = newRequest()
                .method(HttpMethod.GET)
                .url(buildUrl("deliveries/emails"));

        params.toQueryParams().forEach(builder::queryParam);

        ApiResponse<JsonApiCollection<Email>> response =
                apiClient.execute(builder.build(), new TypeReference<JsonApiCollection<Email>>() {
                });

        return new PagedResponse<>(response.getBody());
    }

    /**
     * Retrieves a single email delivery by ID.
     *
     * @param emailId the email UUID
     * @return the email wrapped in an Optional, or empty if not found
     */
    public Optional<Resource<Email>> get(String emailId) {
        ApiRequest request = newRequest()
                .method(HttpMethod.GET)
                .url(buildUrl("deliveries/emails", emailId))
                .build();

        ApiResponse<JsonApiResource<Email>> response =
                apiClient.execute(request, new TypeReference<JsonApiResource<Email>>() {
                });

        JsonApiResource<Email> raw = response.getBody();
        return Optional.ofNullable(raw).map(r -> new Resource<>(r.getData()));
    }

    /**
     * Creates a new email delivery by uploading a file and submitting the create request.
     * Performs the 3-step upload process internally.
     *
     * @param createRequest the email creation parameters including file path or bytes
     * @return the created email resource
     * @throws PingenException if the file upload or API call fails
     */
    public Resource<Email> create(EmailCreateRequest createRequest) {
        UploadResponse uploadResponse;
        try {
            if (createRequest.hasFilePath()) {
                uploadResponse = fileUploader.uploadFile(createRequest.getFilePath());
            } else if (createRequest.hasFileBytes()) {
                uploadResponse = fileUploader.uploadFile(createRequest.getFileBytes());
            } else {
                throw new IllegalArgumentException("EmailCreateRequest must have either filePath or fileBytes");
            }
        } catch (IOException e) {
            throw new PingenException("Failed to read file: " + e.getMessage(), e);
        }

        ApiRequest request = newRequest()
                .method(HttpMethod.POST)
                .url(buildUrl("deliveries/emails"))
                .body(apiClient.toJson(createRequest.toJsonApiRequest(
                        uploadResponse.getUrl(), uploadResponse.getUrlSignature())))
                .build();

        ApiResponse<JsonApiResource<Email>> response =
                apiClient.execute(request, new TypeReference<JsonApiResource<Email>>() {
                });

        return new Resource<>(response.getBody().getData());
    }

    /**
     * Cancels an email delivery that has not yet been sent.
     *
     * @param emailId the email UUID
     */
    public void cancel(String emailId) {
        ApiRequest request = newRequest()
                .method(HttpMethod.PATCH)
                .url(buildUrl("deliveries/emails", emailId, "cancel"))
                .build();

        apiClient.execute(request);
    }

    /**
     * Deletes an email delivery permanently.
     *
     * @param emailId the email UUID
     */
    public void delete(String emailId) {
        ApiRequest request = newRequest()
                .method(HttpMethod.DELETE)
                .url(buildUrl("deliveries/emails", emailId))
                .build();

        apiClient.execute(request);
    }

    /**
     * Retrieves a pre-signed URL to download the email PDF.
     *
     * @param emailId the email UUID
     * @return the download URL
     */
    public String getFile(String emailId) {
        ApiRequest request = newRequest()
                .method(HttpMethod.GET)
                .url(buildUrl("deliveries/emails", emailId, "file"))
                .build();

        return apiClient.executeForFileUrl(request);
    }

    /**
     * Retrieves the lifecycle events for a specific email delivery.
     *
     * @param emailId the email UUID
     * @return a paged response containing delivery events
     */
    public PagedResponse<DeliveryEvent> getEvents(String emailId) {
        return getEvents(emailId, CollectionParams.builder().build());
    }

    /**
     * Retrieves the lifecycle events for a specific email delivery.
     *
     * @param emailId the email UUID
     * @param params  pagination and filtering options
     * @return a paged response containing delivery events
     */
    public PagedResponse<DeliveryEvent> getEvents(String emailId, CollectionParams params) {
        ApiRequest.Builder builder = newRequest()
                .method(HttpMethod.GET)
                .url(buildUrl("deliveries/emails", emailId, "events"));

        params.toQueryParams().forEach(builder::queryParam);

        ApiResponse<JsonApiCollection<DeliveryEvent>> response =
                apiClient.execute(builder.build(), new TypeReference<JsonApiCollection<DeliveryEvent>>() {
                });

        return new PagedResponse<>(response.getBody());
    }

    /**
     * Retrieves a pre-signed URL to download the scan/delivery image for a specific email event.
     *
     * @param emailId the email UUID
     * @param eventId the event UUID
     * @return the image download URL
     */
    public String getEventImage(String emailId, String eventId) {
        String url = buildUrl("deliveries/emails", emailId, "events") + "/" + eventId + "/image";

        ApiRequest request = newRequest()
                .method(HttpMethod.GET)
                .url(url)
                .build();

        return apiClient.executeForFileUrl(request);
    }
}
