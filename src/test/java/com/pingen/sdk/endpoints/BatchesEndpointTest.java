package com.pingen.sdk.endpoints;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.pingen.sdk.PingenConfig;
import com.pingen.sdk.auth.OAuth;
import com.pingen.sdk.client.ApiClient;
import com.pingen.sdk.exception.ApiException;
import com.pingen.sdk.exception.AuthenticationException;
import com.pingen.sdk.exception.RateLimitException;
import com.pingen.sdk.exception.ValidationException;
import com.pingen.sdk.models.batch.*;
import com.pingen.sdk.models.common.CollectionParams;
import com.pingen.sdk.models.common.Filter;
import com.pingen.sdk.models.common.PagedResponse;
import com.pingen.sdk.models.common.Resource;
import com.pingen.sdk.models.letter.AddressPosition;
import com.pingen.sdk.models.letter.DeliveryProduct;
import com.pingen.sdk.models.letter.PrintMode;
import com.pingen.sdk.models.letter.PrintSpectrum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

@WireMockTest
class BatchesEndpointTest {

    private static final String ORG_ID = "org-uuid";
    private static final String BATCH_ID = "batch-uuid";

    private BatchesEndpoint endpoint;

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
        endpoint = new BatchesEndpoint(apiClient, new OAuth(apiClient, config), ORG_ID);
    }

    @Test
    void testGetCollection() {
        stubFor(get(urlPathEqualTo("/organisations/" + ORG_ID + "/batches"))
            .willReturn(okJson(collectionJson())));

        CollectionParams params = CollectionParams.builder().page(2, 10).build();
        PagedResponse<Batch> response = endpoint.getCollection(params);

        assertEquals(30, response.getTotal());
        assertEquals(2, response.getCurrentPage());
        assertEquals(10, response.getPageLimit());
        assertEquals(3, response.getLastPage());
        assertTrue(response.getItems().isEmpty());
        assertFalse(response.hasNext());

        verify(getRequestedFor(urlPathEqualTo("/organisations/" + ORG_ID + "/batches"))
            .withQueryParam("page[number]", equalTo("2"))
            .withQueryParam("page[limit]", equalTo("10")));
    }

    @Test
    void testGet() {
        stubFor(get(urlPathEqualTo("/organisations/" + ORG_ID + "/batches/" + BATCH_ID))
            .willReturn(okJson(batchResourceJson("validating"))));

        Resource<Batch> response = endpoint.get(BATCH_ID).orElseThrow();

        assertEquals(BATCH_ID, response.getId());
        assertEquals("batches", response.getType());
        assertEquals("validating", response.getAttributes().getStatus());
        assertEquals("Test Batch", response.getAttributes().getName());
        assertEquals("lorem.zip", response.getAttributes().getFileOriginalName());

        verify(getRequestedFor(urlPathEqualTo("/organisations/" + ORG_ID + "/batches/" + BATCH_ID)));
    }

    @Test
    void testGetCollectionWithAllParams() {
        stubFor(get(urlPathEqualTo("/organisations/" + ORG_ID + "/batches"))
            .willReturn(okJson(collectionJson())));

        CollectionParams params = CollectionParams.builder()
            .page(2, 10)
            .sort("created_at")
            .filter(Filter.eq("name", "testBatch"))
            .search("test")
            .build();

        PagedResponse<Batch> response = endpoint.getCollection(params);

        assertEquals(30, response.getTotal());

        verify(getRequestedFor(urlPathEqualTo("/organisations/" + ORG_ID + "/batches"))
            .withQueryParam("page[number]", equalTo("2"))
            .withQueryParam("page[limit]", equalTo("10"))
            .withQueryParam("sort", equalTo("created_at"))
            .withQueryParam("q", equalTo("test")));
    }

    @Test
    void testGetCollectionRateLimit() {
        stubFor(get(urlPathEqualTo("/organisations/" + ORG_ID + "/batches"))
            .willReturn(aResponse()
                .withStatus(429)
                .withHeader("Content-Type", "application/vnd.api+json")
                .withHeader("Retry-After", "30")
                .withBody(errorJson(429, "Too Many Requests"))));

        RateLimitException ex = assertThrows(RateLimitException.class,
            () -> endpoint.getCollection());

        assertEquals(429, ex.getStatusCode());
        assertEquals(30, ex.getRetryAfter());

        verify(getRequestedFor(urlPathEqualTo("/organisations/" + ORG_ID + "/batches")));
    }

    @Test
    void testCreate(WireMockRuntimeInfo wm) {
        String uploadUrl = "http://localhost:" + wm.getHttpPort() + "/s3/upload";

        stubFor(get(urlPathEqualTo("/file-upload"))
            .willReturn(okJson(fileUploadJson(uploadUrl))));
        stubFor(put(urlPathEqualTo("/s3/upload"))
            .willReturn(aResponse().withStatus(200)));
        stubFor(post(urlPathEqualTo("/organisations/" + ORG_ID + "/batches"))
            .willReturn(aResponse()
                .withStatus(201)
                .withHeader("Content-Type", "application/vnd.api+json")
                .withBody(batchResourceJson("validating"))));

        BatchCreateRequest request = BatchCreateRequest.builder()
            .fileBytes(new byte[]{1, 2, 3})
            .fileOriginalName("lorem.zip")
            .name("Test Batch")
            .addressPosition(AddressPosition.LEFT)
            .groupingType(GroupingType.ZIP)
            .groupingOptionsSplitType(BatchGroupingSplitType.PAGE)
            .groupingOptionsSplitPosition(BatchGroupingSplitPosition.LAST_PAGE)
            .groupingOptionsSplitSize(1)
            .icon(BatchIcon.DOCUMENT)
            .build();

        Resource<Batch> response = endpoint.create(request);

        assertEquals(BATCH_ID, response.getId());
        assertEquals("validating", response.getAttributes().getStatus());

        verify(getRequestedFor(urlPathEqualTo("/file-upload")));
        verify(putRequestedFor(urlPathEqualTo("/s3/upload")));
        verify(postRequestedFor(urlPathEqualTo("/organisations/" + ORG_ID + "/batches")));
    }

    @Test
    void testCreateReturnsValidationException(WireMockRuntimeInfo wm) {
        String uploadUrl = "http://localhost:" + wm.getHttpPort() + "/s3/upload";

        stubFor(get(urlPathEqualTo("/file-upload"))
            .willReturn(okJson(fileUploadJson(uploadUrl))));
        stubFor(put(urlPathEqualTo("/s3/upload"))
            .willReturn(aResponse().withStatus(200)));
        stubFor(post(urlPathEqualTo("/organisations/" + ORG_ID + "/batches"))
            .willReturn(aResponse()
                .withStatus(422)
                .withHeader("Content-Type", "application/vnd.api+json")
                .withBody(errorJson(422, "The given data was invalid."))));

        BatchCreateRequest request = BatchCreateRequest.builder()
            .fileBytes(new byte[]{1, 2, 3})
            .fileOriginalName("lorem.zip")
            .name("Test Batch")
            .addressPosition(AddressPosition.LEFT)
            .groupingType(GroupingType.ZIP)
            .groupingOptionsSplitType(BatchGroupingSplitType.PAGE)
            .groupingOptionsSplitPosition(BatchGroupingSplitPosition.LAST_PAGE)
            .groupingOptionsSplitSize(1)
            .icon(BatchIcon.DOCUMENT)
            .build();

        ValidationException ex = assertThrows(ValidationException.class,
            () -> endpoint.create(request));

        assertEquals(422, ex.getStatusCode());
        verify(postRequestedFor(urlPathEqualTo("/organisations/" + ORG_ID + "/batches")));
    }

    @Test
    void testCreateUnauthorized(WireMockRuntimeInfo wm) {
        stubFor(post(urlPathEqualTo("/auth/access-tokens"))
            .atPriority(1)
            .willReturn(aResponse()
                .withStatus(401)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    {"error":"invalid_client","error_description":"Unauthorized"}
                    """)));

        String baseUrl = "http://localhost:" + wm.getHttpPort();
        PingenConfig config = PingenConfig.builder()
            .clientId("bad-id")
            .clientSecret("bad-secret")
            .apiUrl(baseUrl)
            .identityUrl(baseUrl)
            .build();
        ApiClient apiClient = new ApiClient(config);
        BatchesEndpoint unauthEndpoint =
            new BatchesEndpoint(apiClient, new OAuth(apiClient, config), ORG_ID);

        BatchCreateRequest request = BatchCreateRequest.builder()
            .fileBytes(new byte[]{1, 2, 3})
            .fileOriginalName("lorem.zip")
            .name("Test Batch")
            .addressPosition(AddressPosition.LEFT)
            .groupingType(GroupingType.ZIP)
            .groupingOptionsSplitType(BatchGroupingSplitType.PAGE)
            .groupingOptionsSplitPosition(BatchGroupingSplitPosition.LAST_PAGE)
            .groupingOptionsSplitSize(1)
            .icon(BatchIcon.DOCUMENT)
            .build();

        assertThrows(AuthenticationException.class, () -> unauthEndpoint.create(request));
    }

    @Test
    void testUpdate() {
        stubFor(patch(urlPathEqualTo("/organisations/" + ORG_ID + "/batches/" + BATCH_ID))
            .willReturn(aResponse().withStatus(200)));

        BatchUpdateRequest updateRequest = BatchUpdateRequest.builder()
            .name("Updated Batch Name")
            .build();

        assertDoesNotThrow(() -> endpoint.update(BATCH_ID, updateRequest));

        verify(patchRequestedFor(urlPathEqualTo("/organisations/" + ORG_ID + "/batches/" + BATCH_ID)));
    }

    @Test
    void testSend() {
        stubFor(patch(urlPathEqualTo("/organisations/" + ORG_ID + "/batches/" + BATCH_ID + "/send"))
            .willReturn(okJson(batchResourceJson("submitted"))));

        BatchSendRequest sendRequest = BatchSendRequest.builder()
            .addDeliveryProduct("CH", DeliveryProduct.FAST)
            .printMode(PrintMode.SIMPLEX)
            .printSpectrum(PrintSpectrum.COLOR)
            .build();

        Resource<Batch> response = endpoint.send(BATCH_ID, sendRequest);

        assertEquals(BATCH_ID, response.getId());
        assertEquals("submitted", response.getAttributes().getStatus());

        verify(patchRequestedFor(urlPathEqualTo(
            "/organisations/" + ORG_ID + "/batches/" + BATCH_ID + "/send")));
    }

    @Test
    void testCancel() {
        stubFor(patch(urlPathEqualTo("/organisations/" + ORG_ID + "/batches/" + BATCH_ID + "/cancel"))
            .willReturn(aResponse().withStatus(202)));

        assertDoesNotThrow(() -> endpoint.cancel(BATCH_ID));

        verify(patchRequestedFor(urlPathEqualTo(
            "/organisations/" + ORG_ID + "/batches/" + BATCH_ID + "/cancel")));
    }

    @Test
    void testDeleteWithoutLetters() {
        stubFor(delete(urlPathEqualTo("/organisations/" + ORG_ID + "/batches/" + BATCH_ID))
            .willReturn(aResponse().withStatus(204)));

        assertDoesNotThrow(() -> endpoint.deleteWithoutLetters(BATCH_ID));

        verify(deleteRequestedFor(urlPathEqualTo("/organisations/" + ORG_ID + "/batches/" + BATCH_ID)));
    }

    @Test
    void testDeleteWithLetters() {
        stubFor(delete(urlPathEqualTo("/organisations/" + ORG_ID + "/batches/" + BATCH_ID))
            .willReturn(aResponse().withStatus(204)));

        assertDoesNotThrow(() -> endpoint.deleteWithLetters(BATCH_ID));

        verify(deleteRequestedFor(urlPathEqualTo("/organisations/" + ORG_ID + "/batches/" + BATCH_ID)));
    }

    @Test
    void testGetEvents() {
        stubFor(get(urlPathEqualTo("/organisations/" + ORG_ID + "/batches/" + BATCH_ID + "/events"))
            .willReturn(okJson(batchEventsCollectionJson())));

        PagedResponse<BatchEvent> response = endpoint.getEvents(BATCH_ID);

        assertEquals(1, response.getTotal());
        assertEquals(1, response.size());
        assertEquals("BATCH_CREATED", response.getItems().get(0).getAttributes().getCode());

        verify(getRequestedFor(urlPathEqualTo(
            "/organisations/" + ORG_ID + "/batches/" + BATCH_ID + "/events")));
    }

    @Test
    void testGetStatistics() {
        stubFor(get(urlPathEqualTo("/organisations/" + ORG_ID + "/batches/" + BATCH_ID + "/statistics"))
            .willReturn(okJson(batchStatisticsJson())));

        Resource<BatchStatistics> response = endpoint.getStatistics(BATCH_ID);

        assertEquals(5, response.getAttributes().getLetterValidating());

        verify(getRequestedFor(urlPathEqualTo(
            "/organisations/" + ORG_ID + "/batches/" + BATCH_ID + "/statistics")));
    }

    @Test
    void testApiException() {
        stubFor(get(urlPathEqualTo("/organisations/" + ORG_ID + "/batches"))
            .willReturn(aResponse()
                .withStatus(403)
                .withHeader("Content-Type", "application/vnd.api+json")
                .withBody(errorJson(403, "Forbidden"))));

        ApiException ex = assertThrows(ApiException.class,
            () -> endpoint.getCollection());

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

    private static String batchResourceJson(String status) {
        return """
            {
                "data": {
                    "id": "%s",
                    "type": "batches",
                    "attributes": {
                        "status": "%s",
                        "name": "Test Batch",
                        "file_original_name": "lorem.zip",
                        "address_position": "left",
                        "letter_count": 10,
                        "price_currency": "CHF",
                        "price_value": 12.50,
                        "created_at": "2020-11-19T09:42:48+01:00",
                        "updated_at": "2020-11-19T09:42:48+01:00"
                    }
                }
            }
            """.formatted(BATCH_ID, status);
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

    private static String batchEventsCollectionJson() {
        return """
            {
                "data": [
                    {
                        "id": "event-uuid",
                        "type": "batch_events",
                        "attributes": {
                            "code": "BATCH_CREATED",
                            "name": "Batch Created",
                            "producer": "API",
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
            """;
    }

    private static String batchStatisticsJson() {
        return """
            {
                "data": {
                    "id": "%s",
                    "type": "batch_statistics",
                    "attributes": {
                        "letter_validating": 5,
                        "letter_countries": [],
                        "letter_groups": [],
                        "letter_regions": []
                    }
                }
            }
            """.formatted(BATCH_ID);
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
