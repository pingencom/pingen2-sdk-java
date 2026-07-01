package com.pingen.sdk.endpoints;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.pingen.sdk.PingenConfig;
import com.pingen.sdk.auth.OAuth;
import com.pingen.sdk.client.ApiClient;
import com.pingen.sdk.exception.ApiException;
import com.pingen.sdk.exception.RateLimitException;
import com.pingen.sdk.exception.ValidationException;
import com.pingen.sdk.models.common.CollectionParams;
import com.pingen.sdk.models.common.DeliveryEvent;
import com.pingen.sdk.models.common.PagedResponse;
import com.pingen.sdk.models.common.Resource;
import com.pingen.sdk.models.email.Email;
import com.pingen.sdk.models.email.EmailCreateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

@WireMockTest
class EmailsEndpointTest {

    private static final String ORG_ID = "org-uuid";
    private static final String EMAIL_ID = "email-uuid";
    private static final String EVENT_ID = "event-uuid";

    private EmailsEndpoint endpoint;

    @BeforeEach
    void setUp(WireMockRuntimeInfo wm) {
        stubFor(post(urlPathEqualTo("/auth/access-tokens"))
            .willReturn(okJson("""
                {"access_token":"test-token","token_type":"Bearer","expires_in":3600}
                """)));

        String baseUrl = "http://localhost:" + wm.getHttpPort();
        PingenConfig config = PingenConfig.builder()
            .clientId("test-client-id")
            .clientSecret("test-client-secret")
            .apiUrl(baseUrl)
            .identityUrl(baseUrl)
            .build();

        ApiClient apiClient = new ApiClient(config);
        endpoint = new EmailsEndpoint(apiClient, new OAuth(apiClient, config), ORG_ID);
    }

    @Test
    void testGetCollection() {
        stubFor(get(urlPathEqualTo("/organisations/" + ORG_ID + "/deliveries/emails"))
            .willReturn(okJson(collectionJson())));

        CollectionParams params = CollectionParams.builder().page(2, 10).build();
        PagedResponse<Email> response = endpoint.getCollection(params);

        assertEquals(30, response.getTotal());
        assertEquals(2, response.getCurrentPage());
        assertEquals(10, response.getPageLimit());
        assertEquals(3, response.getLastPage());
        assertTrue(response.getItems().isEmpty());
        assertFalse(response.hasNext());

        verify(getRequestedFor(urlPathEqualTo("/organisations/" + ORG_ID + "/deliveries/emails"))
            .withQueryParam("page[number]", equalTo("2"))
            .withQueryParam("page[limit]", equalTo("10")));
    }

    @Test
    void testGet() {
        stubFor(get(urlPathEqualTo("/organisations/" + ORG_ID + "/deliveries/emails/" + EMAIL_ID))
            .willReturn(okJson(emailResourceJson("validating"))));

        Resource<Email> response = endpoint.get(EMAIL_ID).orElseThrow();

        assertEquals(EMAIL_ID, response.getId());
        assertEquals("emails", response.getType());
        assertEquals("validating", response.getAttributes().getStatus());
        assertEquals("document.pdf", response.getAttributes().getFileOriginalName());
        assertEquals("recipient@example.com", response.getAttributes().getRecipientIdentifier());

        verify(getRequestedFor(urlPathEqualTo(
            "/organisations/" + ORG_ID + "/deliveries/emails/" + EMAIL_ID)));
    }

    @Test
    void testGetCollectionWithAllParams() {
        stubFor(get(urlPathEqualTo("/organisations/" + ORG_ID + "/deliveries/emails"))
            .willReturn(okJson(collectionJson())));

        CollectionParams params = CollectionParams.builder()
            .page(2, 10)
            .sort("created_at")
            .search("test")
            .build();

        PagedResponse<Email> response = endpoint.getCollection(params);

        assertEquals(30, response.getTotal());

        verify(getRequestedFor(urlPathEqualTo("/organisations/" + ORG_ID + "/deliveries/emails"))
            .withQueryParam("page[number]", equalTo("2"))
            .withQueryParam("page[limit]", equalTo("10"))
            .withQueryParam("sort", equalTo("created_at"))
            .withQueryParam("q", equalTo("test")));
    }

    @Test
    void testGetCollectionRateLimit() {
        stubFor(get(urlPathEqualTo("/organisations/" + ORG_ID + "/deliveries/emails"))
            .willReturn(aResponse()
                .withStatus(429)
                .withHeader("Content-Type", "application/vnd.api+json")
                .withHeader("Retry-After", "30")
                .withBody(errorJson(429, "Too Many Requests"))));

        RateLimitException ex = assertThrows(RateLimitException.class,
            () -> endpoint.getCollection());

        assertEquals(429, ex.getStatusCode());
        assertEquals(30, ex.getRetryAfter());
    }

    @Test
    void testCreate(WireMockRuntimeInfo wm) {
        String uploadUrl = "http://localhost:" + wm.getHttpPort() + "/s3/upload";

        stubFor(get(urlPathEqualTo("/file-upload"))
            .willReturn(okJson(fileUploadJson(uploadUrl))));
        stubFor(put(urlPathEqualTo("/s3/upload"))
            .willReturn(aResponse().withStatus(200)));
        stubFor(post(urlPathEqualTo("/organisations/" + ORG_ID + "/deliveries/emails"))
            .willReturn(aResponse()
                .withStatus(201)
                .withHeader("Content-Type", "application/vnd.api+json")
                .withBody(emailResourceJson("validating"))));

        EmailCreateRequest request = EmailCreateRequest.builder()
            .fileBytes(new byte[]{1, 2, 3})
            .fileOriginalName("document.pdf")
            .autoSend(false)
            .build();

        Resource<Email> response = endpoint.create(request);

        assertEquals(EMAIL_ID, response.getId());
        assertEquals("validating", response.getAttributes().getStatus());

        verify(getRequestedFor(urlPathEqualTo("/file-upload")));
        verify(putRequestedFor(urlPathEqualTo("/s3/upload")));
        verify(postRequestedFor(urlPathEqualTo("/organisations/" + ORG_ID + "/deliveries/emails")));
    }

    @Test
    void testCreateReturnsValidationException(WireMockRuntimeInfo wm) {
        String uploadUrl = "http://localhost:" + wm.getHttpPort() + "/s3/upload";

        stubFor(get(urlPathEqualTo("/file-upload"))
            .willReturn(okJson(fileUploadJson(uploadUrl))));
        stubFor(put(urlPathEqualTo("/s3/upload"))
            .willReturn(aResponse().withStatus(200)));
        stubFor(post(urlPathEqualTo("/organisations/" + ORG_ID + "/deliveries/emails"))
            .willReturn(aResponse()
                .withStatus(422)
                .withHeader("Content-Type", "application/vnd.api+json")
                .withBody(errorJson(422, "The given data was invalid."))));

        EmailCreateRequest request = EmailCreateRequest.builder()
            .fileBytes(new byte[]{1, 2, 3})
            .fileOriginalName("document.pdf")
            .autoSend(false)
            .build();

        ValidationException ex = assertThrows(ValidationException.class,
            () -> endpoint.create(request));

        assertEquals(422, ex.getStatusCode());
        verify(postRequestedFor(urlPathEqualTo("/organisations/" + ORG_ID + "/deliveries/emails")));
    }

    @Test
    void testCancel() {
        stubFor(patch(urlPathEqualTo(
            "/organisations/" + ORG_ID + "/deliveries/emails/" + EMAIL_ID + "/cancel"))
            .willReturn(aResponse().withStatus(202)));

        assertDoesNotThrow(() -> endpoint.cancel(EMAIL_ID));

        verify(patchRequestedFor(urlPathEqualTo(
            "/organisations/" + ORG_ID + "/deliveries/emails/" + EMAIL_ID + "/cancel")));
    }

    @Test
    void testDelete() {
        stubFor(delete(urlPathEqualTo(
            "/organisations/" + ORG_ID + "/deliveries/emails/" + EMAIL_ID))
            .willReturn(aResponse().withStatus(204)));

        assertDoesNotThrow(() -> endpoint.delete(EMAIL_ID));

        verify(deleteRequestedFor(urlPathEqualTo(
            "/organisations/" + ORG_ID + "/deliveries/emails/" + EMAIL_ID)));
    }

    @Test
    void testGetFile() {
        String fileUrl = "https://storage.example.com/emails/email-uuid.pdf";

        stubFor(get(urlPathEqualTo(
            "/organisations/" + ORG_ID + "/deliveries/emails/" + EMAIL_ID + "/file"))
            .willReturn(aResponse()
                .withStatus(302)
                .withHeader("Location", fileUrl)));

        String result = endpoint.getFile(EMAIL_ID);

        assertEquals(fileUrl, result);
        verify(getRequestedFor(urlPathEqualTo(
            "/organisations/" + ORG_ID + "/deliveries/emails/" + EMAIL_ID + "/file")));
    }

    @Test
    void testGetEvents() {
        stubFor(get(urlPathEqualTo(
            "/organisations/" + ORG_ID + "/deliveries/emails/" + EMAIL_ID + "/events"))
            .willReturn(okJson(eventsCollectionJson())));

        PagedResponse<DeliveryEvent> response = endpoint.getEvents(EMAIL_ID);

        assertEquals(1, response.getTotal());
        assertEquals(1, response.size());
        assertEquals("EMAIL_CREATED", response.getItems().get(0).getAttributes().getCode());

        verify(getRequestedFor(urlPathEqualTo(
            "/organisations/" + ORG_ID + "/deliveries/emails/" + EMAIL_ID + "/events")));
    }

    @Test
    void testGetEventImage() {
        String imageUrl = "https://storage.example.com/images/event-uuid.jpg";

        stubFor(get(urlPathEqualTo(
            "/organisations/" + ORG_ID + "/deliveries/emails/" + EMAIL_ID + "/events/" + EVENT_ID + "/image"))
            .willReturn(aResponse()
                .withStatus(302)
                .withHeader("Location", imageUrl)));

        String result = endpoint.getEventImage(EMAIL_ID, EVENT_ID);

        assertEquals(imageUrl, result);
        verify(getRequestedFor(urlPathEqualTo(
            "/organisations/" + ORG_ID + "/deliveries/emails/" + EMAIL_ID + "/events/" + EVENT_ID + "/image")));
    }

    @Test
    void testRateLimitException() {
        stubFor(get(urlPathEqualTo(
            "/organisations/" + ORG_ID + "/deliveries/emails/" + EMAIL_ID + "/file"))
            .willReturn(aResponse()
                .withStatus(429)
                .withHeader("Content-Type", "application/vnd.api+json")
                .withHeader("Retry-After", "60")
                .withBody(errorJson(429, "Too Many Requests"))));

        RateLimitException ex = assertThrows(RateLimitException.class,
            () -> endpoint.getFile(EMAIL_ID));

        assertEquals(429, ex.getStatusCode());
        assertEquals(60, ex.getRetryAfter());
    }

    @Test
    void testApiException() {
        stubFor(get(urlPathEqualTo(
            "/organisations/" + ORG_ID + "/deliveries/emails/" + EMAIL_ID + "/file"))
            .willReturn(aResponse()
                .withStatus(403)
                .withHeader("Content-Type", "application/vnd.api+json")
                .withBody(errorJson(403, "Forbidden"))));

        ApiException ex = assertThrows(ApiException.class,
            () -> endpoint.getFile(EMAIL_ID));

        assertEquals(403, ex.getStatusCode());
        assertNotNull(ex.getResponseBody());
    }

    // -----------------------------------------------------------------------
    // JSON helpers
    // -----------------------------------------------------------------------

    private static String collectionJson() {
        return """
            {
                "data": [],
                "links": {
                    "first": "string",
                    "last": "string",
                    "prev": null,
                    "next": null,
                    "self": "string"
                },
                "meta": {
                    "current_page": 2,
                    "last_page": 3,
                    "per_page": 10,
                    "from": 10,
                    "to": 19,
                    "total": 30
                }
            }
            """;
    }

    private static String emailResourceJson(String status) {
        return """
            {
                "data": {
                    "id": "%s",
                    "type": "emails",
                    "attributes": {
                        "status": "%s",
                        "file_original_name": "document.pdf",
                        "file_pages": 1,
                        "recipient_identifier": "recipient@example.com",
                        "price_currency": "CHF",
                        "price_value": 0.30,
                        "created_at": "2020-11-19T09:42:48+01:00",
                        "updated_at": "2020-11-19T09:42:48+01:00"
                    }
                }
            }
            """.formatted(EMAIL_ID, status);
    }

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

    private static String eventsCollectionJson() {
        return """
            {
                "data": [
                    {
                        "id": "%s",
                        "type": "delivery_events",
                        "attributes": {
                            "code": "EMAIL_CREATED",
                            "name": "Email Created",
                            "producer": "API",
                            "has_image": false,
                            "emitted_at": "2020-11-19T09:42:48+01:00",
                            "created_at": "2020-11-19T09:42:48+01:00",
                            "updated_at": "2020-11-19T09:42:48+01:00"
                        }
                    }
                ],
                "links": {
                    "first": null,
                    "last": null,
                    "prev": null,
                    "next": null,
                    "self": "string"
                },
                "meta": {
                    "current_page": 1,
                    "last_page": 1,
                    "per_page": 20,
                    "from": 1,
                    "to": 1,
                    "total": 1
                }
            }
            """.formatted(EVENT_ID);
    }

    private static String errorJson(int code, String title) {
        return """
            {
                "errors": [
                    {
                        "code": "%d",
                        "title": "%s",
                        "source": {}
                    }
                ]
            }
            """.formatted(code, title);
    }
}
