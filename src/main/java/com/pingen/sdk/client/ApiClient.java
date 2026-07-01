package com.pingen.sdk.client;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pingen.sdk.PingenConfig;
import com.pingen.sdk.exception.ApiException;
import com.pingen.sdk.exception.AuthenticationException;
import com.pingen.sdk.exception.PingenException;
import com.pingen.sdk.exception.RateLimitException;
import com.pingen.sdk.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * HTTP client for making requests to the Pingen API.
 * This class is thread-safe and can be shared across multiple threads.
 */
public class ApiClient {

    private static final Logger log = LoggerFactory.getLogger(ApiClient.class);
    private static final String JSON_API_CONTENT_TYPE = "application/vnd.api+json";
    private static final String JSON_CONTENT_TYPE = "application/json";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final PingenConfig config;

    public ApiClient(PingenConfig config) {
        this.config = config;
        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(config.getConnectTimeout())
                .build();

        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        this.objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    /**
     * Executes an API request and returns the response.
     *
     * @param request the API request to execute
     * @return the API response
     * @throws PingenException if the request fails
     */
    public ApiResponse<String> execute(ApiRequest request) {
        try {
            HttpRequest httpRequest = buildHttpRequest(request);

            log.debug("Executing {} request to {}", request.getMethod(), request.getFullUrl());

            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            Map<String, String> headers = extractHeaders(httpResponse);
            ApiResponse<String> response = new ApiResponse<>(
                    httpResponse.statusCode(),
                    httpResponse.body(),
                    headers
            );

            log.debug("Received response with status {}", httpResponse.statusCode());

            if (!response.isSuccessful()) {
                handleErrorResponse(response);
            }

            return response;
        } catch (IOException e) {
            throw new PingenException("Network error occurred: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new PingenException("Request was interrupted: " + e.getMessage(), e);
        }
    }

    /**
     * Executes an API request and deserializes the response to the specified type.
     *
     * @param request the API request to execute
     * @param responseType the class type to deserialize the response to
     * @param <T> the response type
     * @return the API response with deserialized body
     * @throws PingenException if the request fails or deserialization fails
     */
    public <T> ApiResponse<T> execute(ApiRequest request, Class<T> responseType) {
        ApiResponse<String> stringResponse = execute(request);

        if (stringResponse.getBody() == null || stringResponse.getBody().isEmpty()) {
            return new ApiResponse<>(
                    stringResponse.getStatusCode(),
                    null,
                    stringResponse.getHeaders()
            );
        }

        try {
            T body = objectMapper.readValue(stringResponse.getBody(), responseType);
            return new ApiResponse<>(
                    stringResponse.getStatusCode(),
                    body,
                    stringResponse.getHeaders()
            );
        } catch (JsonProcessingException e) {
            throw new PingenException("Failed to parse response: " + e.getMessage(), e);
        }
    }

    /**
     * Executes an API request and deserializes the response to the specified type using TypeReference.
     * This is useful for generic types like JsonApiCollection<Letter>.
     *
     * @param request the API request to execute
     * @param typeReference the type reference to deserialize the response to
     * @param <T> the response type
     * @return the API response with deserialized body
     * @throws PingenException if the request fails or deserialization fails
     */
    public <T> ApiResponse<T> execute(ApiRequest request, TypeReference<T> typeReference) {
        ApiResponse<String> stringResponse = execute(request);

        if (stringResponse.getBody() == null || stringResponse.getBody().isEmpty()) {
            return new ApiResponse<>(
                    stringResponse.getStatusCode(),
                    null,
                    stringResponse.getHeaders()
            );
        }

        try {
            T body = objectMapper.readValue(stringResponse.getBody(), typeReference);
            return new ApiResponse<>(
                    stringResponse.getStatusCode(),
                    body,
                    stringResponse.getHeaders()
            );
        } catch (JsonProcessingException e) {
            throw new PingenException("Failed to parse response: " + e.getMessage(), e);
        }
    }

    /**
     * Serializes an object to JSON.
     *
     * @param object the object to serialize
     * @return the JSON string
     * @throws PingenException if serialization fails
     */
    public String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new PingenException("Failed to serialize object to JSON: " + e.getMessage(), e);
        }
    }

    /**
     * Deserializes JSON to an object.
     *
     * @param json the JSON string
     * @param type the class type to deserialize to
     * @param <T> the type
     * @return the deserialized object
     * @throws PingenException if deserialization fails
     */
    public <T> T fromJson(String json, Class<T> type) {
        try {
            return objectMapper.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new PingenException("Failed to deserialize JSON: " + e.getMessage(), e);
        }
    }

    /**
     * Gets the ObjectMapper instance for custom serialization needs.
     *
     * @return the ObjectMapper
     */
    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    /**
     * Gets the configuration for this client.
     *
     * @return the PingenConfig
     */
    public PingenConfig getConfig() {
        return config;
    }

    /**
     * Executes a request that responds with a 302 redirect and returns the Location URL.
     * Used for file download endpoints that redirect to pre-signed storage URLs.
     *
     * @param request the API request to execute
     * @return the redirect URL from the Location header
     * @throws PingenException if the request fails
     */
    public String executeForFileUrl(ApiRequest request) {
        try {
            HttpRequest httpRequest = buildHttpRequest(request);
            log.debug("Executing {} request to {} (expecting redirect)", request.getMethod(), request.getFullUrl());

            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            int status = httpResponse.statusCode();

            if (status >= 300 && status < 400) {
                return httpResponse.headers().firstValue("location")
                        .orElseThrow(() -> new PingenException("Redirect response missing Location header"));
            }

            if (status >= 200 && status < 300) {
                return httpResponse.body();
            }

            Map<String, String> headers = extractHeaders(httpResponse);
            handleErrorResponse(new ApiResponse<>(status, httpResponse.body(), headers));
            return null; // unreachable
        } catch (IOException e) {
            throw new PingenException("Network error occurred: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new PingenException("Request was interrupted: " + e.getMessage(), e);
        }
    }

    /**
     * URL encodes a string parameter.
     *
     * @param value the value to encode
     * @return the URL encoded value
     */
    public static String urlEncode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private static final String USER_AGENT = "PINGEN.SDK.JAVA";

    private HttpRequest buildHttpRequest(ApiRequest request) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(request.getFullUrl()))
                .timeout(config.getRequestTimeout())
                .header("User-Agent", USER_AGENT);

        // Add headers (may override User-Agent if explicitly set in the request)
        for (Map.Entry<String, String> header : request.getHeaders().entrySet()) {
            builder.header(header.getKey(), header.getValue());
        }

        // Set body publisher based on method and body type
        HttpRequest.BodyPublisher bodyPublisher;
        if (request.hasBinaryBody()) {
            bodyPublisher = HttpRequest.BodyPublishers.ofByteArray(request.getBinaryBody());
        } else if (request.getBody() != null) {
            bodyPublisher = HttpRequest.BodyPublishers.ofString(request.getBody());
        } else {
            bodyPublisher = HttpRequest.BodyPublishers.noBody();
        }

        // Set HTTP method
        switch (request.getMethod()) {
            case GET:
                builder.GET();
                break;
            case POST:
                builder.POST(bodyPublisher);
                break;
            case PATCH:
                builder.method("PATCH", bodyPublisher);
                break;
            case DELETE:
                // Use the generic method() call so a body can be sent when required
                builder.method("DELETE", bodyPublisher);
                break;
            case PUT:
                builder.PUT(bodyPublisher);
                break;
        }

        return builder.build();
    }

    private Map<String, String> extractHeaders(HttpResponse<String> httpResponse) {
        Map<String, String> headers = new HashMap<>();
        httpResponse.headers().map().forEach((name, values) -> {
            if (!values.isEmpty()) {
                headers.put(name.toLowerCase(), values.get(0));
            }
        });
        return headers;
    }

    private void handleErrorResponse(ApiResponse<String> response) {
        String requestId = response.getRequestId();
        int statusCode = response.getStatusCode();
        String body = response.getBody();

        String errorMessage = extractErrorMessage(body, statusCode);

        switch (statusCode) {
            case 401:
                throw new AuthenticationException(errorMessage, body, requestId);
            case 422:
                throw new ValidationException(errorMessage, body, requestId);
            case 429:
                Integer retryAfter = response.getRetryAfter();
                Long rateLimitReset = response.getRateLimitReset();
                throw new RateLimitException(errorMessage, body, requestId, retryAfter, rateLimitReset);
            default:
                throw new ApiException(statusCode, errorMessage, body, requestId);
        }
    }

    private String extractErrorMessage(String body, int statusCode) {
        if (body == null || body.isEmpty()) {
            return "API request failed with status " + statusCode;
        }

        try {
            // Try to parse JSON:API error format
            Map<?, ?> parsed = objectMapper.readValue(body, Map.class);
            if (parsed.containsKey("errors")) {
                Object errors = parsed.get("errors");
                if (errors instanceof java.util.List) {
                    java.util.List<?> errorList = (java.util.List<?>) errors;
                    if (!errorList.isEmpty() && errorList.get(0) instanceof Map) {
                        Map<?, ?> firstError = (Map<?, ?>) errorList.get(0);
                        if (firstError.containsKey("detail")) {
                            return firstError.get("detail").toString();
                        }
                        if (firstError.containsKey("title")) {
                            return firstError.get("title").toString();
                        }
                    }
                }
            }
            if (parsed.containsKey("error")) {
                return parsed.get("error").toString();
            }
            if (parsed.containsKey("message")) {
                return parsed.get("message").toString();
            }
        } catch (Exception e) {
            log.debug("Could not parse error response as JSON", e);
        }

        return "API request failed with status " + statusCode + ": " + body;
    }
}
