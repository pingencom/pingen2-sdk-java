package com.pingen.sdk.models.letter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pingen.sdk.models.common.DeliverySource;

import java.time.OffsetDateTime;

/**
 * Represents a letter in the Pingen system.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Letter {

    @JsonProperty("file_original_name")
    private String fileOriginalName;

    @JsonProperty("file_pages")
    private Integer filePages;

    @JsonProperty("address_position")
    private AddressPosition addressPosition;

    @JsonProperty("delivery_product")
    private DeliveryProduct deliveryProduct;

    @JsonProperty("print_mode")
    private PrintMode printMode;

    @JsonProperty("print_spectrum")
    private PrintSpectrum printSpectrum;

    @JsonProperty("status")
    private String status;

    @JsonProperty("price_currency")
    private String priceCurrency;

    @JsonProperty("price_value")
    private Double priceValue;

    @JsonProperty("created_at")
    private OffsetDateTime createdAt;

    @JsonProperty("updated_at")
    private OffsetDateTime updatedAt;

    @JsonProperty("submitted_at")
    private OffsetDateTime submittedAt;

    @JsonProperty("address")
    private String address;

    @JsonProperty("country")
    private String country;

    @JsonProperty("source")
    private DeliverySource source;

    @JsonProperty("tracking_number")
    private String trackingNumber;

    @JsonProperty("fonts")
    private java.util.List<String> fonts;

    @JsonProperty("paper_types")
    private java.util.List<String> paperTypes;

    public Letter() {
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

    public AddressPosition getAddressPosition() {
        return addressPosition;
    }

    public void setAddressPosition(AddressPosition addressPosition) {
        this.addressPosition = addressPosition;
    }

    public DeliveryProduct getDeliveryProduct() {
        return deliveryProduct;
    }

    public void setDeliveryProduct(DeliveryProduct deliveryProduct) {
        this.deliveryProduct = deliveryProduct;
    }

    public PrintMode getPrintMode() {
        return printMode;
    }

    public void setPrintMode(PrintMode printMode) {
        this.printMode = printMode;
    }

    public PrintSpectrum getPrintSpectrum() {
        return printSpectrum;
    }

    public void setPrintSpectrum(PrintSpectrum printSpectrum) {
        this.printSpectrum = printSpectrum;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public OffsetDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(OffsetDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public DeliverySource getSource() { return source; }
    public void setSource(DeliverySource source) { this.source = source; }

    public String getTrackingNumber() { return trackingNumber; }
    public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }

    public java.util.List<String> getFonts() { return fonts; }
    public void setFonts(java.util.List<String> fonts) { this.fonts = fonts; }

    public java.util.List<String> getPaperTypes() { return paperTypes; }
    public void setPaperTypes(java.util.List<String> paperTypes) { this.paperTypes = paperTypes; }
}
