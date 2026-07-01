package com.pingen.sdk.models.letter;

import java.util.HashMap;
import java.util.Map;

/**
 * Optional metadata for a letter, allowing programmatic specification of recipient and sender
 * addresses instead of having Pingen extract them from the PDF.
 */
public class LetterMetaData {

    private final AddressEntry recipient;
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

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        if (recipient != null) map.put("recipient", recipient.toMap());
        if (sender != null) map.put("sender", sender.toMap());
        return map;
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

    public static class AddressEntry {
        private final String name;
        private final String street;
        private final String pobox;
        private final String number;
        private final String zip;
        private final String city;
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

        public Map<String, Object> toMap() {
            Map<String, Object> map = new HashMap<>();
            if (name != null) map.put("name", name);
            if (street != null) map.put("street", street);
            if (pobox != null) map.put("pobox", pobox);
            if (number != null) map.put("number", number);
            if (zip != null) map.put("zip", zip);
            if (city != null) map.put("city", city);
            if (country != null) map.put("country", country);
            return map;
        }

        public static AddressBuilder builder() {
            return new AddressBuilder();
        }
    }

    public static class AddressBuilder {
        private String name;
        private String street;
        private String pobox;
        private String number;
        private String zip;
        private String city;
        private String country;

        private AddressBuilder() {
        }

        public AddressBuilder name(String name) { this.name = name; return this; }
        public AddressBuilder street(String street) { this.street = street; return this; }
        public AddressBuilder pobox(String pobox) { this.pobox = pobox; return this; }
        public AddressBuilder number(String number) { this.number = number; return this; }
        public AddressBuilder zip(String zip) { this.zip = zip; return this; }
        public AddressBuilder city(String city) { this.city = city; return this; }
        public AddressBuilder country(String country) { this.country = country; return this; }

        public AddressEntry build() {
            if (name == null) throw new IllegalArgumentException("name is required in address");
            return new AddressEntry(this);
        }
    }
}
