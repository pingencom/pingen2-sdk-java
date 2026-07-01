package com.pingen.sdk.webhook;

import com.pingen.sdk.exception.WebhookSignatureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class IncomingWebhookTest {

    private static final String SECRET = "webhook_test_secret";

    // Exact body and expected signature taken from the PHP SDK test
    private static final String WEBHOOK_BODY =
        "{\"data\":{\"type\":\"webhook_issues\",\"id\":\"a3233e48-5e70-4138-95b2-a72d4875016b\"," +
        "\"attributes\":{\"reason\":\"Page limit exceeded\",\"url\":\"https:\\/\\/test\\/receiver\"," +
        "\"created_at\":\"2023-08-03T11:24:39+0200\"},\"relationships\":{\"organisation\":" +
        "{\"links\":{\"related\":\"http:\\/\\/api-test.v2.pingen.com\\/organisations\\/2017973a-6403-444d-af05-eb4b2b7f5e2f\"}," +
        "\"data\":{\"type\":\"organisations\",\"id\":\"2017973a-6403-444d-af05-eb4b2b7f5e2f\"}}," +
        "\"letter\":{\"links\":{\"related\":\"http:\\/\\/api-test.v2.pingen.com\\/organisations\\/2017973a-6403-444d-af05-eb4b2b7f5e2f\\/deliveries\\/letters\\/4f31cdb2-bc0d-4db5-a13d-3336958dba02\"}," +
        "\"data\":{\"type\":\"letters\",\"id\":\"4f31cdb2-bc0d-4db5-a13d-3336958dba02\"}}," +
        "\"event\":{\"data\":{\"type\":\"letters_events\",\"id\":\"ba08eb5f-413c-4dd1-8ed6-aac2b96124d0\"}}}}," +
        "\"included\":[{\"type\":\"organisations\",\"id\":\"2017973a-6403-444d-af05-eb4b2b7f5e2f\"," +
        "\"attributes\":{\"name\":\"Prof. Leopoldo Hahn\",\"status\":\"active\",\"plan\":\"free\"," +
        "\"billing_mode\":\"postpaid\",\"billing_currency\":\"CHF\",\"billing_balance\":0," +
        "\"default_country\":\"CH\",\"edition\":\"pingen\",\"default_address_position\":\"left\"," +
        "\"data_retention_addresses\":12,\"data_retention_pdf\":12,\"color\":\"#0758FF\"," +
        "\"created_at\":\"2023-08-03T11:24:39+0200\",\"updated_at\":\"2023-08-03T11:24:39+0200\"}," +
        "\"links\":{\"self\":\"http:\\/\\/api-test.v2.pingen.com\\/organisations\\/2017973a-6403-444d-af05-eb4b2b7f5e2f\"}}," +
        "{\"type\":\"letters\",\"id\":\"4f31cdb2-bc0d-4db5-a13d-3336958dba02\"," +
        "\"attributes\":{\"status\":\"validating\",\"file_original_name\":\"ullam.pdf\"," +
        "\"file_pages\":null,\"address\":null,\"address_position\":\"left\",\"country\":null," +
        "\"delivery_product\":\"fast\",\"print_mode\":\"simplex\",\"print_spectrum\":\"color\"," +
        "\"price_currency\":null,\"price_value\":null,\"paper_types\":null,\"fonts\":null," +
        "\"source\":\"app\",\"tracking_number\":null,\"submitted_at\":null," +
        "\"created_at\":\"2023-08-03T11:24:39+0200\",\"updated_at\":\"2023-08-03T11:24:39+0200\"}," +
        "\"links\":{\"self\":\"http:\\/\\/api-test.v2.pingen.com\\/organisations\\/2017973a-6403-444d-af05-eb4b2b7f5e2f\\/letters\\/4f31cdb2-bc0d-4db5-a13d-3336958dba02\"}}," +
        "{\"type\":\"letters_events\",\"id\":\"ba08eb5f-413c-4dd1-8ed6-aac2b96124d0\"," +
        "\"attributes\":{\"code\":\"file_too_many_pages\",\"name\":\"Page limit exceeded\"," +
        "\"producer\":\"Pingen\",\"location\":\"\",\"has_image\":false,\"data\":[]," +
        "\"emitted_at\":\"2023-08-03T11:24:39+0200\",\"created_at\":\"2023-08-03T11:24:39+0200\"," +
        "\"updated_at\":\"2023-08-03T11:24:39+0200\"}}]}";

    private static final String VALID_SIGNATURE =
        "62c589935b0a67e0e64a69ef2b62ee91acc379c71094118c625cc7becd6a09d3";

    private IncomingWebhook incomingWebhook;

    @BeforeEach
    void setUp() {
        incomingWebhook = new IncomingWebhook(SECRET);
    }

    @Test
    void testPositive() {
        WebhookRequest request = new WebhookRequest(
            "POST",
            Map.of("Signature", VALID_SIGNATURE),
            WEBHOOK_BODY
        );

        IncomingWebhookDetails response = incomingWebhook.processWebhook(request);

        assertInstanceOf(IncomingWebhookDetails.class, response);

        IncomingWebhookDetailsData data = response.getData();
        assertNotNull(data);
        assertEquals("a3233e48-5e70-4138-95b2-a72d4875016b", data.getId());
        assertEquals("webhook_issues", data.getType());

        // attributes
        assertNotNull(data.getAttributes());
        assertEquals("Page limit exceeded", data.getAttributes().get("reason"));

        // relationships
        IncomingWebhookRelationships rel = data.getRelationships();
        assertNotNull(rel);

        assertNotNull(rel.getOrganisation());
        assertEquals("2017973a-6403-444d-af05-eb4b2b7f5e2f", rel.getOrganisation().getData().getId());
        assertEquals("organisations", rel.getOrganisation().getData().getType());
        assertNotNull(rel.getOrganisation().getLinks().getRelated());

        assertNotNull(rel.getLetter());
        assertEquals("4f31cdb2-bc0d-4db5-a13d-3336958dba02", rel.getLetter().getData().getId());
        assertEquals("letters", rel.getLetter().getData().getType());
        assertNotNull(rel.getLetter().getLinks().getRelated());

        assertNotNull(rel.getEvent());
        assertEquals("ba08eb5f-413c-4dd1-8ed6-aac2b96124d0", rel.getEvent().getData().getId());
        assertEquals("letters_events", rel.getEvent().getData().getType());
    }

    @Test
    void testRequestAreNotPostMethod() {
        WebhookRequest request = new WebhookRequest("GET", Map.of(), "");

        WebhookSignatureException ex = assertThrows(WebhookSignatureException.class,
            () -> incomingWebhook.processWebhook(request));

        assertEquals(403, ex.getStatusCode());
        assertEquals("Only POST requests are allowed.", ex.getMessage());
    }

    @Test
    void testSignatureHeaderMissing() {
        WebhookRequest request = new WebhookRequest("POST", Map.of(), "");

        WebhookSignatureException ex = assertThrows(WebhookSignatureException.class,
            () -> incomingWebhook.processWebhook(request));

        assertEquals(403, ex.getStatusCode());
        assertEquals("Signature missing.", ex.getMessage());
    }

    @Test
    void testSignatureAreDifferent() {
        WebhookRequest request = new WebhookRequest(
            "POST",
            Map.of("Signature", "example"),
            WEBHOOK_BODY
        );

        WebhookSignatureException ex = assertThrows(WebhookSignatureException.class,
            () -> incomingWebhook.processWebhook(request));

        assertEquals(403, ex.getStatusCode());
        assertEquals("Webhook signature matching failed.", ex.getMessage());
    }
}
