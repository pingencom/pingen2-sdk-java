package com.pingen.sdk.integration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

class IntegrationTestCredentials {

    private static final Properties PROPS = load();

    private static Properties load() {
        File envFile = new File(".env");
        if (!envFile.exists()) {
            throw new IllegalStateException(
                    ".env file not found. Copy .env.example to .env and fill in your staging credentials.");
        }
        Properties p = new Properties();
        try (FileInputStream in = new FileInputStream(envFile)) {
            p.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load .env", e);
        }
        return p;
    }

    static String clientId() {
        return PROPS.getProperty("PINGEN2_CLIENT_ID");
    }

    static String clientSecret() {
        return PROPS.getProperty("PINGEN2_CLIENT_SECRET");
    }

    static String organizationName() {
        return PROPS.getProperty("PINGEN2_ORGANIZATION_NAME");
    }
}
