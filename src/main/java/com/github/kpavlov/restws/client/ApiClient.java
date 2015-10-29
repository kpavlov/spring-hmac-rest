package com.github.kpavlov.restws.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kpavlov.restws.commons.HmacSignatureBuilder;
import com.github.kpavlov.restws.server.model.AbstractResponseWrapper;
import com.github.kpavlov.restws.server.model.RequestWrapper;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.util.UUID;

public class ApiClient {

    private static final String USER_AGENT = "RestAPI client v.1.0";
    private RestTemplate restTemplate;
    private ObjectMapper objectMapper = new ObjectMapper();
    private final CredentialsProvider credentialsProvider;

    private final Clock clock = Clock.systemUTC();

    private final String scheme;
    private final String host;
    private final int port;


    public ApiClient(CredentialsProvider credentialsProvider, String scheme, String host, int port) {
        this.credentialsProvider = credentialsProvider;
        this.scheme = scheme;
        this.host = host;
        this.port = port;
    }

    public ApiClient(String host, int port) {
        this(new BasicCredentialsProvider(), (port == 443) ? "https" : "http", host, port);
    }

    public <I, O extends AbstractResponseWrapper<I>> O echo(I request, Class<O> clazz) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        final AuthScope authScope = new AuthScope(host, port, AuthScope.ANY_REALM, scheme);
        final Credentials credentials = credentialsProvider.getCredentials(authScope);

        if (credentials == null) {
            throw new RuntimeException("Can't find credentials for AuthScope: " + authScope);
        }

        String apiKey = credentials.getUserPrincipal().getName();
        String apiSecret = credentials.getPassword();

        String nonce = UUID.randomUUID().toString();

        headers.setDate(clock.millis());
        String dateString = headers.getFirst(HttpHeaders.DATE);

        RequestWrapper<I> requestWrapper = new RequestWrapper<>(request);
        requestWrapper.setData(request);

        final String valueAsString;
        try {
            valueAsString = objectMapper.writeValueAsString(requestWrapper);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        final String resource = "/api/echo";
        final HmacSignatureBuilder signatureBuilder = new HmacSignatureBuilder()
                .scheme(scheme)
                .host(host+":" + port)
                .method("POST")
                .resource(resource)
                .apiKey(apiKey)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .nonce(nonce)
                .date(dateString)
                .apiSecret(apiSecret)
                .payload(valueAsString.getBytes(StandardCharsets.UTF_8));
        final String signature = signatureBuilder
                .buildAsBase64String();

        final String authHeader = signatureBuilder.getAlgorithm() + " " + apiKey + ":" + nonce + ":" + signature;
        headers.add(HttpHeaders.AUTHORIZATION, authHeader);

        headers.add(HttpHeaders.USER_AGENT, USER_AGENT);

        HttpEntity<String> entity = new HttpEntity<>(valueAsString, headers);

        final O payloadWrapper = restTemplate.postForObject(
                scheme + "://" + host + ":" + port + resource,
                entity,
                clazz);

        return payloadWrapper;
    }

    public void setCredentials(String user, String password) {
        credentialsProvider.setCredentials(
                new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT, AuthScope.ANY_REALM),
                new UsernamePasswordCredentials(user, password)
        );
    }

    public void setCredentials(AuthScope authScope, String user, String password) {
        credentialsProvider.setCredentials(
                authScope,
                new UsernamePasswordCredentials(user, password)
        );
    }

    public void init() {
/*
    HttpComponentsClientHttpRequestFactoryBasicAuth requestFactory = new HttpComponentsClientHttpRequestFactoryBasicAuth(
                new HttpHost("localhost", 8080, "http")
        );
        requestFactory.setCredentialsProvider(credentialsProvider);
        */

        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();

        restTemplate = new RestTemplate(requestFactory);
    }

}
