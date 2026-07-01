package com.pingen.sdk.webhook;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pingen.sdk.exception.WebhookSignatureException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/**
 * Verifies and processes incoming Pingen webhook requests.
 *
 * <p>Usage:
 * <pre>
 *   IncomingWebhook hook = new IncomingWebhook(signingKey);
 *   IncomingWebhookDetails details = hook.processWebhook(request);
 * </pre>
 */
public class IncomingWebhook {

    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final byte[] secretBytes;

    public IncomingWebhook(String secret) {
        this.secretBytes = secret.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Verifies the request and returns the parsed webhook payload.
     *
     * @param request the incoming request
     * @return parsed webhook details
     * @throws WebhookSignatureException if the request method, signature header, or HMAC is invalid
     */
    public IncomingWebhookDetails processWebhook(WebhookRequest request) {
        verify(request);
        try {
            return MAPPER.readValue(request.getBody(), IncomingWebhookDetails.class);
        } catch (Exception e) {
            throw new WebhookSignatureException("Invalid webhook payload: " + e.getMessage());
        }
    }

    private void verify(WebhookRequest request) {
        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            throw new WebhookSignatureException("Only POST requests are allowed.");
        }

        String signature = request.getHeader("Signature");
        if (signature == null) {
            throw new WebhookSignatureException("Signature missing.");
        }

        String computed = hmacSha256Hex(request.getBody());
        if (!MessageDigest.isEqual(
                computed.getBytes(StandardCharsets.UTF_8),
                signature.getBytes(StandardCharsets.UTF_8))) {
            throw new WebhookSignatureException("Webhook signature matching failed.");
        }
    }

    private String hmacSha256Hex(String data) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(secretBytes, HMAC_ALGORITHM));
            byte[] hmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hmac);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new IllegalStateException("HMAC-SHA256 unavailable", e);
        }
    }
}
