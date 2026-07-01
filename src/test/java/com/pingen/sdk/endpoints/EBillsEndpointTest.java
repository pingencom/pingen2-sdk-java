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
import com.pingen.sdk.models.ebill.EBill;
import com.pingen.sdk.models.ebill.EBillCreateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

@WireMockTest
class EBillsEndpointTest {

    private static final String ORG_ID = "org-uuid";
    private static final String EBILL_ID = "ebill-uuid";
    private static final String EVENT_ID = "event-uuid";

    private EBillsEndpoint endpoint;

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
        endpoint = new EBillsEndpoint(apiClient, new OAuth(apiClient, config), ORG_ID);
    }

    @Test
    void testGetCollection() {
        stubFor(get(urlPathEqualTo("/organisations/" + ORG_ID + "/deliveries/ebills"))
            .willReturn(okJson(collectionJson())));

        CollectionParams params = CollectionParams.builder().page(2, 10).build();
        PagedResponse<EBill> response = endpoint.getCollection(params);

        assertEquals(30, response.getTotal());
        assertEquals(2, response.getCurrentPage());
        assertEquals(10, response.getPageLimit());
        assertEquals(3, response.getLastPage());
        assertTrue(response.getItems().isEmpty());
        assertFalse(response.hasNext());

        verify(getRequestedFor(urlPathEqualTo("/organisations/" + ORG_ID + "/deliveries/ebills"))
            .withQueryParam("page[number]", equalTo("2"))
            .withQueryParam("page[limit]", equalTo("10")));
    }

    @Test
    void testGet() {
        stubFor(get(urlPathEqualTo("/organisations/" + ORG_ID + "/deliveries/ebills/" + EBILL_ID))
            .willReturn(okJson(ebillResourceJson("validating"))));

        Resource<EBill> response = endpoint.get(EBILL_ID).orElseThrow();

        assertEquals(EBILL_ID, response.getId());
        assertEquals("ebills", response.getType());
        assertEquals("validating", response.getAttributes().getStatus());
        assertEquals("invoice.pdf", response.getAttributes().getFileOriginalName());

        verify(getRequestedFor(urlPathEqualTo(
            "/organisations/" + ORG_ID + "/deliveries/ebills/" + EBILL_ID)));
    }

    @Test
    void testGetCollectionWithAllParams() {
        stubFor(get(urlPathEqualTo("/organisations/" + ORG_ID + "/deliveries/ebills"))
            .willReturn(okJson(collectionJson())));

        CollectionParams params = CollectionParams.builder()
            .page(2, 10)
            .sort("created_at")
            .search("test")
            .build();

        PagedResponse<EBill> response = endpoint.getCollection(params);

        assertEquals(30, response.getTotal());

        verify(getRequestedFor(urlPathEqualTo("/organisations/" + ORG_ID + "/deliveries/ebills"))
            .withQueryParam("page[number]", equalTo("2"))
            .withQueryParam("page[limit]", equalTo("10"))
            .withQueryParam("sort", equalTo("created_at"))
            .withQueryParam("q", equalTo("test")));
    }

    @Test
    void testGetCollectionRateLimit() {
        stubFor(get(urlPathEqualTo("/organisations/" + ORG_ID + "/deliveries/ebills"))
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
        stubFor(post(urlPathEqualTo("/organisations/" + ORG_ID + "/deliveries/ebills"))
            .willReturn(aResponse()
                .withStatus(201)
                .withHeader("Content-Type", "application/vnd.api+json")
                .withBody(ebillResourceJson("validating"))));

        EBillCreateRequest request = EBillCreateRequest.builder()
            .fileBytes(new byte[]{1, 2, 3})
            .fileOriginalName("invoice.pdf")
            .autoSend(false)
            .build();

        Resource<EBill> response = endpoint.create(request);

        assertEquals(EBILL_ID, response.getId());
        assertEquals("validating", response.getAttributes().getStatus());

        verify(getRequestedFor(urlPathEqualTo("/file-upload")));
        verify(putRequestedFor(urlPathEqualTo("/s3/upload")));
        verify(postRequestedFor(urlPathEqualTo("/organisations/" + ORG_ID + "/deliveries/ebills")));
    }

    @Test
    void testCreateReturnsValidationException(WireMockRuntimeInfo wm) {
        String uploadUrl = "http://localhost:" + wm.getHttpPort() + "/s3/upload";

        stubFor(get(urlPathEqualTo("/file-upload"))
            .willReturn(okJson(fileUploadJson(uploadUrl))));
        stubFor(put(urlPathEqualTo("/s3/upload"))
            .willReturn(aResponse().withStatus(200)));
        stubFor(post(urlPathEqualTo("/organisations/" + ORG_ID + "/deliveries/ebills"))
            .willReturn(aResponse()
                .withStatus(422)
                .withHeader("Content-Type", "application/vnd.api+json")
                .withBody(errorJson(422, "The given data was invalid."))));

        EBillCreateRequest request = EBillCreateRequest.builder()
            .fileBytes(new byte[]{1, 2, 3})
            .fileOriginalName("invoice.pdf")
            .autoSend(false)
            .build();

        ValidationException ex = assertThrows(ValidationException.class,
            () -> endpoint.create(request));

        assertEquals(422, ex.getStatusCode());
        verify(postRequestedFor(urlPathEqualTo("/organisations/" + ORG_ID + "/deliveries/ebills")));
    }

    @Test
    void testSend() {
        stubFor(patch(urlPathEqualTo(
            "/organisations/" + ORG_ID + "/deliveries/ebills/" + EBILL_ID + "/send"))
            .willReturn(okJson(ebillResourceJson("submitted"))));

        Resource<EBill> response = endpoint.send(EBILL_ID);

        assertEquals(EBILL_ID, response.getId());
        assertEquals("submitted", response.getAttributes().getStatus());

        verify(patchRequestedFor(urlPathEqualTo(
            "/organisations/" + ORG_ID + "/deliveries/ebills/" + EBILL_ID + "/send")));
    }

    @Test
    void testCancel() {
        stubFor(patch(urlPathEqualTo(
            "/organisations/" + ORG_ID + "/deliveries/ebills/" + EBILL_ID + "/cancel"))
            .willReturn(aResponse().withStatus(202)));

        assertDoesNotThrow(() -> endpoint.cancel(EBILL_ID));

        verify(patchRequestedFor(urlPathEqualTo(
            "/organisations/" + ORG_ID + "/deliveries/ebills/" + EBILL_ID + "/cancel")));
    }

    @Test
    void testDelete() {
        stubFor(delete(urlPathEqualTo(
            "/organisations/" + ORG_ID + "/deliveries/ebills/" + EBILL_ID))
            .willReturn(aResponse().withStatus(204)));

        assertDoesNotThrow(() -> endpoint.delete(EBILL_ID));

        verify(deleteRequestedFor(urlPathEqualTo(
            "/organisations/" + ORG_ID + "/deliveries/ebills/" + EBILL_ID)));
    }

    @Test
    void testGetFile() {
        String fileUrl = "https://storage.example.com/ebills/ebill-uuid.pdf";

        stubFor(get(urlPathEqualTo(
            "/organisations/" + ORG_ID + "/deliveries/ebills/" + EBILL_ID + "/file"))
            .willReturn(aResponse()
                .withStatus(302)
                .withHeader("Location", fileUrl)));

        String result = endpoint.getFile(EBILL_ID);

        assertEquals(fileUrl, result);
        verify(getRequestedFor(urlPathEqualTo(
            "/organisations/" + ORG_ID + "/deliveries/ebills/" + EBILL_ID + "/file")));
    }

    @Test
    void testGetEvents() {
        stubFor(get(urlPathEqualTo(
            "/organisations/" + ORG_ID + "/deliveries/ebills/" + EBILL_ID + "/events"))
            .willReturn(okJson(eventsCollectionJson())));

        PagedResponse<DeliveryEvent> response = endpoint.getEvents(EBILL_ID);

        assertEquals(1, response.getTotal());
        assertEquals(1, response.size());
        assertEquals("EBILL_CREATED", response.getItems().get(0).getAttributes().getCode());

        verify(getRequestedFor(urlPathEqualTo(
            "/organisations/" + ORG_ID + "/deliveries/ebills/" + EBILL_ID + "/events")));
    }

    @Test
    void testGetEventImage() {
        String imageUrl = "https://storage.example.com/images/event-uuid.jpg";

        stubFor(get(urlPathEqualTo(
            "/organisations/" + ORG_ID + "/deliveries/ebills/" + EBILL_ID + "/events/" + EVENT_ID + "/image"))
            .willReturn(aResponse()
                .withStatus(302)
                .withHeader("Location", imageUrl)));

        String result = endpoint.getEventImage(EBILL_ID, EVENT_ID);

        assertEquals(imageUrl, result);
        verify(getRequestedFor(urlPathEqualTo(
            "/organisations/" + ORG_ID + "/deliveries/ebills/" + EBILL_ID + "/events/" + EVENT_ID + "/image")));
    }

    @Test
    void testRateLimitException() {
        stubFor(get(urlPathEqualTo(
            "/organisations/" + ORG_ID + "/deliveries/ebills/" + EBILL_ID + "/file"))
            .willReturn(aResponse()
                .withStatus(429)
                .withHeader("Content-Type", "application/vnd.api+json")
                .withHeader("Retry-After", "60")
                .withBody(errorJson(429, "Too Many Requests"))));

        RateLimitException ex = assertThrows(RateLimitException.class,
            () -> endpoint.getFile(EBILL_ID));

        assertEquals(429, ex.getStatusCode());
        assertEquals(60, ex.getRetryAfter());
    }

    @Test
    void testApiException() {
        stubFor(get(urlPathEqualTo(
            "/organisations/" + ORG_ID + "/deliveries/ebills/" + EBILL_ID + "/file"))
            .willReturn(aResponse()
                .withStatus(403)
                .withHeader("Content-Type", "application/vnd.api+json")
                .withBody(errorJson(403, "Forbidden"))));

        ApiException ex = assertThrows(ApiException.class,
            () -> endpoint.getFile(EBILL_ID));

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

    private static String ebillResourceJson(String status) {
        return """
            {
                "data": {
                    "id": "%s",
                    "type": "ebills",
                    "attributes": {
                        "status": "%s",
                        "file_original_name": "invoice.pdf",
                        "file_pages": 1,
                        "recipient_identifier": "user@example.com",
                        "price_currency": "CHF",
                        "price_value": 0.50,
                        "created_at": "2020-11-19T09:42:48+01:00",
                        "updated_at": "2020-11-19T09:42:48+01:00"
                    }
                }
            }
            """.formatted(EBILL_ID, status);
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
                            "code": "EBILL_CREATED",
                            "name": "E-Bill Created",
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
