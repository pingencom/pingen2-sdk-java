package com.pingen.sdk.models.batch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pingen.sdk.models.letter.AddressPosition;
import com.pingen.sdk.models.letter.PrintMode;
import com.pingen.sdk.models.letter.PrintSpectrum;

import java.time.OffsetDateTime;

/**
 * Represents a batch of letters in the Pingen system.
 * Batches allow uploading multiple letters in a single ZIP file or merged PDF.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Batch {

    @JsonProperty("name")
    private String name;

    @JsonProperty("icon")
    private BatchIcon icon;

    @JsonProperty("file_original_name")
    private String fileOriginalName;

    @JsonProperty("address_position")
    private AddressPosition addressPosition;

    @JsonProperty("status")
    private String status;

    @JsonProperty("created_at")
    private OffsetDateTime createdAt;

    @JsonProperty("updated_at")
    private OffsetDateTime updatedAt;

    @JsonProperty("submitted_at")
    private OffsetDateTime submittedAt;

    @JsonProperty("letter_count")
    private Integer letterCount;

    @JsonProperty("price_currency")
    private String priceCurrency;

    @JsonProperty("price_value")
    private Double priceValue;

    @JsonProperty("print_mode")
    private PrintMode printMode;

    @JsonProperty("print_spectrum")
    private PrintSpectrum printSpectrum;

    @JsonProperty("source")
    private BatchSource source;

    public Batch() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BatchIcon getIcon() {
        return icon;
    }

    public void setIcon(BatchIcon icon) {
        this.icon = icon;
    }

    public String getFileOriginalName() {
        return fileOriginalName;
    }

    public void setFileOriginalName(String fileOriginalName) {
        this.fileOriginalName = fileOriginalName;
    }

    public AddressPosition getAddressPosition() {
        return addressPosition;
    }

    public void setAddressPosition(AddressPosition addressPosition) {
        this.addressPosition = addressPosition;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public OffsetDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(OffsetDateTime submittedAt) { this.submittedAt = submittedAt; }

    public Integer getLetterCount() { return letterCount; }
    public void setLetterCount(Integer letterCount) { this.letterCount = letterCount; }

    public String getPriceCurrency() { return priceCurrency; }
    public void setPriceCurrency(String priceCurrency) { this.priceCurrency = priceCurrency; }

    public Double getPriceValue() { return priceValue; }
    public void setPriceValue(Double priceValue) { this.priceValue = priceValue; }

    public PrintMode getPrintMode() { return printMode; }
    public void setPrintMode(PrintMode printMode) { this.printMode = printMode; }

    public PrintSpectrum getPrintSpectrum() { return printSpectrum; }
    public void setPrintSpectrum(PrintSpectrum printSpectrum) { this.printSpectrum = printSpectrum; }

    public BatchSource getSource() { return source; }
    public void setSource(BatchSource source) { this.source = source; }
}
