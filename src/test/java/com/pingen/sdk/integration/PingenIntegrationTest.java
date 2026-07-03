package com.pingen.sdk.integration;

import com.pingen.sdk.Pingen;
import com.pingen.sdk.exception.ApiException;
import com.pingen.sdk.models.batch.Batch;
import com.pingen.sdk.models.batch.BatchCreateRequest;
import com.pingen.sdk.models.batch.BatchEvent;
import com.pingen.sdk.models.batch.BatchIcon;
import com.pingen.sdk.models.batch.BatchStatistics;
import com.pingen.sdk.models.batch.BatchUpdateRequest;
import com.pingen.sdk.models.batch.BatchGroupingSplitType;
import com.pingen.sdk.models.batch.GroupingType;
import com.pingen.sdk.models.common.CollectionParams;
import com.pingen.sdk.models.common.DeliveryEvent;
import com.pingen.sdk.models.common.Filter;
import com.pingen.sdk.models.common.PagedResponse;
import com.pingen.sdk.models.common.Resource;
import com.pingen.sdk.models.ebill.EBill;
import com.pingen.sdk.models.ebill.EBillCreateRequest;
import com.pingen.sdk.models.ebill.EBillMetaData;
import com.pingen.sdk.models.email.Email;
import com.pingen.sdk.models.email.EmailCreateRequest;
import com.pingen.sdk.models.email.EmailMetaData;
import com.pingen.sdk.models.letter.*;
import com.pingen.sdk.models.organisation.Organisation;
import com.pingen.sdk.models.user.User;
import com.pingen.sdk.models.user.UserAssociation;
import com.pingen.sdk.models.webhook.Webhook;
import com.pingen.sdk.models.webhook.WebhookCreateRequest;
import com.pingen.sdk.models.webhook.WebhookEventCategory;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for the Pingen Java SDK against the staging environment.
 * <p>
 * These tests make real HTTP calls and require valid staging credentials.
 * They are excluded from normal test runs and must be triggered explicitly:
 * <p>
 * mvn test -DexcludedGroups="" -Dgroups=integration
 */
@Tag("integration")
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PingenIntegrationTest {

    private static final String CLIENT_ID = IntegrationTestCredentials.clientId();
    private static final String CLIENT_SECRET = IntegrationTestCredentials.clientSecret();
    private static final String ORGANIZATION_NAME = IntegrationTestCredentials.organizationName();

    private static Pingen pingen;
    private static String orgId;
    private static byte[] invoicePdfBytes;

    private static String createdLetterId;
    private static String createdBatchId;
    private static String createdWebhookId;
    private static String createdEmailId;
    private static String createdEBillId;

    @BeforeAll
    static void setUp() {
        pingen = Pingen.builder()
                .clientId(CLIENT_ID)
                .clientSecret(CLIENT_SECRET)
                .staging()
                .build();

        PagedResponse<Organisation> orgs = pingen.organisations().getCollection();
        assertEquals(1, orgs.size(), "No organisations returned – check staging credentials");
        orgId = orgs.getItems().get(0).getId();
        System.out.println("Using organisation ID: " + orgId);

        invoicePdfBytes = TestDocumentCreator.createInvoicePdf();
        System.out.printf("Generated invoice PDF: %d bytes%n", invoicePdfBytes.length);
    }

    // -------------------------------------------------------------------------
    // Shared request-object helpers
    // -------------------------------------------------------------------------

    private static EmailMetaData buildEmailMetaData() {
        return EmailMetaData.builder()
                .senderName("Codeblock GmbH Test")
                .recipientEmail("claude.gex@codeblock.ch")
                .recipientName("Claude Gex")
                .replyEmail("noreply@example.com")
                .replyName("Codeblock GmbH Test")
                .subject("Integration Test Email")
                .content("Dear Recipient,\n\nThis is an integration test.\n\nBest regards")
                .build();
    }

    private static EBillMetaData buildEBillMetaData() {
        return EBillMetaData.builder()
                .invoiceNumber("Test-INV-001")
                .invoiceDate(LocalDate.now())
                .invoiceDueDate(LocalDate.now().plusDays(30))
                .recipientIdentifier("41100010014282213")
                .build();
    }

    // =========================================================================
    // Organisations
    // =========================================================================

    @Nested
    @Order(1)
    @DisplayName("Organisations")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class OrganisationsTests {

        @Test
        @Order(1)
        @DisplayName("GET /organisations – list all organisations")
        void testListOrganisations() {
            PagedResponse<Organisation> response = pingen.organisations().getCollection();

            assertEquals(1, response.size());

            Resource<Organisation> first = response.getItems().get(0);
            assertNotNull(first.getId());
            assertEquals(ORGANIZATION_NAME, first.getAttributes().getName());
        }

        @Test
        @Order(2)
        @DisplayName("GET /organisations – paginated (page 1, limit 5)")
        void testListOrganisationsPaginated() {
            PagedResponse<Organisation> response = pingen.organisations().getCollection(CollectionParams.builder().page(1, 5).build());

            assertEquals(1, response.size());
            assertEquals(ORGANIZATION_NAME, response.getItems().get(0).getAttributes().getName());
        }

        @Test
        @Order(3)
        @DisplayName("GET /organisations/{id} – get single organisation")
        void testGetOrganisationById() {
            Resource<Organisation> response = pingen.organisations().get(orgId).orElseThrow();

            assertEquals(orgId, response.getId());
            assertEquals(ORGANIZATION_NAME, response.getAttributes().getName());
        }
    }

    // =========================================================================
    // Letters
    // =========================================================================

    @Nested
    @Order(2)
    @DisplayName("Letters")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class LettersTests {
        final OffsetDateTime integrationTestStart = OffsetDateTime.now();

        @Nested
        @Order(2)
        @DisplayName("Letters Happy Case")
        @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
        class LettersHappyCaseTests {

            @Test
            @Order(1)
            @DisplayName("GET /organisations/{id}/letters – list all letters")
            void testListLetters() {
                PagedResponse<Letter> response = pingen.letters(orgId).getCollection(
                        CollectionParams.builder().filter(Filter.gt("created_at", integrationTestStart.toString())).build());
                assertEquals(0, response.size());
            }

            @Test
            @Order(2)
            @DisplayName("GET /organisations/{id}/letters – paginated (page 1, limit 3)")
            void testListLettersPaginated() {
                PagedResponse<Letter> response = pingen.letters(orgId).getCollection(
                        CollectionParams.builder().filter(Filter.gt("created_at", integrationTestStart.toString())).page(1, 3).build());
                assertEquals(0, response.size());
            }

            @Test
            @Order(3)
            @DisplayName("POST /organisations/{id}/letters – create letter")
            void testCreateLetter() throws InterruptedException {
                LetterCreateRequest request = LetterCreateRequest.builder()
                        .fileBytes(invoicePdfBytes)
                        .fileOriginalName(TestDocumentCreator.FILE_NAME)
                        .addressPosition(AddressPosition.RIGHT)
                        .deliveryProduct(DeliveryProduct.CHEAP)
                        .printMode(PrintMode.SIMPLEX)
                        .printSpectrum(PrintSpectrum.GRAYSCALE)
                        .autoSend(true)
                        .build();

                Resource<Letter> response = pingen.letters(orgId).create(request);

                assertNotNull(response.getId());
                assertEquals("validating", response.getAttributes().getStatus());

                createdLetterId = response.getId();
                assertTrue(pingen.letters(orgId).get(createdLetterId).isPresent());

                System.out.println("sleep 30 second so that collection will contain the newly created letter");
                TimeUnit.SECONDS.sleep(30);

                final PagedResponse<Letter> letters = pingen.letters(orgId).getCollection(
                        CollectionParams.builder().filter(Filter.gt("created_at", integrationTestStart.toString())).build());
                assertEquals(1, letters.size());
                assertEquals(createdLetterId, letters.getItems().get(0).getId());
            }

            @Test
            @Order(4)
            @DisplayName("GET /organisations/{id}/letters/{letterId} – get single letter")
            void testGetLetterById() {
                assertNotNull(createdLetterId, "Requires testCreateLetter to have run");

                Resource<Letter> response = pingen.letters(orgId).get(createdLetterId).orElseThrow();

                assertEquals(createdLetterId, response.getId());
                System.out.println("Letter status: " + response.getAttributes().getStatus());
            }

            @Test
            @Order(5)
            @DisplayName("GET /organisations/{id}/deliveries/letters/{id}/events – get letter events")
            void testGetLetterEvents() {
                assertNotNull(createdLetterId, "Requires testCreateLetter to have run");

                PagedResponse<DeliveryEvent> response = pingen.letters(orgId).getEvents(createdLetterId);

                System.out.printf("Letter events: %d%n", response.size());
                response.getItems().forEach(e ->
                        System.out.printf("  Event: code=%s, name=%s%n", e.getAttributes().getCode(), e.getAttributes().getName())
                );
            }

            @Test
            @Order(6)
            @DisplayName("GET /organisations/{id}/deliveries/letters/{id}/file – get letter file URL")
            void testGetLetterFile() throws InterruptedException {
                assertNotNull(createdLetterId, "Requires testCreateLetter to have run");

                String fileUrl = pingen.letters(orgId).getFile(createdLetterId);

                assertNotNull(fileUrl, "File URL should not be null");
                assertFalse(fileUrl.isBlank(), "File URL should not be blank");
                System.out.println("Letter file URL: " + fileUrl);
            }

            @Test
            @Order(7)
            @DisplayName("POST /organisations/{id}/deliveries/letters/price-calculator – calculate price")
            void testCalculateLetterPrice() {
                LetterPriceCalculatorRequest request = LetterPriceCalculatorRequest.builder()
                        .country("CH")
                        .paperTypes(List.of("normal", "normal"))
                        .printMode(PrintMode.SIMPLEX)
                        .printSpectrum(PrintSpectrum.GRAYSCALE)
                        .deliveryProduct(DeliveryProduct.CHEAP)
                        .build();

                Resource<LetterPriceCalculatorResult> response = pingen.letters(orgId).calculatePrice(request);

                assertNotNull(response.getId());
                System.out.printf("Price calculator: id=%s, currency=%s, price=%s%n",
                        response.getId(),
                        response.getAttributes() != null ? response.getAttributes().getCurrency() : "n/a",
                        response.getAttributes() != null ? response.getAttributes().getPrice() : "n/a");
            }

            @Test
            @Order(8)
            @DisplayName("GET /organisations/{id}/deliveries/letters/events/sent – global sent events")
            void testGetLetterSentEvents() {
                PagedResponse<DeliveryEvent> response = pingen.letters(orgId).getSentEvents();
                assertNotNull(response);
                System.out.printf("Global sent events: %d%n", response.size());
            }

            @Test
            @Order(9)
            @DisplayName("GET /organisations/{id}/deliveries/letters/events/delivered – global delivered events")
            void testGetLetterDeliveredEvents() {
                PagedResponse<DeliveryEvent> response = pingen.letters(orgId).getDeliveredEvents();
                assertNotNull(response);
                System.out.printf("Global delivered events: %d%n", response.size());
            }

            @Test
            @Order(10)
            @DisplayName("GET /organisations/{id}/deliveries/letters/events/issues – global issue events")
            void testGetLetterIssueEvents() {
                PagedResponse<DeliveryEvent> response = pingen.letters(orgId).getIssueEvents();
                assertNotNull(response);
                System.out.printf("Global issue events: %d%n", response.size());
            }

            @Test
            @Order(11)
            @DisplayName("GET /organisations/{id}/deliveries/letters/events/undeliverable – global undeliverable events")
            void testGetLetterUndeliverableEvents() {
                PagedResponse<DeliveryEvent> response = pingen.letters(orgId).getUndeliverableEvents();
                assertNotNull(response);
                System.out.printf("Global undeliverable events: %d%n", response.size());
            }
        }

        @Nested
        @Order(3)
        @DisplayName("Letters Cancel Case")
        @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
        class LettersCancelCaseTests {

            @Test
            @Order(1)
            @DisplayName("POST /organisations/{id}/letters – create letter for cancellation")
            void testCreateLetter() {
                LetterCreateRequest request = LetterCreateRequest.builder()
                        .fileBytes(invoicePdfBytes)
                        .fileOriginalName(TestDocumentCreator.FILE_NAME_CANCELLABLE)
                        .addressPosition(AddressPosition.RIGHT)
                        .deliveryProduct(DeliveryProduct.CHEAP)
                        .printMode(PrintMode.SIMPLEX)
                        .printSpectrum(PrintSpectrum.GRAYSCALE)
                        .autoSend(true)
                        .build();

                Resource<Letter> response = pingen.letters(orgId).create(request);

                assertNotNull(response.getId());
                assertEquals("validating", response.getAttributes().getStatus());

                createdLetterId = response.getId();
            }

            @Test
            @Order(2)
            @DisplayName("PATCH /organisations/{id}/letters/{letterId}/cancel – cancel letter")
            void testCancelLetter() throws InterruptedException {
                assertNotNull(createdLetterId, "Requires testCreateLetter to have run");

                System.out.println("sleep 10 second so that letter will be in correct state for cancel");
                TimeUnit.SECONDS.sleep(10);

                pingen.letters(orgId).cancel(createdLetterId);
            }
        }

        @Nested
        @Order(4)
        @DisplayName("Letters Delete Test")
        @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
        class LettersDeleteTests {

            @Test
            @Order(1)
            @DisplayName("POST /organisations/{id}/letters – create letter for deletion")
            void testCreateLetterDeletion() throws InterruptedException {
                LetterCreateRequest request = LetterCreateRequest.builder()
                        .fileBytes(invoicePdfBytes)
                        .fileOriginalName(TestDocumentCreator.FILE_NAME)
                        .addressPosition(AddressPosition.RIGHT)
                        .deliveryProduct(DeliveryProduct.CHEAP)
                        .printMode(PrintMode.SIMPLEX)
                        .printSpectrum(PrintSpectrum.GRAYSCALE)
                        .autoSend(false)
                        .build();

                Resource<Letter> response = pingen.letters(orgId).create(request);

                assertNotNull(response.getId());
                assertEquals("validating", response.getAttributes().getStatus());
                TimeUnit.SECONDS.sleep(5);

                createdLetterId = response.getId();
            }

            @Test
            @Order(2)
            @DisplayName("DELETE /organisations/{id}/letters/{letterId} – delete letter")
            void testDeleteLetter() {
                assertNotNull(createdLetterId, "Requires testCreateLetterForDeletion to have run");

                assertDoesNotThrow(() -> pingen.letters(orgId).delete(createdLetterId));
                System.out.println("Deleted letter: " + createdLetterId);
            }
        }
    }

// =========================================================================
// Batches
// =========================================================================

    @Nested
    @Order(3)
    @DisplayName("Batches")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class Batches {

        @Nested
        @Order(3)
        @DisplayName("Batches Happy Case")
        @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
        class BatchesHappyCaseTests {

            @Test
            @Order(1)
            @DisplayName("GET /organisations/{id}/batches – list all batches")
            void testListBatches() {
                PagedResponse<Batch> response = pingen.batches(orgId).getCollection();

                assertNotNull(response);
                System.out.printf("Batches – total: %s, items: %d%n", response.getTotal(), response.size());
            }

            @Test
            @Order(2)
            @DisplayName("GET /organisations/{id}/batches – paginated (page 1, limit 3)")
            void testListBatchesPaginated() {
                PagedResponse<Batch> response = pingen.batches(orgId).getCollection(CollectionParams.builder().page(1, 3).build());

                assertNotNull(response);
                System.out.printf("Batches paged – total: %s, page: %s, limit: %s, items: %d%n",
                        response.getTotal(), response.getCurrentPage(), response.getPageLimit(), response.size());
            }

            @Test
            @Order(3)
            @DisplayName("POST /organisations/{id}/batches – create batch")
            void testCreateBatch() {
                BatchCreateRequest request = BatchCreateRequest.builder()
                        .fileBytes(invoicePdfBytes)
                        .fileOriginalName(TestDocumentCreator.FILE_NAME)
                        .name("Integration Test Batch")
                        .icon(BatchIcon.DOCUMENT)
                        .addressPosition(AddressPosition.RIGHT)
                        .groupingType(GroupingType.MERGE)
                        .groupingOptionsSplitType(BatchGroupingSplitType.QR_INVOICE)
                        .groupingOptionsSplitSize(1)
                        .build();

                Resource<Batch> response = pingen.batches(orgId).create(request);

                assertNotNull(response.getId());

                createdBatchId = response.getId();
                System.out.printf("Created batch: %s (status: %s)%n",
                        createdBatchId, response.getAttributes().getStatus());
            }

            @Test
            @Order(4)
            @DisplayName("GET /organisations/{id}/batches/{batchId} – get single batch")
            void testGetBatchById() {
                assertNotNull(createdBatchId, "Requires testCreateBatch to have run");

                Resource<Batch> response = pingen.batches(orgId).get(createdBatchId).orElseThrow();

                assertEquals(createdBatchId, response.getId());
                assertNotNull(response.getAttributes().getStatus());
                System.out.println("Batch status: " + response.getAttributes().getStatus());
            }

            @Test
            @Order(5)
            @DisplayName("PATCH /organisations/{id}/batches/{batchId} – update batch name/icon")
            void testUpdateBatch() throws InterruptedException {
                assertNotNull(createdBatchId, "Requires testCreateBatch to have run");

                BatchUpdateRequest updateRequest = BatchUpdateRequest.builder()
                        .name("Updated Integration Batch")
                        .icon(BatchIcon.ROCKET)
                        .build();


                System.out.println("sleep 5 second so that batch will be in correct state for update");
                TimeUnit.SECONDS.sleep(5);

                pingen.batches(orgId).update(createdBatchId, updateRequest);
                System.out.println("Batch updated (HTTP 202, no content returned)");
            }

            @Test
            @Order(6)
            @DisplayName("GET /organisations/{id}/batches/{batchId}/events – get batch events")
            void testGetBatchEvents() {
                assertNotNull(createdBatchId, "Requires testCreateBatch to have run");

                PagedResponse<BatchEvent> response = pingen.batches(orgId).getEvents(createdBatchId);

                assertNotNull(response);
                System.out.printf("Batch events: %d%n", response.size());
                response.getItems().forEach(e ->
                        System.out.printf("  Event: code=%s, name=%s%n", e.getAttributes().getCode(), e.getAttributes().getName())
                );
            }

            @Test
            @Order(7)
            @DisplayName("GET /organisations/{id}/batches/{batchId}/statistics – get batch statistics")
            void testGetBatchStatistics() {
                assertNotNull(createdBatchId, "Requires testCreateBatch to have run");

                Resource<BatchStatistics> response = pingen.batches(orgId).getStatistics(createdBatchId);

                assertNotNull(response);
                System.out.printf("Batch statistics: letterValidating=%s, letterCountries=%s%n",
                        response.getAttributes() != null ? response.getAttributes().getLetterValidating() : "n/a",
                        response.getAttributes() != null ? response.getAttributes().getLetterCountries() : "n/a");
            }
        }

        @Nested
        @Order(4)
        @DisplayName("Batches Update Test")
        @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
        class BatchesCancelTests {

            @Test
            @Order(1)
            @DisplayName("POST /organisations/{id}/batches – create batch")
            void testCreateBatch() {
                BatchCreateRequest request = BatchCreateRequest.builder()
                        .fileBytes(invoicePdfBytes)
                        .fileOriginalName(TestDocumentCreator.FILE_NAME_CANCELLABLE)
                        .name("Integration Test Batch")
                        .icon(BatchIcon.DOCUMENT)
                        .addressPosition(AddressPosition.RIGHT)
                        .groupingType(GroupingType.MERGE)
                        .groupingOptionsSplitType(BatchGroupingSplitType.QR_INVOICE)
                        .groupingOptionsSplitSize(1)
                        .build();

                Resource<Batch> response = pingen.batches(orgId).create(request);

                assertNotNull(response.getId());

                createdBatchId = response.getId();
                System.out.printf("Created batch: %s (status: %s)%n",
                        createdBatchId, response.getAttributes().getStatus());
            }

            @Test
            @Order(2)
            @Disabled // TODO
            @DisplayName("PATCH /organisations/{id}/batches/{batchId}/cancel – cancel batch")
            void testCancelBatch() throws InterruptedException {
                assertNotNull(createdBatchId, "Requires testCreateBatch to have run");

                pingen.batches(orgId).cancel(createdBatchId);
            }

            @Test
            @Order(3)
            @DisplayName("DELETE /organisations/{id}/batches/{batchId} – delete batch")
            void testDeleteBatch() throws InterruptedException {
                assertNotNull(createdBatchId, "Requires testCreateBatch to have run");

                System.out.println("sleep 5 second so that batch will be in correct state for deletion");
                TimeUnit.SECONDS.sleep(5);

                assertDoesNotThrow(() -> pingen.batches(orgId).deleteWithLetters(createdBatchId));
                System.out.println("Deleted batch: " + createdBatchId);
            }
        }
    }

// =========================================================================
// Webhooks
// =========================================================================

    @Nested
    @Order(4)
    @DisplayName("Webhooks")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class WebhooksTests {

        @Test
        @Order(1)
        @DisplayName("GET /organisations/{id}/webhooks – list all webhooks")
        void testListWebhooks() {
            PagedResponse<Webhook> response = pingen.webhooks(orgId).getCollection();

            assertNotNull(response);
            System.out.printf("Webhooks – total: %s, items: %d%n", response.getTotal(), response.size());
        }

        @Test
        @Order(2)
        @DisplayName("POST /organisations/{id}/webhooks – create webhook")
        void testCreateWebhook() {
            WebhookCreateRequest request = WebhookCreateRequest.builder()
                    .url("https://httpbin.org/post")
                    .eventCategory(WebhookEventCategory.ISSUES)
                    .signingKey("integration-test-signing-key-32c")
                    .build();

            Resource<Webhook> response = pingen.webhooks(orgId).create(request);

            assertNotNull(response.getId());

            createdWebhookId = response.getId();
            System.out.printf("Created webhook: %s (url: %s)%n",
                    createdWebhookId, response.getAttributes().getUrl());
        }

        @Test
        @Order(3)
        @DisplayName("GET /organisations/{id}/webhooks/{webhookId} – get single webhook")
        void testGetWebhookById() {
            assertNotNull(createdWebhookId, "Requires testCreateWebhook to have run");

            Resource<Webhook> response = pingen.webhooks(orgId).get(createdWebhookId).orElseThrow();

            assertEquals(createdWebhookId, response.getId());
            assertNotNull(response.getAttributes().getUrl());
            System.out.println("Webhook url: " + response.getAttributes().getUrl());
        }

        @Test
        @Order(4)
        @DisplayName("DELETE /organisations/{id}/webhooks/{webhookId} – delete webhook")
        void testDeleteWebhook() {
            assertNotNull(createdWebhookId, "Requires testCreateWebhook to have run");

            assertDoesNotThrow(() -> pingen.webhooks(orgId).delete(createdWebhookId));
            System.out.println("Deleted webhook: " + createdWebhookId);
        }
    }

// =========================================================================
// Emails
// =========================================================================

    @Nested
    @Order(5)
    @DisplayName("Emails")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class EmailsTests {

        @Nested
        @Order(5)
        @DisplayName("Emails Happy Case")
        @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
        class EmailsHappyCaseTests {

            @Test
            @Order(1)
            @DisplayName("GET /organisations/{id}/deliveries/emails – list all emails")
            void testListEmails() {
                PagedResponse<Email> response = pingen.emails(orgId).getCollection();

                assertNotNull(response);
                System.out.printf("Emails – total: %s, items: %d%n", response.getTotal(), response.size());
            }

            @Test
            @Order(2)
            @DisplayName("POST /organisations/{id}/deliveries/emails – create email")
            void testCreateEmail() {
                EmailCreateRequest request = EmailCreateRequest.builder()
                        .fileBytes(invoicePdfBytes)
                        .fileOriginalName(TestDocumentCreator.FILE_NAME)
                        .autoSend(true)
                        .metaData(buildEmailMetaData())
                        .build();

                Resource<Email> response = pingen.emails(orgId).create(request);

                assertNotNull(response.getId());

                createdEmailId = response.getId();
                System.out.printf("Created email: %s (status: %s)%n",
                        createdEmailId, response.getAttributes().getStatus());
            }

            @Test
            @Order(3)
            @DisplayName("GET /organisations/{id}/deliveries/emails/{emailId} – get single email")
            void testGetEmailById() {
                Assumptions.assumeTrue(createdEmailId != null, "Requires testCreateEmail to have run");

                Resource<Email> response = pingen.emails(orgId).get(createdEmailId).orElseThrow();

                assertEquals(createdEmailId, response.getId());
                System.out.println("Email status: " + response.getAttributes().getStatus());
            }

            @Test
            @Order(4)
            @DisplayName("GET /organisations/{id}/deliveries/emails/{emailId}/events – get email events")
            void testGetEmailEvents() {
                Assumptions.assumeTrue(createdEmailId != null, "Requires testCreateEmail to have run");

                PagedResponse<DeliveryEvent> response = pingen.emails(orgId).getEvents(createdEmailId);

                assertNotNull(response);
                System.out.printf("Email events: %d%n", response.size());
            }

            @Test
            @Order(5)
            @DisplayName("GET /organisations/{id}/deliveries/emails/{emailId}/file – get email file URL")
            void testGetEmailFile() throws InterruptedException {
                Assumptions.assumeTrue(createdEmailId != null, "Requires testCreateEmail to have run");


                System.out.println("sleep 5 second so that email will be in correct state for getFile");
                TimeUnit.SECONDS.sleep(5);
                String fileUrl = pingen.emails(orgId).getFile(createdEmailId);

                assertNotNull(fileUrl, "File URL should not be null");
                assertFalse(fileUrl.isBlank(), "File URL should not be blank");
                System.out.println("Email file URL: " + fileUrl);
            }

        }

        @Nested
        @Order(6)
        @DisplayName("Emails Cancel Case")
        @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
        class EmailsCancelCaseTests {

            @Test
            @Order(1)
            @DisplayName("POST /organisations/{id}/deliveries/emails – create email")
            void testCreateEmail() {
                EmailCreateRequest request = EmailCreateRequest.builder()
                        .fileBytes(invoicePdfBytes)
                        .fileOriginalName(TestDocumentCreator.FILE_NAME_CANCELLABLE)
                        .autoSend(false)
                        .metaData(buildEmailMetaData())
                        .build();

                Resource<Email> response = pingen.emails(orgId).create(request);

                assertNotNull(response.getId());

                createdEmailId = response.getId();
                System.out.printf("Created email: %s (status: %s)%n",
                        createdEmailId, response.getAttributes().getStatus());
            }

            @Test
            @Order(2)
            @Disabled // TODO
            @DisplayName("PATCH /organisations/{id}/deliveries/emails/{emailId}/cancel – cancel email")
            void testCancelEmail() throws InterruptedException {
                assertNotNull(createdEmailId, "Requires testCreateSecondEmail to have run");

                System.out.println("sleep 10 second so that email will be in correct state for cancel");
                TimeUnit.SECONDS.sleep(10);
                pingen.emails(orgId).cancel(createdEmailId);
            }
        }

        @Nested
        @Order(7)
        @DisplayName("Emails Delete Case")
        @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
        class EmailsCancelDeleteTests {

            @Test
            @Order(1)
            @DisplayName("POST /organisations/{id}/deliveries/emails – create email")
            void testCreateEmail() {
                EmailCreateRequest request = EmailCreateRequest.builder()
                        .fileBytes(invoicePdfBytes)
                        .fileOriginalName(TestDocumentCreator.FILE_NAME)
                        .autoSend(false)
                        .metaData(buildEmailMetaData())
                        .build();

                Resource<Email> response = pingen.emails(orgId).create(request);

                assertNotNull(response.getId());

                createdEmailId = response.getId();
                System.out.printf("Created email: %s (status: %s)%n",
                        createdEmailId, response.getAttributes().getStatus());
            }

            @Test
            @Order(2)
            @DisplayName("DELETE /organisations/{id}/deliveries/emails/{emailId} – delete email")
            void testDeleteEmail() throws InterruptedException {
                Assumptions.assumeTrue(createdEmailId != null, "Requires testCreateEmail to have run");

                System.out.println("sleep 10 second so that email will be in correct state for deletion");
                TimeUnit.SECONDS.sleep(10);

                assertDoesNotThrow(() -> pingen.emails(orgId).delete(createdEmailId));
                System.out.println("Deleted email: " + createdEmailId);
            }
        }
    }

// =========================================================================
// E-Bills
// =========================================================================

    @Nested
    @Order(6)
    @DisplayName("E-Bills")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class EBillTests {

        @Nested
        @Order(6)
        @DisplayName("E-Bill Happy Case")
        @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
        class EBillHappyCaseTests {

            @Test
            @Order(1)
            @DisplayName("GET /organisations/{id}/deliveries/ebills – list all e-bills")
            void testListEBills() {
                PagedResponse<EBill> response = pingen.ebills(orgId).getCollection();

                assertNotNull(response);
                System.out.printf("E-Bills – total: %s, items: %d%n", response.getTotal(), response.size());
            }

            @Test
            @Order(2)
            @DisplayName("POST /organisations/{id}/deliveries/ebills – create e-bill")
            void testCreateEBill() {
                EBillCreateRequest request = EBillCreateRequest.builder()
                        .fileBytes(invoicePdfBytes)
                        .fileOriginalName(TestDocumentCreator.FILE_NAME)
                        .autoSend(false)
                        .metaData(buildEBillMetaData())
                        .build();

                Resource<EBill> response;
                try {
                    response = pingen.ebills(orgId).create(request);
                } catch (ApiException e) {
                    if (e.getResponseBody() != null && e.getResponseBody().contains("conflict_missing_configuration")) {
                        Assumptions.abort("E-Bill channel not configured – skipping");
                    }
                    throw e;
                }

                assertNotNull(response.getId());

                createdEBillId = response.getId();
                System.out.printf("Created e-bill: %s (status: %s)%n",
                        createdEBillId, response.getAttributes().getStatus());
            }

            @Test
            @Order(3)
            @DisplayName("GET /organisations/{id}/deliveries/ebills/{ebillId} – get single e-bill")
            void testGetEBillById() {
                Assumptions.assumeTrue(createdEBillId != null, "Requires testCreateEBill to have run");

                Resource<EBill> response = pingen.ebills(orgId).get(createdEBillId).orElseThrow();

                assertEquals(createdEBillId, response.getId());
                System.out.println("E-Bill status: " + response.getAttributes().getStatus());
            }

            @Test
            @Order(4)
            @DisplayName("GET /organisations/{id}/deliveries/ebills/{ebillId}/events – get e-bill events")
            void testGetEBillEvents() {
                Assumptions.assumeTrue(createdEBillId != null, "Requires testCreateEBill to have run");

                PagedResponse<DeliveryEvent> response = pingen.ebills(orgId).getEvents(createdEBillId);

                assertNotNull(response);
                System.out.printf("E-Bill events: %d%n", response.size());
            }

            @Test
            @Order(5)
            @DisplayName("GET /organisations/{id}/deliveries/ebills/{ebillId}/file – get e-bill file URL")
            void testGetEBillFile() throws InterruptedException {
                Assumptions.assumeTrue(createdEBillId != null, "Requires testCreateEBill to have run");

                System.out.println("sleep 10 second so that ebill will be in correct state for retrieval");
                TimeUnit.SECONDS.sleep(10);

                String fileUrl = pingen.ebills(orgId).getFile(createdEBillId);

                assertNotNull(fileUrl, "File URL should not be null");
                assertFalse(fileUrl.isBlank(), "File URL should not be blank");
                System.out.println("E-Bill file URL: " + fileUrl);
            }
        }

        @Nested
        @Order(7)
        @DisplayName("E-Bill Cancel Case")
        @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
        class EBillCancelCaseTests {

            @Test
            @Order(1)
            @DisplayName("POST /organisations/{id}/deliveries/ebills – create e-bill")
            void testCreateEBill() {
                EBillCreateRequest request = EBillCreateRequest.builder()
                        .fileBytes(invoicePdfBytes)
                        .fileOriginalName(TestDocumentCreator.FILE_NAME_CANCELLABLE)
                        .autoSend(false)
                        .metaData(buildEBillMetaData())
                        .build();

                Resource<EBill> response;
                try {
                    response = pingen.ebills(orgId).create(request);
                } catch (ApiException e) {
                    if (e.getResponseBody() != null && e.getResponseBody().contains("conflict_missing_configuration")) {
                        Assumptions.abort("E-Bill channel not configured – skipping");
                    }
                    throw e;
                }

                assertNotNull(response.getId());

                createdEBillId = response.getId();
                System.out.printf("Created e-bill: %s (status: %s)%n",
                        createdEBillId, response.getAttributes().getStatus());
            }

            @Test
            @Order(2)
            @DisplayName("PATCH /organisations/{id}/deliveries/ebills/{ebillId}/cancel – cancel e-bill")
            void testCancelEBill() throws InterruptedException {
                Assumptions.assumeTrue(createdEBillId != null, "Requires testCreateEBill to have run");

                System.out.println("sleep 10 second so that ebill will be in correct state for cancellation");
                TimeUnit.SECONDS.sleep(10);

                pingen.ebills(orgId).cancel(createdEBillId);
            }
        }

        @Nested
        @Order(8)
        @DisplayName("E-Bill Delete Case")
        @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
        class EBillDeleteCaseTests {

            @Test
            @Order(1)
            @DisplayName("POST /organisations/{id}/deliveries/ebills – create e-bill")
            void testCreateEBill() {
                EBillCreateRequest request = EBillCreateRequest.builder()
                        .fileBytes(invoicePdfBytes)
                        .fileOriginalName(TestDocumentCreator.FILE_NAME)
                        .autoSend(false)
                        .metaData(buildEBillMetaData())
                        .build();

                Resource<EBill> response;
                try {
                    response = pingen.ebills(orgId).create(request);
                } catch (ApiException e) {
                    if (e.getResponseBody() != null && e.getResponseBody().contains("conflict_missing_configuration")) {
                        Assumptions.abort("E-Bill channel not configured – skipping");
                    }
                    throw e;
                }

                assertNotNull(response.getId());

                createdEBillId = response.getId();
                System.out.printf("Created e-bill: %s (status: %s)%n",
                        createdEBillId, response.getAttributes().getStatus());
            }

            @Test
            @Order(2)
            @DisplayName("DELETE /organisations/{id}/deliveries/ebills/{ebillId} – delete e-bill")
            void testDeleteEBill() throws InterruptedException {
                Assumptions.assumeTrue(createdEBillId != null, "Requires testCreateEBill to have run");

                System.out.println("sleep 10 second so that ebill will be in correct state for deletion");
                TimeUnit.SECONDS.sleep(10);

                assertDoesNotThrow(() -> pingen.ebills(orgId).delete(createdEBillId));
                System.out.println("Deleted e-bill: " + createdEBillId);
            }
        }
    }

// =========================================================================
// User
// =========================================================================

    @Nested
    @Order(7)
    @DisplayName("User")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class UserTests {

        @Test
        @Order(1)
        @DisplayName("GET /user – get authenticated user")
        void testGetUser() {
            Resource<User> response = pingen.user().get();

            assertNotNull(response.getId());
            assertNotNull(response.getAttributes().getEmail());
            System.out.printf("User: %s %s (%s), status=%s%n",
                    response.getAttributes().getFirstName(),
                    response.getAttributes().getLastName(),
                    response.getAttributes().getEmail(),
                    response.getAttributes().getStatus());
        }

        @Test
        @Order(2)
        @DisplayName("GET /user/associations – get user organisation associations")
        void testGetUserAssociations() {
            PagedResponse<UserAssociation> response = pingen.user().getAssociations();

            assertNotNull(response);
            System.out.printf("User associations: %d%n", response.size());
            response.getItems().forEach(a ->
                    System.out.printf("  Association: role=%s, status=%s%n",
                            a.getAttributes().getRole(), a.getAttributes().getStatus())
            );
        }
    }
}
