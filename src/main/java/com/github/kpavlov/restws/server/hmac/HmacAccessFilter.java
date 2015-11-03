package com.github.kpavlov.restws.server.hmac;

import com.github.kpavlov.restws.commons.HmacSignatureBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedCredentialsNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Restricts access to resource if HMAC signature is not valid.
 * <p>
 * This filter does not provide Spring {@link org.springframework.security.core.context.SecurityContext} down to filter chain.
 */
public class HmacAccessFilter extends OncePerRequestFilter {

    private static final Pattern AUTHORIZATION_TOKEN_PATTERN = Pattern.compile("^(\\w+) (\\S+):(\\S+):([\\S]+)$");

    @Autowired
    private CredentialsProvider credentialsProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null) {
            // invalid authorization token
            logger.error("Authorization header is missing");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;

        }

        final Matcher authHeaderMatcher = AUTHORIZATION_TOKEN_PATTERN.matcher(authHeader);
        if (!authHeaderMatcher.matches()) {
            // invalid authorization token
            logger.error("Bad authorization data");
            response.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE, "Bad authorization data");
            return;
        }

        final String algorithm = authHeaderMatcher.group(1);
        final String apiKey = authHeaderMatcher.group(2);
        final String nonce = authHeaderMatcher.group(3);
        final String receivedDigest = authHeaderMatcher.group(4);

        final byte[] apiSecret = credentialsProvider.getApiSecret(apiKey);
        if (apiSecret == null) {
            throw new PreAuthenticatedCredentialsNotFoundException("Access Denied");
        }

        CachingRequestWrapper requestWrapper = new CachingRequestWrapper(request);
        final byte[] contentAsByteArray = requestWrapper.getContentAsByteArray();

        final HmacSignatureBuilder signatureBuilder = new HmacSignatureBuilder()
                .algorithm(algorithm)
                .scheme(request.getScheme())
                .host(request.getServerName() + ":" + request.getServerPort())
                .method(request.getMethod())
                .resource(request.getRequestURI())
                .contentType(request.getContentType())
                .date(request.getHeader(HttpHeaders.DATE))
                .nonce(nonce)
                .apiKey(apiKey)
                .apiSecret(apiSecret)
                .payload(contentAsByteArray);

        final byte[] receivedDigestBytes = DatatypeConverter.parseBase64Binary(receivedDigest);
        if (!signatureBuilder.isHashEquals(receivedDigestBytes)) {
            // invalid digest
            logger.error("Invalid digest");
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid authorization data");
            return;
        }

        filterChain.doFilter(requestWrapper, response);
    }
}
