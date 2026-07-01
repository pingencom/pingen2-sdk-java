package com.pingen.sdk.models.ebill;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pingen.sdk.models.common.DeliverySource;

import java.time.OffsetDateTime;

/**
 * Represents an e-bill delivery in the Pingen system.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class EBill {

    @JsonProperty("status")
    private String status;

    @JsonProperty("file_original_name")
    private String fileOriginalName;

    @JsonProperty("file_pages")
    private Integer filePages;

    @JsonProperty("recipient_identifier")
    private String recipientIdentifier;

    @JsonProperty("recipient_address")
    private String recipientAddress;

    @JsonProperty("invoice_number")
    private String invoiceNumber;

    @JsonProperty("invoice_date")
    private String invoiceDate;

    @JsonProperty("invoice_due_date")
    private String invoiceDueDate;

    @JsonProperty("invoice_value")
    private Double invoiceValue;

    @JsonProperty("invoice_currency")
    private String invoiceCurrency;

    @JsonProperty("invoice_iban")
    private String invoiceIban;

    @JsonProperty("invoice_address")
    private String invoiceAddress;

    @JsonProperty("invoice_reference")
    private String invoiceReference;

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

    public EBill() {
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

    public String getRecipientAddress() {
        return recipientAddress;
    }

    public void setRecipientAddress(String recipientAddress) {
        this.recipientAddress = recipientAddress;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(String invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getInvoiceDueDate() {
        return invoiceDueDate;
    }

    public void setInvoiceDueDate(String invoiceDueDate) {
        this.invoiceDueDate = invoiceDueDate;
    }

    public Double getInvoiceValue() {
        return invoiceValue;
    }

    public void setInvoiceValue(Double invoiceValue) {
        this.invoiceValue = invoiceValue;
    }

    public String getInvoiceCurrency() {
        return invoiceCurrency;
    }

    public void setInvoiceCurrency(String invoiceCurrency) {
        this.invoiceCurrency = invoiceCurrency;
    }

    public String getInvoiceIban() {
        return invoiceIban;
    }

    public void setInvoiceIban(String invoiceIban) {
        this.invoiceIban = invoiceIban;
    }

    public String getInvoiceAddress() {
        return invoiceAddress;
    }

    public void setInvoiceAddress(String invoiceAddress) {
        this.invoiceAddress = invoiceAddress;
    }

    public String getInvoiceReference() {
        return invoiceReference;
    }

    public void setInvoiceReference(String invoiceReference) {
        this.invoiceReference = invoiceReference;
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
