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
import com.pingen.sdk.models.common.CollectionParams;
import com.pingen.sdk.models.common.DeliveryEvent;
import com.pingen.sdk.models.common.Filter;
import com.pingen.sdk.models.common.PagedResponse;
import com.pingen.sdk.models.common.Resource;
import com.pingen.sdk.models.letter.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link LettersEndpoint} using WireMock to intercept HTTP calls at the
 * transport layer — the same approach as the PHP SDK's HTTP client faking.
 */
@WireMockTest
class LettersEndpointTest {

    private static final String ORG_ID = "org-uuid";
    private static final String LETTER_ID = "letter-uuid";
    private static final String EVENT_ID = "event-uuid";

    private LettersEndpoint endpoint;

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
        endpoint = new LettersEndpoint(apiClient, new OAuth(apiClient, config), ORG_ID);
    }

    // -----------------------------------------------------------------------
    // getCollection — testGetLetterCollection equivalent
    // -----------------------------------------------------------------------

    @Test
    void testGetCollection() {
        stubFor(get(urlPathEqualTo("/organisations/" + ORG_ID + "/deliveries/letters"))
            .willReturn(okJson(collectionJson())));

        CollectionParams params = CollectionParams.builder().page(2, 10).build();
        PagedResponse<Letter> response = endpoint.getCollection(params);

        assertEquals(30, response.getTotal());
        assertEquals(2, response.getCurrentPage());
        assertEquals(10, response.getPageLimit());
        assertEquals(3, response.getLastPage());
        assertTrue(response.getItems().isEmpty());
        assertFalse(response.hasNext());

        verify(getRequestedFor(urlPathEqualTo("/organisations/" + ORG_ID + "/deliveries/letters"))
            .withQueryParam("page[number]", equalTo("2"))
            .withQueryParam("page[limit]", equalTo("10")));
    }

    // -----------------------------------------------------------------------
    // get — testGetDetails equivalent
    // -----------------------------------------------------------------------

    @Test
    void testGet() {
        stubFor(get(urlPathEqualTo("/organisations/" + ORG_ID + "/deliveries/letters/" + LETTER_ID))
            .willReturn(okJson(letterResourceJson("validating"))));

        Resource<Letter> response = endpoint.get(LETTER_ID).orElseThrow();

        assertEquals(LETTER_ID, response.getId());
        assertEquals("letters", response.getType());
        assertEquals("validating", response.getAttributes().getStatus());
        assertEquals("lorem.pdf", response.getAttributes().getFileOriginalName());
        assertEquals(AddressPosition.LEFT, response.getAttributes().getAddressPosition());
        assertEquals(2, response.getAttributes().getFilePages());

        verify(getRequestedFor(urlPathEqualTo(
            "/organisations/" + ORG_ID + "/deliveries/letters/" + LETTER_ID)));
    }

    // -----------------------------------------------------------------------
    // getCollection with all params — testIterateOverCollection equivalent
    // -----------------------------------------------------------------------

    @Test
    void testGetCollectionWithAllParams() {
        stubFor(get(urlPathEqualTo("/organisations/" + ORG_ID + "/deliveries/letters"))
            .willReturn(okJson(collectionJson())));

        CollectionParams params = CollectionParams.builder()
            .page(2, 10)
            .sort("created_at")
            .filter(Filter.eq("name", "testName"))
            .search("test")
            .build();

        PagedResponse<Letter> response = endpoint.getCollection(params);

        assertEquals(30, response.getTotal());

        verify(getRequestedFor(urlPathEqualTo("/organisations/" + ORG_ID + "/deliveries/letters"))
            .withQueryParam("page[number]", equalTo("2"))
            .withQueryParam("page[limit]", equalTo("10"))
            .withQueryParam("sort", equalTo("created_at"))
            .withQueryParam("q", equalTo("test")));
    }

    // -----------------------------------------------------------------------
    // 429 on getCollection — testIterateOverCollectionRateLimit equivalent
    // -----------------------------------------------------------------------

    @Test
    void testGetCollectionRateLimit() {
        stubFor(get(urlPathEqualTo("/organisations/" + ORG_ID + "/deliveries/letters"))
            .willReturn(aResponse()
                .withStatus(429)
                .withHeader("Content-Type", "application/vnd.api+json")
                .withHeader("Retry-After", "30")
                .withBody(errorJson(429, "Too Many Requests"))));

        RateLimitException ex = assertThrows(RateLimitException.class,
            () -> endpoint.getCollection());

        assertEquals(429, ex.getStatusCode());
        assertEquals(30, ex.getRetryAfter());

        verify(getRequestedFor(urlPathEqualTo("/organisations/" + ORG_ID + "/deliveries/letters")));
    }

    // -----------------------------------------------------------------------
    // create with 3-step upload — testCreate / testCreateAndUpload equivalent
    // -----------------------------------------------------------------------

    @Test
    void testCreate(WireMockRuntimeInfo wm) {
        String uploadUrl = "http://localhost:" + wm.getHttpPort() + "/s3/upload";

        stubFor(get(urlPathEqualTo("/file-upload"))
            .willReturn(okJson(fileUploadJson(uploadUrl))));
        stubFor(put(urlPathEqualTo("/s3/upload"))
            .willReturn(aResponse().withStatus(200)));
        stubFor(post(urlPathEqualTo("/organisations/" + ORG_ID + "/deliveries/letters"))
            .willReturn(aResponse()
                .withStatus(201)
                .withHeader("Content-Type", "application/vnd.api+json")
                .withBody(letterResourceJson("validating"))));

        LetterCreateRequest request = LetterCreateRequest.builder()
            .fileBytes(new byte[]{1, 2, 3})
            .fileOriginalName("lorem.pdf")
            .addressPosition(AddressPosition.LEFT)
            .autoSend(false)
            .metaData(LetterMetaData.builder()
                .recipient(LetterMetaData.AddressEntry.builder()
                    .name("R_Example").street("R_Street").number("R_12")
                    .zip("R_12").city("R_Warsaw").country("PL").build())
                .sender(LetterMetaData.AddressEntry.builder()
                    .name("S_Example").street("S_Street").number("S_12")
                    .zip("S_12").city("S_Warsaw").country("PL").build())
                .build())
            .build();

        Resource<Letter> response = endpoint.create(request);

        assertEquals(LETTER_ID, response.getId());
        assertEquals("validating", response.getAttributes().getStatus());

        verify(getRequestedFor(urlPathEqualTo("/file-upload")));
        verify(putRequestedFor(urlPathEqualTo("/s3/upload")));
        verify(postRequestedFor(urlPathEqualTo(
            "/organisations/" + ORG_ID + "/deliveries/letters")));
    }

    // -----------------------------------------------------------------------
    // 422 on create — testCreateValidation equivalent
    // -----------------------------------------------------------------------

    @Test
    void testCreateReturnsValidationException(WireMockRuntimeInfo wm) {
        String uploadUrl = "http://localhost:" + wm.getHttpPort() + "/s3/upload";

        stubFor(get(urlPathEqualTo("/file-upload"))
            .willReturn(okJson(fileUploadJson(uploadUrl))));
        stubFor(put(urlPathEqualTo("/s3/upload"))
            .willReturn(aResponse().withStatus(200)));
        stubFor(post(urlPathEqualTo("/organisations/" + ORG_ID + "/deliveries/letters"))
            .willReturn(aResponse()
                .withStatus(422)
                .withHeader("Content-Type", "application/vnd.api+json")
                .withBody(errorJson(422,
                    "When auto_send is set to true delivery_product field is required."))));

        LetterCreateRequest request = LetterCreateRequest.builder()
            .fileBytes(new byte[]{1, 2, 3})
            .fileOriginalName("lorem.pdf")
            .addressPosition(AddressPosition.LEFT)
            .autoSend(true)
            .build();

        ValidationException ex = assertThrows(ValidationException.class,
            () -> endpoint.create(request));

        assertEquals(422, ex.getStatusCode());
        verify(postRequestedFor(urlPathEqualTo(
            "/organisations/" + ORG_ID + "/deliveries/letters")));
    }

    // -----------------------------------------------------------------------
    // 401 on file upload — testCreateAndUploadUnauthorized equivalent
    // -----------------------------------------------------------------------

    @Test
    void testCreateUnauthorized(WireMockRuntimeInfo wm) {
        // Higher priority overrides the setUp stub so this token fetch returns 401
        stubFor(post(urlPathEqualTo("/auth/access-tokens"))
            .atPriority(1)
            .willReturn(aResponse()
                .withStatus(401)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    {"error":"invalid_client","error_description":"Unauthorized"}
                    """)));

        // Fresh endpoint with un-cached OAuth — first getValidToken() call hits the 401 stub
        String baseUrl = "http://localhost:" + wm.getHttpPort();
        PingenConfig config = PingenConfig.builder()
            .clientId("bad-id")
            .clientSecret("bad-secret")
            .apiUrl(baseUrl)
            .identityUrl(baseUrl)
            .build();
        ApiClient apiClient = new ApiClient(config);
        LettersEndpoint unauthEndpoint =
            new LettersEndpoint(apiClient, new OAuth(apiClient, config), ORG_ID);

        LetterCreateRequest request = LetterCreateRequest.builder()
            .fileBytes(new byte[]{1, 2, 3})
            .fileOriginalName("lorem.pdf")
            .addressPosition(AddressPosition.LEFT)
            .autoSend(false)
            .build();

        assertThrows(AuthenticationException.class, () -> unauthEndpoint.create(request));
    }

    // -----------------------------------------------------------------------
    // send — testSend equivalent
    // -----------------------------------------------------------------------

    @Test
    void testSend() {
        stubFor(patch(urlPathEqualTo(
            "/organisations/" + ORG_ID + "/deliveries/letters/" + LETTER_ID + "/send"))
            .willReturn(okJson(letterResourceJson("submitted"))));

        LetterSendRequest sendRequest = LetterSendRequest.builder()
            .deliveryProduct(DeliveryProduct.FAST)
            .printMode(PrintMode.SIMPLEX)
            .printSpectrum(PrintSpectrum.COLOR)
            .build();

        Resource<Letter> response = endpoint.send(LETTER_ID, sendRequest);

        assertEquals(LETTER_ID, response.getId());
        assertEquals("submitted", response.getAttributes().getStatus());

        verify(patchRequestedFor(urlPathEqualTo(
            "/organisations/" + ORG_ID + "/deliveries/letters/" + LETTER_ID + "/send")));
    }

    // -----------------------------------------------------------------------
    // cancel — testCancel equivalent
    // -----------------------------------------------------------------------

    @Test
    void testCancel() {
        stubFor(patch(urlPathEqualTo(
            "/organisations/" + ORG_ID + "/deliveries/letters/" + LETTER_ID + "/cancel"))
            .willReturn(aResponse().withStatus(202)));

        assertDoesNotThrow(() -> endpoint.cancel(LETTER_ID));

        verify(patchRequestedFor(urlPathEqualTo(
            "/organisations/" + ORG_ID + "/deliveries/letters/" + LETTER_ID + "/cancel")));
    }

    // -----------------------------------------------------------------------
    // delete — testDelete equivalent
    // -----------------------------------------------------------------------

    @Test
    void testDelete() {
        stubFor(delete(urlPathEqualTo(
            "/organisations/" + ORG_ID + "/deliveries/letters/" + LETTER_ID))
            .willReturn(aResponse().withStatus(204)));

        assertDoesNotThrow(() -> endpoint.delete(LETTER_ID));

        verify(deleteRequestedFor(urlPathEqualTo(
            "/organisations/" + ORG_ID + "/deliveries/letters/" + LETTER_ID)));
    }

    // -----------------------------------------------------------------------
    // calculatePrice — testCalculatePrice equivalent
    // -----------------------------------------------------------------------

    @Test
    void testCalculatePrice() {
        stubFor(post(urlPathEqualTo(
            "/organisations/" + ORG_ID + "/deliveries/letters/price-calculator"))
            .willReturn(okJson("""
                {
                    "data": {
                        "type": "letter_price_calculator",
                        "attributes": {
                            "currency": "CHF",
                            "price": 2.01
                        }
                    }
                }
                """)));

        LetterPriceCalculatorRequest request = LetterPriceCalculatorRequest.builder()
            .country("CH")
            .paperTypes(List.of("normal", "qr"))
            .printMode(PrintMode.SIMPLEX)
            .printSpectrum(PrintSpectrum.COLOR)
            .deliveryProduct(DeliveryProduct.FAST)
            .build();

        Resource<LetterPriceCalculatorResult> response = endpoint.calculatePrice(request);

        assertEquals("CHF", response.getAttributes().getCurrency());
        assertEquals(2.01, response.getAttributes().getPrice());

        verify(postRequestedFor(urlPathEqualTo(
            "/organisations/" + ORG_ID + "/deliveries/letters/price-calculator")));
    }

    // -----------------------------------------------------------------------
    // getFile with 302 redirect — testGetFile equivalent
    // -----------------------------------------------------------------------

    @Test
    void testGetFile() {
        String fileUrl = "https://storage.example.com/letters/letter-uuid.pdf";

        stubFor(get(urlPathEqualTo(
            "/organisations/" + ORG_ID + "/deliveries/letters/" + LETTER_ID + "/file"))
            .willReturn(aResponse()
                .withStatus(302)
                .withHeader("Location", fileUrl)));

        String result = endpoint.getFile(LETTER_ID);

        assertEquals(fileUrl, result);
        verify(getRequestedFor(urlPathEqualTo(
            "/organisations/" + ORG_ID + "/deliveries/letters/" + LETTER_ID + "/file")));
    }

    // -----------------------------------------------------------------------
    // 429 on getFile — testRateLimit equivalent
    // -----------------------------------------------------------------------

    @Test
    void testRateLimitException() {
        stubFor(get(urlPathEqualTo(
            "/organisations/" + ORG_ID + "/deliveries/letters/" + LETTER_ID + "/file"))
            .willReturn(aResponse()
                .withStatus(429)
                .withHeader("Content-Type", "application/vnd.api+json")
                .withHeader("Retry-After", "60")
                .withBody(errorJson(429, "Too Many Requests"))));

        RateLimitException ex = assertThrows(RateLimitException.class,
            () -> endpoint.getFile(LETTER_ID));

        assertEquals(429, ex.getStatusCode());
        assertEquals(60, ex.getRetryAfter());

        verify(getRequestedFor(urlPathEqualTo(
            "/organisations/" + ORG_ID + "/deliveries/letters/" + LETTER_ID + "/file")));
    }

    // -----------------------------------------------------------------------
    // 403 → ApiException — testApiException equivalent
    // -----------------------------------------------------------------------

    @Test
    void testApiException() {
        stubFor(get(urlPathEqualTo(
            "/organisations/" + ORG_ID + "/deliveries/letters/" + LETTER_ID + "/file"))
            .willReturn(aResponse()
                .withStatus(403)
                .withHeader("Content-Type", "application/vnd.api+json")
                .withBody(errorJson(403, "Forbidden"))));

        ApiException ex = assertThrows(ApiException.class,
            () -> endpoint.getFile(LETTER_ID));

        assertEquals(403, ex.getStatusCode());
        assertNotNull(ex.getResponseBody());

        verify(getRequestedFor(urlPathEqualTo(
            "/organisations/" + ORG_ID + "/deliveries/letters/" + LETTER_ID + "/file")));
    }

    // -----------------------------------------------------------------------
    // getEvents for a specific letter
    // -----------------------------------------------------------------------

    @Test
    void testGetEvents() {
        stubFor(get(urlPathEqualTo(
            "/organisations/" + ORG_ID + "/deliveries/letters/" + LETTER_ID + "/events"))
            .willReturn(okJson(eventsCollectionJson())));

        PagedResponse<DeliveryEvent> response = endpoint.getEvents(LETTER_ID);

        assertEquals(1, response.getTotal());
        assertEquals(1, response.size());
        assertEquals("LETTER_CREATED", response.getItems().get(0).getAttributes().getCode());
        assertFalse(response.getItems().get(0).getAttributes().getHasImage());

        verify(getRequestedFor(urlPathEqualTo(
            "/organisations/" + ORG_ID + "/deliveries/letters/" + LETTER_ID + "/events")));
    }

    // -----------------------------------------------------------------------
    // Global event feeds
    // -----------------------------------------------------------------------

    @Test
    void testGetDeliveredEvents() {
        stubFor(get(urlPathEqualTo(
            "/organisations/" + ORG_ID + "/deliveries/letters/events/delivered"))
            .willReturn(okJson(eventsCollectionJson())));

        PagedResponse<DeliveryEvent> response = endpoint.getDeliveredEvents();

        assertEquals(1, response.size());
        verify(getRequestedFor(urlPathEqualTo(
            "/organisations/" + ORG_ID + "/deliveries/letters/events/delivered")));
    }

    @Test
    void testGetSentEvents() {
        stubFor(get(urlPathEqualTo(
            "/organisations/" + ORG_ID + "/deliveries/letters/events/sent"))
            .willReturn(okJson(eventsCollectionJson())));

        PagedResponse<DeliveryEvent> response = endpoint.getSentEvents();

        assertEquals(1, response.size());
        verify(getRequestedFor(urlPathEqualTo(
            "/organisations/" + ORG_ID + "/deliveries/letters/events/sent")));
    }

    @Test
    void testGetIssueEvents() {
        stubFor(get(urlPathEqualTo(
            "/organisations/" + ORG_ID + "/deliveries/letters/events/issues"))
            .willReturn(okJson(eventsCollectionJson())));

        PagedResponse<DeliveryEvent> response = endpoint.getIssueEvents();

        assertEquals(1, response.size());
        verify(getRequestedFor(urlPathEqualTo(
            "/organisations/" + ORG_ID + "/deliveries/letters/events/issues")));
    }

    @Test
    void testGetUndeliverableEvents() {
        stubFor(get(urlPathEqualTo(
            "/organisations/" + ORG_ID + "/deliveries/letters/events/undeliverable"))
            .willReturn(okJson(eventsCollectionJson())));

        PagedResponse<DeliveryEvent> response = endpoint.getUndeliverableEvents();

        assertEquals(1, response.size());
        verify(getRequestedFor(urlPathEqualTo(
            "/organisations/" + ORG_ID + "/deliveries/letters/events/undeliverable")));
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

    private static String letterResourceJson(String status) {
        return """
            {
                "data": {
                    "id": "%s",
                    "type": "letters",
                    "attributes": {
                        "status": "%s",
                        "file_original_name": "lorem.pdf",
                        "file_pages": 2,
                        "address": "Hans Meier, Example Street 4, 8000 Zurich, Switzerland",
                        "address_position": "left",
                        "country": "CH",
                        "delivery_product": "fast",
                        "print_mode": "simplex",
                        "print_spectrum": "color",
                        "price_currency": "CHF",
                        "price_value": 1.25,
                        "paper_types": ["normal"],
                        "fonts": [],
                        "created_at": "2020-11-19T09:42:48+01:00",
                        "updated_at": "2020-11-19T09:42:48+01:00"
                    }
                }
            }
            """.formatted(LETTER_ID, status);
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
                            "code": "LETTER_CREATED",
                            "name": "Letter Created",
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
