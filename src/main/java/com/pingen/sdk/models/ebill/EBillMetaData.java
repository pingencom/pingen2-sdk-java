package com.pingen.sdk.models.ebill;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Metadata required when creating an e-bill delivery.
 * invoice_number, invoice_date, invoice_due_date, and recipient_identifier are required.
 */
public class EBillMetaData {

    @JsonProperty("invoice_number")
    private final String invoiceNumber;

    @JsonProperty("invoice_date")
    private final String invoiceDate;

    @JsonProperty("invoice_due_date")
    private final String invoiceDueDate;

    @JsonProperty("recipient_identifier")
    private final String recipientIdentifier;

    private EBillMetaData(Builder builder) {
        this.invoiceNumber = builder.invoiceNumber;
        this.invoiceDate = builder.invoiceDate;
        this.invoiceDueDate = builder.invoiceDueDate;
        this.recipientIdentifier = builder.recipientIdentifier;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public String getInvoiceDate() {
        return invoiceDate;
    }

    public String getInvoiceDueDate() {
        return invoiceDueDate;
    }

    public String getRecipientIdentifier() {
        return recipientIdentifier;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String invoiceNumber;
        private String invoiceDate;
        private String invoiceDueDate;
        private String recipientIdentifier;

        private Builder() {
        }

        public Builder invoiceNumber(String invoiceNumber) {
            this.invoiceNumber = invoiceNumber;
            return this;
        }

        public Builder invoiceDate(LocalDate invoiceDate) {
            return invoiceDate(invoiceDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
        }

        public Builder invoiceDate(String invoiceDate) {
            this.invoiceDate = invoiceDate;
            return this;
        }

        public Builder invoiceDueDate(LocalDate invoiceDueDate) {
            return invoiceDueDate(invoiceDueDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
        }

        public Builder invoiceDueDate(String invoiceDueDate) {
            this.invoiceDueDate = invoiceDueDate;
            return this;
        }

        public Builder recipientIdentifier(String recipientIdentifier) {
            this.recipientIdentifier = recipientIdentifier;
            return this;
        }

        public EBillMetaData build() {
            if (invoiceNumber == null) throw new IllegalArgumentException("invoiceNumber is required");
            if (invoiceDate == null) throw new IllegalArgumentException("invoiceDate is required");
            if (invoiceDueDate == null) throw new IllegalArgumentException("invoiceDueDate is required");
            if (recipientIdentifier == null) throw new IllegalArgumentException("recipientIdentifier is required");
            return new EBillMetaData(this);
        }
    }
}
