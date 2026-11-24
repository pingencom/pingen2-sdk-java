package com.pingen.sdk.models.organisation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pingen.sdk.models.letter.AddressPosition;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Represents a Pingen organisation with its billing settings and plan.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Organisation {

    @JsonProperty("name")
    private String name;

    @JsonProperty("status")
    private String status;

    @JsonProperty("plan")
    private String plan;

    @JsonProperty("edition")
    private String edition;

    @JsonProperty("color")
    private String color;

    @JsonProperty("billing_mode")
    private String billingMode;

    @JsonProperty("billing_currency")
    private String billingCurrency;

    @JsonProperty("billing_balance")
    private Double billingBalance;

    @JsonProperty("missing_credits")
    private Double missingCredits;

    @JsonProperty("default_address_position")
    private AddressPosition defaultAddressPosition;

    @JsonProperty("data_retention_addresses")
    private Integer dataRetentionAddresses;

    @JsonProperty("data_retention_pdf")
    private Integer dataRetentionPdf;

    @JsonProperty("flags")
    private List<String> flags;

    @JsonProperty("created_at")
    private OffsetDateTime createdAt;

    @JsonProperty("updated_at")
    private OffsetDateTime updatedAt;

    public Organisation() {
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPlan() { return plan; }
    public void setPlan(String plan) { this.plan = plan; }

    public String getEdition() { return edition; }
    public void setEdition(String edition) { this.edition = edition; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getBillingMode() { return billingMode; }
    public void setBillingMode(String billingMode) { this.billingMode = billingMode; }

    public String getBillingCurrency() { return billingCurrency; }
    public void setBillingCurrency(String billingCurrency) { this.billingCurrency = billingCurrency; }

    public Double getBillingBalance() { return billingBalance; }
    public void setBillingBalance(Double billingBalance) { this.billingBalance = billingBalance; }

    public Double getMissingCredits() { return missingCredits; }
    public void setMissingCredits(Double missingCredits) { this.missingCredits = missingCredits; }

    public AddressPosition getDefaultAddressPosition() { return defaultAddressPosition; }
    public void setDefaultAddressPosition(AddressPosition defaultAddressPosition) { this.defaultAddressPosition = defaultAddressPosition; }

    public Integer getDataRetentionAddresses() { return dataRetentionAddresses; }
    public void setDataRetentionAddresses(Integer dataRetentionAddresses) { this.dataRetentionAddresses = dataRetentionAddresses; }

    public Integer getDataRetentionPdf() { return dataRetentionPdf; }
    public void setDataRetentionPdf(Integer dataRetentionPdf) { this.dataRetentionPdf = dataRetentionPdf; }

    public List<String> getFlags() { return flags; }
    public void setFlags(List<String> flags) { this.flags = flags; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}
