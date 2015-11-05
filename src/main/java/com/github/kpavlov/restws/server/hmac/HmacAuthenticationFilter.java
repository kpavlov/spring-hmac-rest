package com.github.kpavlov.restws.server.hmac;


import com.github.kpavlov.restws.commons.HmacSignatureBuilder;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class HmacAuthenticationFilter extends OncePerRequestFilter {

    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        final AuthHeader authHeader = HmacUtil.getAuthHeader(request);

        if (authHeader == null) {
            // invalid authorization token
            logger.warn("Authorization header is missing");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        final String username = authHeader.getApiKey();

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        assert (userDetails != null);

        CachingRequestWrapper requestWrapper = new CachingRequestWrapper(request);
        final byte[] contentAsByteArray = requestWrapper.getContentAsByteArray();

        final HmacSignatureBuilder signatureBuilder = new HmacSignatureBuilder()
                .algorithm(authHeader.getAlgorithm())
                .scheme(request.getScheme())
                .host(request.getServerName() + ":" + request.getServerPort())
                .method(request.getMethod())
                .resource(request.getRequestURI())
                .contentType(request.getContentType())
                .date(request.getHeader(HttpHeaders.DATE))
                .nonce(authHeader.getNonce())
                .apiKey(username)
                .apiSecret(userDetails.getPassword())
                .payload(contentAsByteArray);

        if (!signatureBuilder.isHashEquals(authHeader.getDigest())) {
            throw new BadCredentialsException("HmacAccessFilter.badSignature");
        }

        final PreAuthenticatedAuthenticationToken authentication = new PreAuthenticatedAuthenticationToken(
                userDetails.getUsername(),
                null,
                userDetails.getAuthorities());
        authentication.setDetails(userDetails);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        try {
            filterChain.doFilter(requestWrapper, response);
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    @Required
    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }
}
