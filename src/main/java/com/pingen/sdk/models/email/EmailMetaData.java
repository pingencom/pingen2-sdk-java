package com.pingen.sdk.models.email;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Metadata required when creating an email delivery.
 * All fields are required.
 */
public class EmailMetaData {

    @JsonProperty("sender_name")
    private final String senderName;

    @JsonProperty("recipient_email")
    private final String recipientEmail;

    @JsonProperty("recipient_name")
    private final String recipientName;

    @JsonProperty("reply_email")
    private final String replyEmail;

    @JsonProperty("reply_name")
    private final String replyName;

    @JsonProperty("subject")
    private final String subject;

    @JsonProperty("content")
    private final String content;

    private EmailMetaData(Builder builder) {
        this.senderName = builder.senderName;
        this.recipientEmail = builder.recipientEmail;
        this.recipientName = builder.recipientName;
        this.replyEmail = builder.replyEmail;
        this.replyName = builder.replyName;
        this.subject = builder.subject;
        this.content = builder.content;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getRecipientEmail() {
        return recipientEmail;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public String getReplyEmail() {
        return replyEmail;
    }

    public String getReplyName() {
        return replyName;
    }

    public String getSubject() {
        return subject;
    }

    public String getContent() {
        return content;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String senderName, recipientEmail, recipientName;
        private String replyEmail, replyName, subject, content;

        private Builder() {
        }

        public Builder senderName(String senderName) {
            this.senderName = senderName;
            return this;
        }

        public Builder recipientEmail(String recipientEmail) {
            this.recipientEmail = recipientEmail;
            return this;
        }

        public Builder recipientName(String recipientName) {
            this.recipientName = recipientName;
            return this;
        }

        public Builder replyEmail(String replyEmail) {
            this.replyEmail = replyEmail;
            return this;
        }

        public Builder replyName(String replyName) {
            this.replyName = replyName;
            return this;
        }

        public Builder subject(String subject) {
            this.subject = subject;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public EmailMetaData build() {
            if (senderName == null) throw new IllegalArgumentException("senderName is required");
            if (recipientEmail == null) throw new IllegalArgumentException("recipientEmail is required");
            if (recipientName == null) throw new IllegalArgumentException("recipientName is required");
            if (replyEmail == null) throw new IllegalArgumentException("replyEmail is required");
            if (replyName == null) throw new IllegalArgumentException("replyName is required");
            if (subject == null) throw new IllegalArgumentException("subject is required");
            if (content == null) throw new IllegalArgumentException("content is required");
            return new EmailMetaData(this);
        }
    }
}
