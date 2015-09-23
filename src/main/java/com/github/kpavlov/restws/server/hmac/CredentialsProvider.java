package com.github.kpavlov.restws.server.hmac;

public interface CredentialsProvider {

    byte[] getApiSecret(String apiKey);
}
