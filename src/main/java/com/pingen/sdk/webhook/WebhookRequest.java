package com.pingen.sdk.webhook;

import java.util.Map;
import java.util.TreeMap;

/**
 * Represents an incoming HTTP request for webhook processing.
 * Framework-agnostic wrapper — populate from your servlet/framework's request object.
 */
public class WebhookRequest {

    private final String method;
    private final Map<String, String> headers;
    private final String body;

    public WebhookRequest(String method, Map<String, String> headers, String body) {
        this.method = method;
        // case-insensitive header lookup
        TreeMap<String, String> ci = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        if (headers != null) ci.putAll(headers);
        this.headers = ci;
        this.body = body != null ? body : "";
    }

    public String getMethod() {
        return method;
    }

    public String getHeader(String name) {
        return headers.get(name);
    }

    public String getBody() {
        return body;
    }
}
