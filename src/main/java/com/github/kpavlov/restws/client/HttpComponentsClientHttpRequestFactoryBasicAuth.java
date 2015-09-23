package com.github.kpavlov.restws.client;

import org.apache.http.HttpHost;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import java.net.URI;

public class HttpComponentsClientHttpRequestFactoryBasicAuth
        extends HttpComponentsClientHttpRequestFactory {

    final HttpHost host;
    private CredentialsProvider credentialsProvider;

    public HttpComponentsClientHttpRequestFactoryBasicAuth(HttpHost host) {
        super();
        this.host = host;
    }

    protected HttpContext createHttpContext(HttpMethod httpMethod, URI uri) {
        return createHttpContext();
    }

    public void setCredentialsProvider(CredentialsProvider credentialsProvider) {

        this.credentialsProvider = credentialsProvider;
    }

    private HttpContext createHttpContext() {
        // Create AuthCache instance
        AuthCache authCache = new BasicAuthCache();
        // Generate BASIC scheme object and add it to the local auth cache
        BasicScheme basicAuth = new BasicScheme();
        authCache.put(host, basicAuth);

        // Add AuthCache to the execution context
        BasicHttpContext localcontext = new BasicHttpContext();
        localcontext.setAttribute(HttpClientContext.AUTH_CACHE, authCache);
        localcontext.setAttribute(HttpClientContext.CREDS_PROVIDER, credentialsProvider);

        return localcontext;
    }
}
