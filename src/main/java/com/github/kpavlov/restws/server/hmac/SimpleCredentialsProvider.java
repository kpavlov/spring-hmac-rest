package com.github.kpavlov.restws.server.hmac;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleCredentialsProvider implements CredentialsProvider {

    private final Map<String, byte[]> secrets = new ConcurrentHashMap<>();

    public SimpleCredentialsProvider() {
    }

    public SimpleCredentialsProvider(String apiKey, String secret) {
        addCredentials(apiKey, secret);
    }

    public void addCredentials(String apiKey, String secret) {
        secrets.put(apiKey, secret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public byte[] getApiSecret(String apiKey) {
        if (apiKey == null) {
            return null;
        }
        return secrets.get(apiKey);
    }
}
