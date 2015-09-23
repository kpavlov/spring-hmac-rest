package com.github.kpavlov.restws.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kpavlov.restws.server.model.AbstractResponseWrapper;
import com.github.kpavlov.restws.server.model.RequestWrapper;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;

public class ApiClient {

    private RestTemplate restTemplate;
    private ObjectMapper objectMapper = new ObjectMapper();
    private final CredentialsProvider credentialsProvider;

    public ApiClient() {
        credentialsProvider = new BasicCredentialsProvider();
    }

    public <I, O extends AbstractResponseWrapper<I>> O echo(I request, Class<O> clazz) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("X-KEY", "api");
        headers.add("X-" + HttpHeaders.AUTHORIZATION,
                "Bearer " + DatatypeConverter.printHexBinary("secret".getBytes(StandardCharsets.UTF_8))
        );

        RequestWrapper<I> requestWrapper = new RequestWrapper<>(request);
        requestWrapper.setData(request);

        final String valueAsString;
        try {
            valueAsString = objectMapper.writeValueAsString(requestWrapper);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        HttpEntity<String> entity = new HttpEntity<>(valueAsString, headers);

        final O payloadWrapper = restTemplate.postForObject(
                "http://localhost:8080/api/echo",
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

    public void init() {

        HttpComponentsClientHttpRequestFactoryBasicAuth requestFactory = new HttpComponentsClientHttpRequestFactoryBasicAuth(
                new HttpHost("localhost", 8080, "http")
        );
        requestFactory.setCredentialsProvider(credentialsProvider);

        restTemplate = new RestTemplate(requestFactory);

    }

}
