package com.github.kpavlov.restws.server.hmac;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedCredentialsNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.AbstractRequestLoggingFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Component
public class HmacFilter extends AbstractRequestLoggingFilter {

    public static final String API_KEY_HEADER = "X-KEY";
    private static final String HMAC_SHA_512 = "HmacSHA512";

    @Autowired
    private CredentialsProvider credentialsProvider;

    public HmacFilter() {
        super();
        setIncludePayload(true);
    }

    @Override
    protected String createMessage(HttpServletRequest request, String prefix, String suffix) {
        return null;
    }

    @Override
    protected void beforeRequest(HttpServletRequest request, String message) {
        assert (request instanceof ContentCachingRequestWrapper);
        ContentCachingRequestWrapper requestWrapper = (ContentCachingRequestWrapper) request;

        final String apiKey = request.getHeader(API_KEY_HEADER);
        final String authHeader = request.getHeader("X-" + HttpHeaders.AUTHORIZATION);
        String receivedDigest = authHeader.substring("Bearer ".length());

        final byte[] apiSecret = credentialsProvider.getApiSecret(apiKey);
        if (apiSecret == null) {
            throw new PreAuthenticatedCredentialsNotFoundException("Access Denied");
        }

        Mac digest = null;
        SecretKeySpec secretKey = new SecretKeySpec(apiSecret, HMAC_SHA_512);
        try {
            digest = Mac.getInstance(HMAC_SHA_512);
            digest.init(secretKey);
            digest.update(apiKey.getBytes(StandardCharsets.UTF_8));
            digest.update(requestWrapper.getContentAsByteArray());
            final byte[] expectedDigest = digest.doFinal();
            digest.reset();

            final byte[] receivedDigestBytes = DatatypeConverter.parseHexBinary(receivedDigest);
            if (!MessageDigest.isEqual(receivedDigestBytes, expectedDigest)) {
                // invalid digest
                throw new BadCredentialsException("Invalid digest");
            }
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            logger.warn(e);
        }
    }

    @Override
    protected void afterRequest(HttpServletRequest request, String message) {
        //noop
    }
}
