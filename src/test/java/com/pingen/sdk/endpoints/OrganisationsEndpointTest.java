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
import com.pingen.sdk.models.organisation.Organisation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

@WireMockTest
class OrganisationsEndpointTest {

    private static final String ORG_ID = "org-uuid";

    private OrganisationsEndpoint endpoint;

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
        endpoint = new OrganisationsEndpoint(apiClient, new OAuth(apiClient, config));
    }

    @Test
    void testGetCollection() {
        stubFor(get(urlPathEqualTo("/organisations"))
            .willReturn(okJson(collectionJson())));

        PagedResponse<Organisation> response = endpoint.getCollection();

        assertEquals(5, response.getTotal());
        assertEquals(1, response.getCurrentPage());
        assertEquals(20, response.getPageLimit());
        assertEquals(1, response.getLastPage());
        assertEquals(1, response.size());
        assertFalse(response.hasNext());

        verify(getRequestedFor(urlPathEqualTo("/organisations")));
    }

    @Test
    void testGet() {
        stubFor(get(urlPathEqualTo("/organisations/" + ORG_ID))
            .willReturn(okJson(organisationResourceJson())));

        Resource<Organisation> response = endpoint.get(ORG_ID).orElseThrow();

        assertEquals(ORG_ID, response.getId());
        assertEquals("organisations", response.getType());
        assertEquals("My Organisation", response.getAttributes().getName());
        assertEquals("active", response.getAttributes().getStatus());
        assertEquals("CHF", response.getAttributes().getBillingCurrency());

        verify(getRequestedFor(urlPathEqualTo("/organisations/" + ORG_ID)));
    }

    @Test
    void testGetCollectionWithPagination() {
        stubFor(get(urlPathEqualTo("/organisations"))
            .willReturn(okJson(collectionJson())));

        CollectionParams params = CollectionParams.builder().page(1, 5).build();
        PagedResponse<Organisation> response = endpoint.getCollection(params);

        assertEquals(5, response.getTotal());

        verify(getRequestedFor(urlPathEqualTo("/organisations"))
            .withQueryParam("page[number]", equalTo("1"))
            .withQueryParam("page[limit]", equalTo("5")));
    }

    @Test
    void testUserAgentHeaderIsSent() {
        stubFor(get(urlPathEqualTo("/organisations"))
            .willReturn(okJson(collectionJson())));

        endpoint.getCollection();

        verify(getRequestedFor(urlPathEqualTo("/organisations"))
            .withHeader("User-Agent", equalTo("PINGEN.SDK.JAVA")));
    }

    @Test
    void testGetCollectionRateLimit() {
        stubFor(get(urlPathEqualTo("/organisations"))
            .willReturn(aResponse()
                .withStatus(429)
                .withHeader("Content-Type", "application/vnd.api+json")
                .withHeader("Retry-After", "30")
                .withBody(errorJson(429, "Too Many Requests"))));

        RateLimitException ex = assertThrows(RateLimitException.class,
            () -> endpoint.getCollection());

        assertEquals(429, ex.getStatusCode());
        assertEquals(30, ex.getRetryAfter());

        verify(getRequestedFor(urlPathEqualTo("/organisations")));
    }

    @Test
    void testGetApiException() {
        stubFor(get(urlPathEqualTo("/organisations/" + ORG_ID))
            .willReturn(aResponse()
                .withStatus(403)
                .withHeader("Content-Type", "application/vnd.api+json")
                .withBody(errorJson(403, "Forbidden"))));

        ApiException ex = assertThrows(ApiException.class,
            () -> endpoint.get(ORG_ID));

        assertEquals(403, ex.getStatusCode());
        assertNotNull(ex.getResponseBody());

        verify(getRequestedFor(urlPathEqualTo("/organisations/" + ORG_ID)));
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
                        "type": "organisations",
                        "attributes": {
                            "name": "My Organisation",
                            "status": "active",
                            "plan": "business",
                            "billing_currency": "CHF",
                            "billing_balance": 100.00,
                            "created_at": "2020-01-01T00:00:00+01:00",
                            "updated_at": "2020-01-01T00:00:00+01:00"
                        }
                    }
                ],
                "links": {
                    "first": "string",
                    "last": "string",
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
                    "total": 5
                }
            }
            """.formatted(ORG_ID);
    }

    private static String organisationResourceJson() {
        return """
            {
                "data": {
                    "id": "%s",
                    "type": "organisations",
                    "attributes": {
                        "name": "My Organisation",
                        "status": "active",
                        "plan": "business",
                        "edition": "standard",
                        "billing_mode": "prepaid",
                        "billing_currency": "CHF",
                        "billing_balance": 100.00,
                        "default_country": "CH",
                        "created_at": "2020-01-01T00:00:00+01:00",
                        "updated_at": "2020-01-01T00:00:00+01:00"
                    }
                }
            }
            """.formatted(ORG_ID);
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
