package com.pingen.sdk.models.email;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pingen.sdk.models.common.DeliverySource;

import java.time.OffsetDateTime;

/**
 * Represents an email delivery in the Pingen system.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Email {

    @JsonProperty("status")
    private String status;

    @JsonProperty("file_original_name")
    private String fileOriginalName;

    @JsonProperty("file_pages")
    private Integer filePages;

    @JsonProperty("recipient_identifier")
    private String recipientIdentifier;

    @JsonProperty("price_currency")
    private String priceCurrency;

    @JsonProperty("price_value")
    private Double priceValue;

    @JsonProperty("source")
    private DeliverySource source;

    @JsonProperty("submitted_at")
    private OffsetDateTime submittedAt;

    @JsonProperty("created_at")
    private OffsetDateTime createdAt;

    @JsonProperty("updated_at")
    private OffsetDateTime updatedAt;

    public Email() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFileOriginalName() {
        return fileOriginalName;
    }

    public void setFileOriginalName(String fileOriginalName) {
        this.fileOriginalName = fileOriginalName;
    }

    public Integer getFilePages() {
        return filePages;
    }

    public void setFilePages(Integer filePages) {
        this.filePages = filePages;
    }

    public String getRecipientIdentifier() {
        return recipientIdentifier;
    }

    public void setRecipientIdentifier(String recipientIdentifier) {
        this.recipientIdentifier = recipientIdentifier;
    }

    public String getPriceCurrency() {
        return priceCurrency;
    }

    public void setPriceCurrency(String priceCurrency) {
        this.priceCurrency = priceCurrency;
    }

    public Double getPriceValue() {
        return priceValue;
    }

    public void setPriceValue(Double priceValue) {
        this.priceValue = priceValue;
    }

    public DeliverySource getSource() {
        return source;
    }

    public void setSource(DeliverySource source) {
        this.source = source;
    }

    public OffsetDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(OffsetDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
