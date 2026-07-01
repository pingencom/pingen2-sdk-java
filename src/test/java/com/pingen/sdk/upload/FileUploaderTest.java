package com.pingen.sdk.upload;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.pingen.sdk.PingenConfig;
import com.pingen.sdk.auth.OAuth;
import com.pingen.sdk.client.ApiClient;
import com.pingen.sdk.exception.ApiException;
import com.pingen.sdk.exception.AuthenticationException;
import com.pingen.sdk.exception.PingenException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

@WireMockTest
class FileUploaderTest {

    private FileUploader uploader;
    private String baseUrl;

    @BeforeEach
    void setUp(WireMockRuntimeInfo wm) {
        stubFor(post(urlPathEqualTo("/auth/access-tokens"))
            .willReturn(okJson("""
                {"access_token":"test-token","token_type":"Bearer","expires_in":3600}
                """)));

        baseUrl = "http://localhost:" + wm.getHttpPort();
        PingenConfig config = PingenConfig.builder()
            .clientId("test-client-id")
            .clientSecret("test-client-secret")
            .apiUrl(baseUrl)
            .identityUrl(baseUrl)
            .build();

        ApiClient apiClient = new ApiClient(config);
        uploader = new FileUploader(apiClient, new OAuth(apiClient, config));
    }

    @Test
    void testUploadFileBytes(WireMockRuntimeInfo wm) {
        String uploadUrl = "http://localhost:" + wm.getHttpPort() + "/s3/upload";

        stubFor(get(urlPathEqualTo("/file-upload"))
            .willReturn(okJson(fileUploadJson(uploadUrl))));
        stubFor(put(urlPathEqualTo("/s3/upload"))
            .willReturn(aResponse().withStatus(200)));

        UploadResponse response = uploader.uploadFile(new byte[]{1, 2, 3});

        assertEquals(uploadUrl, response.getUrl());
        assertEquals("test-signature-123", response.getUrlSignature());

        verify(getRequestedFor(urlPathEqualTo("/file-upload")));
        verify(putRequestedFor(urlPathEqualTo("/s3/upload")));
    }

    @Test
    void testUploadFilePath(WireMockRuntimeInfo wm, @TempDir Path tempDir) throws IOException {
        String uploadUrl = "http://localhost:" + wm.getHttpPort() + "/s3/upload";

        stubFor(get(urlPathEqualTo("/file-upload"))
            .willReturn(okJson(fileUploadJson(uploadUrl))));
        stubFor(put(urlPathEqualTo("/s3/upload"))
            .willReturn(aResponse().withStatus(200)));

        Path testFile = tempDir.resolve("test.pdf");
        Files.write(testFile, new byte[]{1, 2, 3, 4, 5});

        UploadResponse response = uploader.uploadFile(testFile);

        assertEquals(uploadUrl, response.getUrl());
        assertEquals("test-signature-123", response.getUrlSignature());

        verify(getRequestedFor(urlPathEqualTo("/file-upload")));
        verify(putRequestedFor(urlPathEqualTo("/s3/upload")));
    }

    @Test
    void testUploadUrlAndSignatureAreReturned(WireMockRuntimeInfo wm) {
        String uploadUrl = "http://localhost:" + wm.getHttpPort() + "/s3/upload";
        String signature = "unique-signature-abc-xyz";

        stubFor(get(urlPathEqualTo("/file-upload"))
            .willReturn(okJson("""
                {
                    "data": {
                        "type": "file_uploads",
                        "attributes": {
                            "url": "%s",
                            "url_signature": "%s"
                        }
                    }
                }
                """.formatted(uploadUrl, signature))));
        stubFor(put(urlPathEqualTo("/s3/upload"))
            .willReturn(aResponse().withStatus(200)));

        UploadResponse response = uploader.uploadFile(new byte[]{99});

        assertEquals(uploadUrl, response.getUrl());
        assertEquals(signature, response.getUrlSignature());
    }

    @Test
    void testUploadFailsWhenS3Returns4xx(WireMockRuntimeInfo wm) {
        String uploadUrl = "http://localhost:" + wm.getHttpPort() + "/s3/upload";

        stubFor(get(urlPathEqualTo("/file-upload"))
            .willReturn(okJson(fileUploadJson(uploadUrl))));
        stubFor(put(urlPathEqualTo("/s3/upload"))
            .willReturn(aResponse().withStatus(403).withBody("Access Denied")));

        PingenException ex = assertThrows(PingenException.class,
            () -> uploader.uploadFile(new byte[]{1, 2, 3}));

        assertNotNull(ex.getMessage());
        verify(putRequestedFor(urlPathEqualTo("/s3/upload")));
    }

    @Test
    void testUploadFailsWhenFileUploadEndpointReturnsError() {
        stubFor(get(urlPathEqualTo("/file-upload"))
            .willReturn(aResponse()
                .withStatus(403)
                .withHeader("Content-Type", "application/vnd.api+json")
                .withBody("""
                    {
                        "errors": [{"code": "403", "title": "Forbidden", "source": {}}]
                    }
                    """)));

        assertThrows(ApiException.class, () -> uploader.uploadFile(new byte[]{1, 2, 3}));

        verify(getRequestedFor(urlPathEqualTo("/file-upload")));
    }

    @Test
    void testUploadFailsOnAuthenticationError(WireMockRuntimeInfo wm) {
        stubFor(post(urlPathEqualTo("/auth/access-tokens"))
            .atPriority(1)
            .willReturn(aResponse()
                .withStatus(401)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    {"error":"invalid_client","error_description":"Unauthorized"}
                    """)));

        String localBaseUrl = "http://localhost:" + wm.getHttpPort();
        PingenConfig config = PingenConfig.builder()
            .clientId("bad-id")
            .clientSecret("bad-secret")
            .apiUrl(localBaseUrl)
            .identityUrl(localBaseUrl)
            .build();
        ApiClient apiClient = new ApiClient(config);
        FileUploader unauthUploader = new FileUploader(apiClient, new OAuth(apiClient, config));

        assertThrows(AuthenticationException.class,
            () -> unauthUploader.uploadFile(new byte[]{1, 2, 3}));
    }

    // -----------------------------------------------------------------------
    // JSON helpers
    // -----------------------------------------------------------------------

    private static String fileUploadJson(String uploadUrl) {
        return """
            {
                "data": {
                    "type": "file_uploads",
                    "attributes": {
                        "url": "%s",
                        "url_signature": "test-signature-123"
                    }
                }
            }
            """.formatted(uploadUrl);
    }
}
