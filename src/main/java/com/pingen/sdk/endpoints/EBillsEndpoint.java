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
import com.pingen.sdk.models.ebill.EBill;
import com.pingen.sdk.models.ebill.EBillCreateRequest;
import com.pingen.sdk.upload.FileUploader;
import com.pingen.sdk.upload.UploadResponse;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

/**
 * Endpoint for managing e-bill deliveries.
 */
public class EBillsEndpoint extends BaseEndpoint {

    private final FileUploader fileUploader;

    public EBillsEndpoint(ApiClient apiClient, OAuth oauth, String organisationId) {
        super(apiClient, oauth, organisationId);
        this.fileUploader = new FileUploader(apiClient, oauth);
    }

    /**
     * Retrieves the first page of e-bills for this organisation using default parameters.
     *
     * @return a paged response containing e-bills
     */
    public PagedResponse<EBill> getCollection() {
        return getCollection(CollectionParams.builder().build());
    }

    /**
     * Retrieves a page of e-bills for this organisation.
     *
     * @param params pagination, filtering, and sorting options
     * @return a paged response containing e-bills
     */
    public PagedResponse<EBill> getCollection(CollectionParams params) {
        ApiRequest.Builder builder = newRequest()
                .method(HttpMethod.GET)
                .url(buildUrl("deliveries/ebills"));

        params.toQueryParams().forEach(builder::queryParam);

        ApiResponse<JsonApiCollection<EBill>> response =
                apiClient.execute(builder.build(), new TypeReference<JsonApiCollection<EBill>>() {
                });

        return new PagedResponse<>(response.getBody());
    }

    /**
     * Retrieves a single e-bill by ID.
     *
     * @param ebillId the e-bill UUID
     * @return the e-bill wrapped in an Optional, or empty if not found
     */
    public Optional<Resource<EBill>> get(String ebillId) {
        ApiRequest request = newRequest()
                .method(HttpMethod.GET)
                .url(buildUrl("deliveries/ebills", ebillId))
                .build();

        ApiResponse<JsonApiResource<EBill>> response =
                apiClient.execute(request, new TypeReference<JsonApiResource<EBill>>() {
                });

        JsonApiResource<EBill> raw = response.getBody();
        return Optional.ofNullable(raw).map(r -> new Resource<>(r.getData()));
    }

    /**
     * Creates a new e-bill delivery by uploading a file and submitting the create request.
     * Performs the 3-step upload process internally.
     *
     * @param createRequest the e-bill creation parameters including file path or bytes
     * @return the created e-bill resource
     * @throws PingenException if the file upload or API call fails
     */
    public Resource<EBill> create(EBillCreateRequest createRequest) {
        UploadResponse uploadResponse;
        try {
            if (createRequest.hasFilePath()) {
                uploadResponse = fileUploader.uploadFile(createRequest.getFilePath());
            } else if (createRequest.hasFileBytes()) {
                uploadResponse = fileUploader.uploadFile(createRequest.getFileBytes());
            } else {
                throw new IllegalArgumentException("EBillCreateRequest must have either filePath or fileBytes");
            }
        } catch (IOException e) {
            throw new PingenException("Failed to read file: " + e.getMessage(), e);
        }

        ApiRequest request = newRequest()
                .method(HttpMethod.POST)
                .url(buildUrl("deliveries/ebills"))
                .body(apiClient.toJson(createRequest.toJsonApiRequest(
                        uploadResponse.getUrl(), uploadResponse.getUrlSignature())))
                .build();

        ApiResponse<JsonApiResource<EBill>> response =
                apiClient.execute(request, new TypeReference<JsonApiResource<EBill>>() {
                });

        return new Resource<>(response.getBody().getData());
    }

    /**
     * Sends an e-bill that is in draft state.
     *
     * @param ebillId the e-bill UUID
     * @return the updated e-bill resource
     */
    public Resource<EBill> send(String ebillId) {
        ApiRequest request = newRequest()
                .method(HttpMethod.PATCH)
                .url(buildUrl("deliveries/ebills", ebillId, "send"))
                .build();

        ApiResponse<JsonApiResource<EBill>> response =
                apiClient.execute(request, new TypeReference<JsonApiResource<EBill>>() {
                });

        return new Resource<>(response.getBody().getData());
    }

    /**
     * Cancels an e-bill that has not yet been sent.
     *
     * @param ebillId the e-bill UUID
     */
    public void cancel(String ebillId) {
        ApiRequest request = newRequest()
                .method(HttpMethod.PATCH)
                .url(buildUrl("deliveries/ebills", ebillId, "cancel"))
                .build();

        apiClient.execute(request);
    }

    /**
     * Deletes an e-bill permanently.
     *
     * @param ebillId the e-bill UUID
     */
    public void delete(String ebillId) {
        ApiRequest request = newRequest()
                .method(HttpMethod.DELETE)
                .url(buildUrl("deliveries/ebills", ebillId))
                .build();

        apiClient.execute(request);
    }

    /**
     * Retrieves a pre-signed URL to download the e-bill PDF.
     *
     * @param ebillId the e-bill UUID
     * @return the download URL
     */
    public String getFile(String ebillId) {
        ApiRequest request = newRequest()
                .method(HttpMethod.GET)
                .url(buildUrl("deliveries/ebills", ebillId, "file"))
                .build();

        return apiClient.executeForFileUrl(request);
    }

    /**
     * Retrieves the lifecycle events for a specific e-bill.
     *
     * @param ebillId the e-bill UUID
     * @return a paged response containing delivery events
     */
    public PagedResponse<DeliveryEvent> getEvents(String ebillId) {
        return getEvents(ebillId, CollectionParams.builder().build());
    }

    /**
     * Retrieves the lifecycle events for a specific e-bill.
     *
     * @param ebillId the e-bill UUID
     * @param params  pagination and filtering options
     * @return a paged response containing delivery events
     */
    public PagedResponse<DeliveryEvent> getEvents(String ebillId, CollectionParams params) {
        ApiRequest.Builder builder = newRequest()
                .method(HttpMethod.GET)
                .url(buildUrl("deliveries/ebills", ebillId, "events"));

        params.toQueryParams().forEach(builder::queryParam);

        ApiResponse<JsonApiCollection<DeliveryEvent>> response =
                apiClient.execute(builder.build(), new TypeReference<JsonApiCollection<DeliveryEvent>>() {
                });

        return new PagedResponse<>(response.getBody());
    }

    /**
     * Retrieves a pre-signed URL to download the scan/delivery image for a specific e-bill event.
     *
     * @param ebillId the e-bill UUID
     * @param eventId the event UUID
     * @return the image download URL
     */
    public String getEventImage(String ebillId, String eventId) {
        String url = buildUrl("deliveries/ebills", ebillId, "events") + "/" + eventId + "/image";

        ApiRequest request = newRequest()
                .method(HttpMethod.GET)
                .url(url)
                .build();

        return apiClient.executeForFileUrl(request);
    }
}
