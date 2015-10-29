package com.github.kpavlov.restws.commons;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class HmacSignatureBuilder {

    private static final String HMAC_SHA_512 = "HmacSHA512";
    private static final byte DELIMITER = '\n';
    private String algorithm = HMAC_SHA_512;
    private String scheme;
    private String host;
    private String method;
    private String resource;
    private String nonce;
    private String apiKey;
    private byte[] apiSecret;
    private byte[] payload;
    private String date;
    private String contentType;

    public String getAlgorithm() {
        return HMAC_SHA_512;
    }

    public HmacSignatureBuilder algorithm(String algorithm) {
        this.algorithm = algorithm;
        return this;
    }

    public HmacSignatureBuilder scheme(String scheme) {
        this.scheme = scheme;
        return this;
    }

    public HmacSignatureBuilder host(String host) {
        this.host = host;
        return this;
    }

    public HmacSignatureBuilder apiKey(String key) {
        this.apiKey = key;
        return this;
    }

    public HmacSignatureBuilder method(String method) {
        this.method = method;
        return this;
    }

    public HmacSignatureBuilder resource(String resource) {
        this.resource = resource;
        return this;
    }

    public HmacSignatureBuilder contentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public HmacSignatureBuilder date(String dateString) {
        this.date = dateString;
        return this;
    }


    public HmacSignatureBuilder nonce(String nonce) {
        this.nonce = nonce;
        return this;
    }

    public HmacSignatureBuilder apiSecret(byte[] secretBytes) {
        this.apiSecret = secretBytes;
        return this;
    }

    public HmacSignatureBuilder apiSecret(String secretString) {
        this.apiSecret = secretString.getBytes(StandardCharsets.UTF_8);
        return this;
    }

    public HmacSignatureBuilder payload(byte[] payloadBytes) {
        this.payload = payloadBytes;
        return this;
    }

    public byte[] build() {
        Objects.requireNonNull(algorithm, "algorithm");
        Objects.requireNonNull(scheme, "scheme");
        Objects.requireNonNull(host, "host");
        Objects.requireNonNull(method, "method");
        Objects.requireNonNull(resource, "resource");
        Objects.requireNonNull(contentType, "contentType");
        Objects.requireNonNull(apiKey, "apiKey");
        Objects.requireNonNull(date, "date");
        Objects.requireNonNull(payload, "payload");
        try {
            final Mac digest = Mac.getInstance(algorithm);
            SecretKeySpec secretKey = new SecretKeySpec(apiSecret, algorithm);
            digest.init(secretKey);
            digest.update(method.getBytes(StandardCharsets.UTF_8));
            digest.update(DELIMITER);
            digest.update(scheme.getBytes(StandardCharsets.UTF_8));
            digest.update(DELIMITER);
            digest.update(host.getBytes(StandardCharsets.UTF_8));
            digest.update(DELIMITER);
            digest.update(resource.getBytes(StandardCharsets.UTF_8));
            digest.update(DELIMITER);
            digest.update(contentType.getBytes(StandardCharsets.UTF_8));
            digest.update(DELIMITER);
            digest.update(apiKey.getBytes(StandardCharsets.UTF_8));
            digest.update(DELIMITER);
            if (nonce != null) {
                digest.update(nonce.getBytes(StandardCharsets.UTF_8));
            }
            digest.update(DELIMITER);
            digest.update(date.getBytes(StandardCharsets.UTF_8));
            digest.update(DELIMITER);
            digest.update(payload);
            digest.update(DELIMITER);
            final byte[] signatureBytes = digest.doFinal();
            digest.reset();
            return signatureBytes;
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Can't create signature: " + e.getMessage(), e);
        }
    }

    public boolean isHashEquals(byte[] expectedSignature) {
        final byte[] signature = build();
        return MessageDigest.isEqual(signature, expectedSignature);
    }

    public String buildAsHexString() {
        return DatatypeConverter.printHexBinary(build());
    }

    public String buildAsBase64String() {
        return DatatypeConverter.printBase64Binary(build());
    }
}
