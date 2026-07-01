package com.pingen.sdk.endpoints;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.pingen.sdk.PingenConfig;
import com.pingen.sdk.auth.OAuth;
import com.pingen.sdk.client.ApiClient;
import com.pingen.sdk.exception.ApiException;
import com.pingen.sdk.exception.RateLimitException;
import com.pingen.sdk.models.common.PagedResponse;
import com.pingen.sdk.models.common.Resource;
import com.pingen.sdk.models.user.User;
import com.pingen.sdk.models.user.UserAssociation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

@WireMockTest
class UserEndpointTest {

    private UserEndpoint endpoint;

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
        endpoint = new UserEndpoint(apiClient, new OAuth(apiClient, config));
    }

    @Test
    void testGet() {
        stubFor(get(urlPathEqualTo("/user"))
            .willReturn(okJson(userResourceJson())));

        Resource<User> response = endpoint.get();

        assertEquals("user-uuid", response.getId());
        assertEquals("users", response.getType());
        assertEquals("john.doe@example.com", response.getAttributes().getEmail());
        assertEquals("John", response.getAttributes().getFirstName());
        assertEquals("Doe", response.getAttributes().getLastName());
        assertEquals("active", response.getAttributes().getStatus());

        verify(getRequestedFor(urlPathEqualTo("/user")));
    }

    @Test
    void testGetRateLimit() {
        stubFor(get(urlPathEqualTo("/user"))
            .willReturn(aResponse()
                .withStatus(429)
                .withHeader("Content-Type", "application/vnd.api+json")
                .withHeader("Retry-After", "60")
                .withBody(errorJson(429, "Too Many Requests"))));

        RateLimitException ex = assertThrows(RateLimitException.class,
            () -> endpoint.get());

        assertEquals(429, ex.getStatusCode());
        assertEquals(60, ex.getRetryAfter());

        verify(getRequestedFor(urlPathEqualTo("/user")));
    }

    @Test
    void testGetApiException() {
        stubFor(get(urlPathEqualTo("/user"))
            .willReturn(aResponse()
                .withStatus(403)
                .withHeader("Content-Type", "application/vnd.api+json")
                .withBody(errorJson(403, "Forbidden"))));

        ApiException ex = assertThrows(ApiException.class,
            () -> endpoint.get());

        assertEquals(403, ex.getStatusCode());
        assertNotNull(ex.getResponseBody());

        verify(getRequestedFor(urlPathEqualTo("/user")));
    }

    @Test
    void testGetAssociations() {
        stubFor(get(urlPathEqualTo("/user/associations"))
            .willReturn(okJson(associationsCollectionJson())));

        PagedResponse<UserAssociation> response = endpoint.getAssociations();

        assertEquals(1, response.getTotal());
        assertEquals(1, response.size());
        assertEquals("owner", response.getItems().get(0).getAttributes().getRole());
        assertEquals("active", response.getItems().get(0).getAttributes().getStatus());

        verify(getRequestedFor(urlPathEqualTo("/user/associations")));
    }

    @Test
    void testGetAssociationsRateLimit() {
        stubFor(get(urlPathEqualTo("/user/associations"))
            .willReturn(aResponse()
                .withStatus(429)
                .withHeader("Content-Type", "application/vnd.api+json")
                .withHeader("Retry-After", "30")
                .withBody(errorJson(429, "Too Many Requests"))));

        RateLimitException ex = assertThrows(RateLimitException.class,
            () -> endpoint.getAssociations());

        assertEquals(429, ex.getStatusCode());
        assertEquals(30, ex.getRetryAfter());

        verify(getRequestedFor(urlPathEqualTo("/user/associations")));
    }

    @Test
    void testGetAssociationsApiException() {
        stubFor(get(urlPathEqualTo("/user/associations"))
            .willReturn(aResponse()
                .withStatus(403)
                .withHeader("Content-Type", "application/vnd.api+json")
                .withBody(errorJson(403, "Forbidden"))));

        ApiException ex = assertThrows(ApiException.class,
            () -> endpoint.getAssociations());

        assertEquals(403, ex.getStatusCode());
        assertNotNull(ex.getResponseBody());

        verify(getRequestedFor(urlPathEqualTo("/user/associations")));
    }

    // -----------------------------------------------------------------------
    // JSON helpers
    // -----------------------------------------------------------------------

    private static String userResourceJson() {
        return """
            {
                "data": {
                    "id": "user-uuid",
                    "type": "users",
                    "attributes": {
                        "email": "john.doe@example.com",
                        "first_name": "John",
                        "last_name": "Doe",
                        "status": "active",
                        "language": "en",
                        "edition": "standard",
                        "flags": [],
                        "created_at": "2020-01-01T00:00:00+01:00",
                        "updated_at": "2020-01-01T00:00:00+01:00"
                    }
                }
            }
            """;
    }

    private static String associationsCollectionJson() {
        return """
            {
                "data": [
                    {
                        "id": "assoc-uuid",
                        "type": "user_associations",
                        "attributes": {
                            "role": "owner",
                            "status": "active",
                            "created_at": "2020-01-01T00:00:00+01:00",
                            "updated_at": "2020-01-01T00:00:00+01:00"
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
