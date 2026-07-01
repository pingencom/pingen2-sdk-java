package com.pingen.sdk.endpoints;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.pingen.sdk.PingenConfig;
import com.pingen.sdk.auth.OAuth;
import com.pingen.sdk.client.ApiClient;
import com.pingen.sdk.exception.ApiException;
import com.pingen.sdk.exception.RateLimitException;
import com.pingen.sdk.models.common.CollectionParams;
import com.pingen.sdk.models.common.PagedResponse;
import com.pingen.sdk.models.common.Resource;
import com.pingen.sdk.models.webhook.Webhook;
import com.pingen.sdk.models.webhook.WebhookCreateRequest;
import com.pingen.sdk.models.webhook.WebhookEventCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

@WireMockTest
class WebhooksEndpointTest {

    private static final String ORG_ID = "org-uuid";
    private static final String WEBHOOK_ID = "webhook-uuid";

    private WebhooksEndpoint endpoint;

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
        endpoint = new WebhooksEndpoint(apiClient, new OAuth(apiClient, config), ORG_ID);
    }

    @Test
    void testGetCollection() {
        stubFor(get(urlPathEqualTo("/organisations/" + ORG_ID + "/webhooks"))
            .willReturn(okJson(collectionJson())));

        PagedResponse<Webhook> response = endpoint.getCollection();

        assertEquals(1, response.getTotal());
        assertEquals(1, response.size());
        assertFalse(response.hasNext());

        verify(getRequestedFor(urlPathEqualTo("/organisations/" + ORG_ID + "/webhooks")));
    }

    @Test
    void testGetCollectionWithPagination() {
        stubFor(get(urlPathEqualTo("/organisations/" + ORG_ID + "/webhooks"))
            .willReturn(okJson(collectionJson())));

        CollectionParams params = CollectionParams.builder().page(1, 10).build();
        PagedResponse<Webhook> response = endpoint.getCollection(params);

        assertEquals(1, response.getTotal());

        verify(getRequestedFor(urlPathEqualTo("/organisations/" + ORG_ID + "/webhooks"))
            .withQueryParam("page[number]", equalTo("1"))
            .withQueryParam("page[limit]", equalTo("10")));
    }

    @Test
    void testGet() {
        stubFor(get(urlPathEqualTo("/organisations/" + ORG_ID + "/webhooks/" + WEBHOOK_ID))
            .willReturn(okJson(webhookResourceJson())));

        Resource<Webhook> response = endpoint.get(WEBHOOK_ID).orElseThrow();

        assertEquals(WEBHOOK_ID, response.getId());
        assertEquals("webhooks", response.getType());
        assertEquals("https://example.com/webhook", response.getAttributes().getUrl());
        assertEquals(WebhookEventCategory.DELIVERED, response.getAttributes().getEventCategory());

        verify(getRequestedFor(urlPathEqualTo("/organisations/" + ORG_ID + "/webhooks/" + WEBHOOK_ID)));
    }

    @Test
    void testCreate() {
        stubFor(post(urlPathEqualTo("/organisations/" + ORG_ID + "/webhooks"))
            .willReturn(aResponse()
                .withStatus(201)
                .withHeader("Content-Type", "application/vnd.api+json")
                .withBody(webhookResourceJson())));

        WebhookCreateRequest request = WebhookCreateRequest.builder()
            .url("https://example.com/webhook")
            .eventCategory(WebhookEventCategory.DELIVERED)
            .signingKey("my-secret-signing-key-123")
            .build();

        Resource<Webhook> response = endpoint.create(request);

        assertEquals(WEBHOOK_ID, response.getId());
        assertEquals("https://example.com/webhook", response.getAttributes().getUrl());
        assertEquals(WebhookEventCategory.DELIVERED, response.getAttributes().getEventCategory());

        verify(postRequestedFor(urlPathEqualTo("/organisations/" + ORG_ID + "/webhooks")));
    }

    @Test
    void testDelete() {
        stubFor(delete(urlPathEqualTo("/organisations/" + ORG_ID + "/webhooks/" + WEBHOOK_ID))
            .willReturn(aResponse().withStatus(204)));

        assertDoesNotThrow(() -> endpoint.delete(WEBHOOK_ID));

        verify(deleteRequestedFor(urlPathEqualTo(
            "/organisations/" + ORG_ID + "/webhooks/" + WEBHOOK_ID)));
    }

    @Test
    void testGetCollectionRateLimit() {
        stubFor(get(urlPathEqualTo("/organisations/" + ORG_ID + "/webhooks"))
            .willReturn(aResponse()
                .withStatus(429)
                .withHeader("Content-Type", "application/vnd.api+json")
                .withHeader("Retry-After", "30")
                .withBody(errorJson(429, "Too Many Requests"))));

        RateLimitException ex = assertThrows(RateLimitException.class,
            () -> endpoint.getCollection());

        assertEquals(429, ex.getStatusCode());
        assertEquals(30, ex.getRetryAfter());

        verify(getRequestedFor(urlPathEqualTo("/organisations/" + ORG_ID + "/webhooks")));
    }

    @Test
    void testApiException() {
        stubFor(get(urlPathEqualTo("/organisations/" + ORG_ID + "/webhooks/" + WEBHOOK_ID))
            .willReturn(aResponse()
                .withStatus(403)
                .withHeader("Content-Type", "application/vnd.api+json")
                .withBody(errorJson(403, "Forbidden"))));

        ApiException ex = assertThrows(ApiException.class,
            () -> endpoint.get(WEBHOOK_ID));

        assertEquals(403, ex.getStatusCode());
        assertNotNull(ex.getResponseBody());

        verify(getRequestedFor(urlPathEqualTo("/organisations/" + ORG_ID + "/webhooks/" + WEBHOOK_ID)));
    }

    // -----------------------------------------------------------------------
    // JSON helpers
    // -----------------------------------------------------------------------

    private static String collectionJson() {
        return """
            {
                "data": [
                    {
                        "id": "%s",
                        "type": "webhooks",
                        "attributes": {
                            "url": "https://example.com/webhook",
                            "event_category": "delivered",
                            "signing_key": "my-secret-signing-key-123"
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
            """.formatted(WEBHOOK_ID);
    }

    private static String webhookResourceJson() {
        return """
            {
                "data": {
                    "id": "%s",
                    "type": "webhooks",
                    "attributes": {
                        "url": "https://example.com/webhook",
                        "event_category": "delivered",
                        "signing_key": "my-secret-signing-key-123"
                    }
                }
            }
            """.formatted(WEBHOOK_ID);
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
