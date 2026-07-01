package com.pingen.sdk.upload;

import com.pingen.sdk.auth.OAuth;
import com.pingen.sdk.client.ApiClient;
import com.pingen.sdk.client.ApiRequest;
import com.pingen.sdk.client.ApiResponse;
import com.pingen.sdk.client.HttpMethod;
import com.pingen.sdk.exception.PingenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Handles the 3-step file upload process for creating letters and batches.
 *
 * Step 1: Request an upload URL from the API
 * Step 2: Upload the file to the received URL (no authentication)
 * Step 3: Create the resource (letter/batch) with the URL and signature
 */
public class FileUploader {

    private static final Logger log = LoggerFactory.getLogger(FileUploader.class);

    private final ApiClient apiClient;
    private final OAuth oauth;

    public FileUploader(ApiClient apiClient, OAuth oauth) {
        this.apiClient = apiClient;
        this.oauth = oauth;
    }

    /**
     * Uploads a file and returns the URL and signature needed to create a resource.
     * This performs steps 1 and 2 of the 3-step upload process.
     *
     * @param filePath the path to the file to upload
     * @return the upload response containing URL and signature
     * @throws PingenException if upload fails
     */
    public UploadResponse uploadFile(Path filePath) throws IOException {
        byte[] fileBytes = Files.readAllBytes(filePath);
        return uploadFile(fileBytes);
    }

    /**
     * Uploads file bytes and returns the URL and signature needed to create a resource.
     * This performs steps 1 and 2 of the 3-step upload process.
     *
     * @param fileBytes the file content as bytes
     * @return the upload response containing URL and signature
     * @throws PingenException if upload fails
     */
    public UploadResponse uploadFile(byte[] fileBytes) {
        // Step 1: Request upload URL
        log.debug("Requesting file upload URL");
        UploadResponse uploadResponse = requestUploadUrl();

        String uploadUrl = uploadResponse.getUrl();
        if (uploadUrl == null || uploadUrl.isEmpty()) {
            throw new PingenException("Failed to get upload URL from response");
        }

        // Step 2: Upload file to the URL (no authentication header)
        log.debug("Uploading file to {}", uploadUrl);
        uploadFileToUrl(uploadUrl, fileBytes);

        return uploadResponse;
    }

    /**
     * Step 1: Requests a file upload URL from the API.
     *
     * @return the upload response with URL and signature
     */
    private UploadResponse requestUploadUrl() {
        String url = apiClient.getConfig().getApiUrl() + "/file-upload";

        ApiRequest request = ApiRequest.builder()
                .method(HttpMethod.GET)
                .url(url)
                .header("Authorization", oauth.getValidToken().getAuthorizationHeader())
                .header("Accept", "application/vnd.api+json")
                .build();

        ApiResponse<UploadResponse> response = apiClient.execute(request, UploadResponse.class);
        return response.getBody();
    }

    /**
     * Step 2: Uploads the file to the provided URL.
     * Important: No authentication header should be included in this request.
     *
     * @param uploadUrl the URL to upload to
     * @param fileBytes the file content as bytes
     */
    private void uploadFileToUrl(String uploadUrl, byte[] fileBytes) {
        // Create request WITHOUT authentication header
        ApiRequest request = ApiRequest.builder()
                .method(HttpMethod.PUT)
                .url(uploadUrl)
                .binaryBody(fileBytes)
                .build();

        // Upload the file
        ApiResponse<String> response = apiClient.execute(request);

        if (!response.isSuccessful()) {
            throw new PingenException("Failed to upload file: HTTP " + response.getStatusCode());
        }

        log.debug("File uploaded successfully");
    }
}
