package com.pingen.sdk.integration;

import com.pingen.sdk.Pingen;
import com.pingen.sdk.auth.AccessToken;
import com.pingen.sdk.models.common.PagedResponse;
import com.pingen.sdk.models.organisation.Organisation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test verifying that the SDK transparently refreshes an expired OAuth token.
 *
 * Run with: mvn test -P integration-tests -Dtest=com.pingen.sdk.integration.PingenOAuthRefreshIntegrationTest
 */
@Tag("integration")
public class PingenOAuthRefreshIntegrationTest {

    private static final String CLIENT_ID = IntegrationTestCredentials.clientId();
    private static final String CLIENT_SECRET = IntegrationTestCredentials.clientSecret();

    private static Pingen pingen;

    @BeforeAll
    static void setUp() {
        pingen = Pingen.builder()
                .clientId(CLIENT_ID)
                .clientSecret(CLIENT_SECRET)
                .staging()
                .build();
    }

    @Test
    void testOrganisationsRetrievableAfterTokenExpiry() throws InterruptedException {
        // Step 1: initial call acquires the first token
        PagedResponse<Organisation> firstResponse = pingen.organisations().getCollection();
        assertFalse(firstResponse.getItems().isEmpty(), "Expected at least one organisation");

        AccessToken firstToken = pingen.getOAuth().getCurrentToken();
        assertNotNull(firstToken, "Token must be present after first call");
        String firstTokenValue = firstToken.getAccessToken();

        // Step 2: backdate issuedAt so the token expires 2 seconds from now.
        // OAuth.TOKEN_REFRESH_BUFFER_SECONDS = 300, so any token with < 300 s
        // remaining is treated as expiring soon — setting TTL to 2 s is well inside
        // that window and guarantees a refresh on the next call.
        firstToken.setIssuedAt(Instant.now().minusSeconds(firstToken.getExpiresIn() - 2));
        Instant manipulatedExpiry = firstToken.getExpiresAt();

        System.out.printf("Token expiry set to: %s (in ~%d ms)%n",
                manipulatedExpiry,
                Duration.between(Instant.now(), manipulatedExpiry).toMillis());

        // Step 3: sleep until after the (backdated) expiry
        long waitMs = Duration.between(Instant.now(), manipulatedExpiry).toMillis() + 500;
        System.out.printf("Sleeping %d ms until token is past expiry...%n", waitMs);
        Thread.sleep(Math.max(waitMs, 0));
        System.out.println("Awoke — token should now be expired.");

        assertTrue(firstToken.isExpired(),
                "Token must be expired before the refresh call");

        // Step 4: make another organisations call — the SDK must silently refresh
        PagedResponse<Organisation> secondResponse = pingen.organisations().getCollection();
        assertFalse(secondResponse.getItems().isEmpty(),
                "Organisations must still be retrievable after token refresh");

        // Step 5: verify a brand-new token was issued
        AccessToken secondToken = pingen.getOAuth().getCurrentToken();
        assertNotNull(secondToken, "Token must be present after second call");
        assertNotEquals(firstTokenValue, secondToken.getAccessToken(),
                "A new access token must have been fetched from the OAuth server");
        assertFalse(secondToken.isExpired(),
                "Newly issued token must not already be expired");
        assertTrue(secondToken.getExpiresAt().isAfter(Instant.now()),
                "New token expiry must be in the future");

        System.out.printf("New token issued, expires at: %s%n", secondToken.getExpiresAt());
    }
}
