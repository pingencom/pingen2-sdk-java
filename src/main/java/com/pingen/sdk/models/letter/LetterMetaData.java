package com.pingen.sdk.models.letter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Optional metadata for a letter, allowing programmatic specification of recipient and sender
 * addresses instead of having Pingen extract them from the PDF.
 */
public class LetterMetaData {

    @JsonProperty("recipient")
    private final AddressEntry recipient;

    @JsonProperty("sender")
    private final AddressEntry sender;

    private LetterMetaData(Builder builder) {
        this.recipient = builder.recipient;
        this.sender = builder.sender;
    }

    public AddressEntry getRecipient() {
        return recipient;
    }

    public AddressEntry getSender() {
        return sender;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private AddressEntry recipient;
        private AddressEntry sender;

        private Builder() {
        }

        public Builder recipient(AddressEntry recipient) {
            this.recipient = recipient;
            return this;
        }

        public Builder sender(AddressEntry sender) {
            this.sender = sender;
            return this;
        }

        public LetterMetaData build() {
            if (recipient == null) throw new IllegalArgumentException("recipient is required in meta_data");
            if (sender == null) throw new IllegalArgumentException("sender is required in meta_data");
            return new LetterMetaData(this);
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class AddressEntry {
        @JsonProperty("name")
        private final String name;
        @JsonProperty("street")
        private final String street;
        @JsonProperty("pobox")
        private final String pobox;
        @JsonProperty("number")
        private final String number;
        @JsonProperty("zip")
        private final String zip;
        @JsonProperty("city")
        private final String city;
        @JsonProperty("country")
        private final String country;

        private AddressEntry(AddressBuilder builder) {
            this.name = builder.name;
            this.street = builder.street;
            this.pobox = builder.pobox;
            this.number = builder.number;
            this.zip = builder.zip;
            this.city = builder.city;
            this.country = builder.country;
        }

        public String getName() {
            return name;
        }

        public String getStreet() {
            return street;
        }

        public String getPobox() {
            return pobox;
        }

        public String getNumber() {
            return number;
        }

        public String getZip() {
            return zip;
        }

        public String getCity() {
            return city;
        }

        public String getCountry() {
            return country;
        }

        public static AddressBuilder builder() {
            return new AddressBuilder();
        }
    }

    public static class AddressBuilder {
        private String name, street, pobox, number, zip, city, country;

        private AddressBuilder() {
        }

        public AddressBuilder name(String name) {
            this.name = name;
            return this;
        }

        public AddressBuilder street(String street) {
            this.street = street;
            return this;
        }

        public AddressBuilder pobox(String pobox) {
            this.pobox = pobox;
            return this;
        }

        public AddressBuilder number(String number) {
            this.number = number;
            return this;
        }

        public AddressBuilder zip(String zip) {
            this.zip = zip;
            return this;
        }

        public AddressBuilder city(String city) {
            this.city = city;
            return this;
        }
        public AddressBuilder country(String country) { this.country = country; return this; }

        public AddressEntry build() {
            if (name == null) throw new IllegalArgumentException("name is required in address");
            return new AddressEntry(this);
        }
    }
}
