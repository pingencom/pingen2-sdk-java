# pingen2-sdk-java
The official Java SDK for using the Pingen API

## Features

- OAuth2 client credentials authentication with automatic token refresh
- Full support for letters, batches, emails, e-bills, webhooks, organisations, and user
- Automatic 3-step file upload handling
- Flexible filtering, sorting, and pagination via `CollectionParams` and `Filter`
- Built on Java 17 with minimal runtime dependencies
- Thread-safe — a single `Pingen` instance can be shared across threads
- Typed exception hierarchy for authentication, validation, and rate-limit errors
- Production and staging environments

## Requirements

- Java 17 or higher
- Maven

## Installation

```xml
<dependency>
    <groupId>com.pingen.sdk</groupId>
    <artifactId>pingen2-sdk-java</artifactId>
    <version>0.1.0-SNAPSHOT</version>
</dependency>
```

## Quick Start

```java
import com.pingen.sdk.Pingen;
import com.pingen.sdk.models.letter.*;

Pingen pingen = Pingen.builder()
    .clientId("your-client-id")
    .clientSecret("your-client-secret")
    .build();

String orgId = pingen.organisations()
    .getCollection()
    .getItems()
    .get(0)
    .getId();

var letter = pingen.letters(orgId).create(
    LetterCreateRequest.builder()
        .filePath("invoice.pdf")
        .fileOriginalName("invoice.pdf")
        .addressPosition(AddressPosition.LEFT)
        .autoSend(true)
        .build()
);
System.out.println("Created letter: " + letter.getId());
```

## Authentication

The SDK uses the OAuth2 client credentials grant. Obtain your Client ID and Client Secret by creating a Developer App in your Pingen account (User profile → API Access).

### Environments

```java
// Production (default)
Pingen pingen = Pingen.builder()
    .clientId("your-client-id")
    .clientSecret("your-client-secret")
    .build();

// Staging — separate credentials required
Pingen pingen = Pingen.builder()
    .clientId("your-staging-client-id")
    .clientSecret("your-staging-client-secret")
    .staging()
    .build();
```

Staging requires a separate account at `https://identity-staging.pingen.com`. Letters sent in staging are simulated and never printed.

### Custom Timeouts

```java
Pingen pingen = Pingen.builder()
    .clientId("your-client-id")
    .clientSecret("your-client-secret")
    .connectTimeout(Duration.ofSeconds(10))
    .requestTimeout(Duration.ofMinutes(3))
    .build();
```

## Organisations

```java
// List all organisations
var orgs = pingen.organisations().getCollection();
for (var org : orgs.getItems()) {
    System.out.println(org.getId() + ": " + org.getAttributes().getName());
}

// Get a specific organisation
var org = pingen.organisations().get("org-uuid");
```

## Letters

### Create

Provide either a file path or raw bytes. The SDK handles the 3-step upload internally.

```java
// From a file path
var letter = pingen.letters(orgId).create(
    LetterCreateRequest.builder()
        .filePath("invoice.pdf")          // or Path.of(...)
        .fileOriginalName("invoice.pdf")
        .addressPosition(AddressPosition.LEFT)  // LEFT or RIGHT
        .autoSend(true)
        .build()
);

// From bytes
byte[] pdf = Files.readAllBytes(Path.of("document.pdf"));
var letter = pingen.letters(orgId).create(
    LetterCreateRequest.builder()
        .fileBytes(pdf)
        .fileOriginalName("document.pdf")
        .addressPosition(AddressPosition.RIGHT)
        .autoSend(false)
        .build()
);
```

Optional parameters on `LetterCreateRequest.Builder`:

| Method | Type | Description |
|---|---|---|
| `deliveryProduct(DeliveryProduct)` | enum | `FAST`, `CHEAP`, `BULK`, `PREMIUM`, `REGISTERED` |
| `printMode(PrintMode)` | enum | `SIMPLEX`, `DUPLEX` |
| `printSpectrum(PrintSpectrum)` | enum | `COLOR`, `GRAYSCALE` |
| `metaData(LetterMetaData)` | object | Programmatic recipient/sender address |
| `additionalAttribute(String, Object)` | key/value | Pass-through API attributes |

### MetaData (programmatic addresses)

```java
var meta = LetterMetaData.builder()
    .recipient(LetterMetaData.AddressEntry.builder()
        .name("Jane Doe")
        .street("Main Street")
        .number("1")
        .zip("8000")
        .city("Zurich")
        .country("CH")
        .build())
    .sender(LetterMetaData.AddressEntry.builder()
        .name("Acme AG")
        .street("Business Road")
        .number("42")
        .zip("3000")
        .city("Bern")
        .country("CH")
        .build())
    .build();
```

### Send (manual)

When `autoSend` is `false`, send the letter explicitly:

```java
var sent = pingen.letters(orgId).send(
    "letter-uuid",
    LetterSendRequest.builder()
        .deliveryProduct(DeliveryProduct.CHEAP)
        .printMode(PrintMode.SIMPLEX)
        .printSpectrum(PrintSpectrum.GRAYSCALE)
        .build()
);
```

### Other operations

```java
// Retrieve
var letter = pingen.letters(orgId).get("letter-uuid");  // Optional<Resource<Letter>>

// Cancel (only possible before submission)
pingen.letters(orgId).cancel("letter-uuid");

// Delete
pingen.letters(orgId).delete("letter-uuid");

// Download the original PDF
String url = pingen.letters(orgId).getFile("letter-uuid");

// Lifecycle events for a specific letter
var events = pingen.letters(orgId).getEvents("letter-uuid");

// Scan image for a specific event
String imageUrl = pingen.letters(orgId).getEventImage("letter-uuid", "event-uuid");
```

### Price calculator

```java
var result = pingen.letters(orgId).calculatePrice(
    LetterPriceCalculatorRequest.builder()
        .country("CH")
        .paperTypes(List.of("normal"))
        .printMode(PrintMode.SIMPLEX)
        .printSpectrum(PrintSpectrum.GRAYSCALE)
        .deliveryProduct(DeliveryProduct.CHEAP)
        .build()
);
System.out.println(result.getAttributes().getPrice() + " " + result.getAttributes().getCurrency());
```

### Global delivery event feeds

```java
// Letters that were delivered / sent / had issues / became undeliverable
var delivered    = pingen.letters(orgId).getDeliveredEvents();
var sent         = pingen.letters(orgId).getSentEvents();
var issues       = pingen.letters(orgId).getIssueEvents();
var undelivered  = pingen.letters(orgId).getUndeliverableEvents();
```

## Batches

Batches let you upload many letters at once in a ZIP or merged PDF.

### Create

```java
var batch = pingen.batches(orgId).create(
    BatchCreateRequest.builder()
        .filePath("monthly-invoices.zip")
        .fileOriginalName("monthly-invoices.zip")
        .name("Monthly Invoices June")
        .icon(BatchIcon.RECEIPT)
        .addressPosition(AddressPosition.LEFT)
        .groupingType(GroupingType.ZIP)
        .groupingOptionsSplitType(BatchGroupingSplitType.FILE)
        .build()
);
```

For merged PDFs, choose a split strategy:

```java
BatchCreateRequest.builder()
    ...
    .groupingType(GroupingType.MERGE)
    .groupingOptionsSplitType(BatchGroupingSplitType.PAGE)
    .groupingOptionsSplitSize(2)                              // 2 pages per letter
    .groupingOptionsSplitPosition(BatchGroupingSplitPosition.FIRST_PAGE)
    .build();
```

`BatchGroupingSplitType` values: `FILE`, `PAGE`, `CUSTOM`, `QR_INVOICE`

### Send

```java
var sent = pingen.batches(orgId).send(
    "batch-uuid",
    BatchSendRequest.builder()
        .addDeliveryProduct("CH", DeliveryProduct.CHEAP)
        .addDeliveryProduct("DE", DeliveryProduct.FAST)
        .printMode(PrintMode.SIMPLEX)
        .printSpectrum(PrintSpectrum.GRAYSCALE)
        .build()
);
```

### Other operations

```java
// Retrieve
var batch = pingen.batches(orgId).get("batch-uuid");  // Optional<Resource<Batch>>

// Update name/icon
pingen.batches(orgId).update("batch-uuid",
    BatchUpdateRequest.builder()
        .name("New Name")
        .icon(BatchIcon.CAMPAIGN)
        .build()
);

// Cancel
pingen.batches(orgId).cancel("batch-uuid");

// Delete (batch only, keep letters)
pingen.batches(orgId).deleteWithoutLetters("batch-uuid");

// Delete batch and all its letters
pingen.batches(orgId).deleteWithLetters("batch-uuid");

// Lifecycle events
var events = pingen.batches(orgId).getEvents("batch-uuid");

// Statistics (letter counts by country, region, group)
var stats = pingen.batches(orgId).getStatistics("batch-uuid");
```

## Emails

Send PDF documents as emails. The recipient address is specified in `EmailMetaData`.

```java
var email = pingen.emails(orgId).create(
    EmailCreateRequest.builder()
        .filePath("document.pdf")
        .fileOriginalName("document.pdf")
        .autoSend(true)
        .metaData(
            EmailMetaData.builder()
                .senderName("Acme AG")
                .recipientEmail("jane.doe@example.com")
                .recipientName("Jane Doe")
                .replyEmail("noreply@acme.com")
                .replyName("Acme AG")
                .subject("Your invoice")
                .content("Please find your invoice attached.")
                .build()
        )
        .build()
);

// Cancel
pingen.emails(orgId).cancel("email-uuid");

// Delete
pingen.emails(orgId).delete("email-uuid");

// Download
String url = pingen.emails(orgId).getFile("email-uuid");

// Events
var events = pingen.emails(orgId).getEvents("email-uuid");
```

## E-Bills

Send Swiss e-bills (SIX e-invoicing network). The invoice metadata is required.

```java
var ebill = pingen.ebills(orgId).create(
    EBillCreateRequest.builder()
        .filePath("invoice.pdf")
        .fileOriginalName("invoice.pdf")
        .autoSend(false)
        .metaData(
            EBillMetaData.builder()
                .invoiceNumber("INV-2024-001")
                .invoiceDate(LocalDate.of(2024, 6, 1))
                .invoiceDueDate(LocalDate.of(2024, 6, 30))
                .recipientIdentifier("41010560425610173")   // Swiss e-bill participant ID
                .build()
        )
        .build()
);

// Send (when autoSend is false)
pingen.ebills(orgId).send("ebill-uuid");

// Cancel / delete
pingen.ebills(orgId).cancel("ebill-uuid");
pingen.ebills(orgId).delete("ebill-uuid");

// Download
String url = pingen.ebills(orgId).getFile("ebill-uuid");

// Events and event images
var events = pingen.ebills(orgId).getEvents("ebill-uuid");
String imageUrl = pingen.ebills(orgId).getEventImage("ebill-uuid", "event-uuid");
```

## Webhooks

```java
var webhook = pingen.webhooks(orgId).create(
    WebhookCreateRequest.builder()
        .url("https://your-domain.com/webhooks/pingen")
        .eventCategory(WebhookEventCategory.DELIVERED)
        .signingKey("your-20-to-32-char-key")
        .build()
);

// Available event categories
// WebhookEventCategory.ISSUES
// WebhookEventCategory.SENT
// WebhookEventCategory.UNDELIVERABLE
// WebhookEventCategory.DELIVERED
// WebhookEventCategory.CHANNEL_SUBSCRIPTIONS

var webhooks = pingen.webhooks(orgId).getCollection();
var wh = pingen.webhooks(orgId).get("webhook-uuid");
pingen.webhooks(orgId).delete("webhook-uuid");
```

## User

```java
// Authenticated user's profile
var user = pingen.user().get();
System.out.println(user.getAttributes().getEmail());

// Organisation associations (role per org)
var associations = pingen.user().getAssociations();
```

## Pagination, Filtering & Sorting

All collection endpoints accept a `CollectionParams` object.

```java
import com.pingen.sdk.models.common.CollectionParams;
import com.pingen.sdk.models.common.Filter;

// Pagination
CollectionParams params = CollectionParams.builder()
    .page(2, 50)           // page number, items per page
    .build();

// Sorting
CollectionParams params = CollectionParams.builder()
    .sort("created_at")    // ascending
    .sortDesc("updated_at") // descending; multiple calls accumulate
    .build();

// Full-text search
CollectionParams params = CollectionParams.builder()
    .search("invoice")
    .build();

// Filtering
CollectionParams params = CollectionParams.builder()
    .filter(Filter.eq("status", "sent"))
    .build();

var letters = pingen.letters(orgId).getCollection(params);
```

### Filter expressions

```java
// Simple equality
Filter.eq("status", "sent")

// Comparators
Filter.gt("created_at", "2024-01-01")
Filter.lt("price_value", 5)
Filter.gte("price_value", 1)
Filter.lte("price_value", 10)
Filter.notEq("status", "cancelled")
Filter.approx("address", "Zurich")

// Logical combinations
Filter.and(Filter.eq("status", "sent"), Filter.gt("created_at", "2024-01-01"))
Filter.or(Filter.eq("status", "sent"), Filter.eq("status", "delivered"))
```

### Iterating through pages

```java
int page = 1;
PagedResponse<Letter> response;
do {
    response = pingen.letters(orgId).getCollection(
        CollectionParams.builder().page(page, 20).build()
    );
    for (var item : response.getItems()) {
        process(item);
    }
    page++;
} while (response.hasNext());

// Pagination metadata
response.getTotal();        // total items across all pages
response.getCurrentPage();  // current page number
response.getLastPage();     // last page number
response.getPageLimit();    // items per page
response.hasNext();
response.hasPrev();
```

## Error Handling

```java
try {
    var letter = pingen.letters(orgId).create(request);
} catch (AuthenticationException e) {
    // HTTP 401 — invalid or expired credentials
    System.err.println("Auth failed: " + e.getMessage());
    System.err.println("Request ID: " + e.getRequestId());
} catch (ValidationException e) {
    // HTTP 422 — request payload rejected by the API
    System.err.println("Validation error: " + e.getMessage());
    System.err.println("Response body: " + e.getResponseBody());
} catch (RateLimitException e) {
    // HTTP 429 — too many requests
    System.err.println("Rate limited. Retry after: " + e.getRetryAfter() + "s");
    System.err.println("Limit resets at: " + e.getRateLimitReset());
} catch (ApiException e) {
    // Other HTTP error (4xx / 5xx)
    System.err.println("API error " + e.getStatusCode() + ": " + e.getMessage());
    System.err.println("Request ID: " + e.getRequestId());
} catch (PingenException e) {
    // Network error, interrupted request, or JSON parse failure
    System.err.println("SDK error: " + e.getMessage());
}
```

All exceptions carry an `X-Request-Id` header value (via `getRequestId()`) that you can include in support requests.

## Staging Simulation

In the staging environment, the lifecycle of a letter is determined by the `fileOriginalName`:

| Filename pattern | Simulated outcome |
|---|---|
| `*_simulate_undeliverable.*` | Letter becomes undeliverable |
| `*_simulate_unprintable.*` | Letter is rejected by the print centre |
| `*_simulate_cancellable.*` | Letter stops in a cancellable state |
| Any other name | Letter is delivered successfully |

## Logging

The SDK uses SLF4J for debug-level logging (token acquisition, request URLs, HTTP status codes). Add any SLF4J-compatible implementation to your project — no specific one is required.

```xml
<!-- Example: Logback -->
<dependency>
    <groupId>ch.qos.logback</groupId>
    <artifactId>logback-classic</artifactId>
    <version>1.5.6</version>
</dependency>
```

## Building from Source

```bash
git clone https://github.com/pingencom/pingen2-sdk-java.git
cd pingen2-sdk-java

# Build and run unit tests
mvn clean install

# Run integration tests (requires credentials in environment)
mvn test -DexcludedGroups="" -Dgroups=integration

# Generate Javadoc
mvn javadoc:javadoc
```

## Runtime Dependencies

| Dependency                         | Purpose |
|------------------------------------|---|
| `jackson-databind`                 | JSON serialisation / deserialisation |
| `jackson-datatype-jsr310`          | Java 8 date/time support for Jackson |
| `slf4j-api`                        | Logging façade (no implementation bundled) |
