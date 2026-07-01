package com.pingen.sdk.models.ebill;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Metadata required when creating an e-bill delivery.
 * invoice_number, invoice_date, invoice_due_date, and recipient_identifier are required.
 */
public class EBillMetaData {

    private final String invoiceNumber;
    private final String invoiceDate;
    private final String invoiceDueDate;
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

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("invoice_number", invoiceNumber);
        map.put("invoice_date", invoiceDate);
        map.put("invoice_due_date", invoiceDueDate);
        map.put("recipient_identifier", recipientIdentifier);
        return map;
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

        /**
         * Sets the invoice number (1–100 characters).
         */
        public Builder invoiceNumber(String invoiceNumber) {
            this.invoiceNumber = invoiceNumber;
            return this;
        }

        /**
         * Sets the invoice date in YYYY-MM-DD format.
         */
        public Builder invoiceDate(LocalDate invoiceDate) {
            return invoiceDate(invoiceDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
        }

        /**
         * Sets the invoice date in YYYY-MM-DD format.
         */
        public Builder invoiceDate(String invoiceDate) {
            this.invoiceDate = invoiceDate;
            return this;
        }

        /**
         * Sets the invoice due date in YYYY-MM-DD format.
         */
        public Builder invoiceDueDate(LocalDate invoiceDueDate) {
            return invoiceDueDate(invoiceDueDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
        }

        /**
         * Sets the invoice due date in YYYY-MM-DD format.
         */
        public Builder invoiceDueDate(String invoiceDueDate) {
            this.invoiceDueDate = invoiceDueDate;
            return this;
        }

        /**
         * Sets the recipient's e-bill identifier (Swiss e-bill participant ID).
         */
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
