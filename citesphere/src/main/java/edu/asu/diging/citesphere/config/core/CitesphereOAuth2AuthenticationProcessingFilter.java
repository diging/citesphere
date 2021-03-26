/*
 * Copyright 2006-2011 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * https://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package edu.asu.diging.citesphere.config.core;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.exceptions.BadClientCredentialsException;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.BearerTokenExtractor;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetailsSource;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationManager;
import org.springframework.security.oauth2.provider.authentication.TokenExtractor;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.util.Assert;

/**
 * A pre-authentication filter for OAuth2 protected resources. Extracts an
 * OAuth2 token from the incoming request and uses it to populate the Spring
 * Security context with an {@link OAuth2Authentication} (if used in conjunction
 * with an {@link OAuth2AuthenticationManager}).
 *
 * <p>
 * 
 * @deprecated See the <a href=
 *             "https://github.com/spring-projects/spring-security/wiki/OAuth-2.0-Migration-Guide">OAuth
 *             2.0 Migration Guide</a> for Spring Security 5.
 *
 * @author Dave Syer; adjusted jdamerow
 * 
 */
@Deprecated
public class CitesphereOAuth2AuthenticationProcessingFilter extends AbstractAuthenticationProcessingFilter {

    private final static Log logger = LogFactory.getLog(CitesphereOAuth2AuthenticationProcessingFilter.class);

    private AuthenticationEntryPoint authenticationEntryPoint = new OAuth2AuthenticationEntryPoint();

    private AuthenticationManager authenticationManager;

    private AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource = new OAuth2AuthenticationDetailsSource();

    private TokenExtractor tokenExtractor = new BearerTokenExtractor();

    protected CitesphereOAuth2AuthenticationProcessingFilter(String defaultFilterProcessesUrl) {
        super(defaultFilterProcessesUrl);
    }

    /**
     * @param authenticationEntryPoint
     *            the authentication entry point to set
     */
    public void setAuthenticationEntryPoint(AuthenticationEntryPoint authenticationEntryPoint) {
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    /**
     * @param authenticationManager
     *            the authentication manager to set (mandatory with no default)
     */
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    /**
     * @param tokenExtractor
     *            the tokenExtractor to set
     */
    public void setTokenExtractor(TokenExtractor tokenExtractor) {
        this.tokenExtractor = tokenExtractor;
    }

    /**
     * @param authenticationDetailsSource
     *            The AuthenticationDetailsSource to use
     */
    public void setAuthenticationDetailsSource(
            AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource) {
        Assert.notNull(authenticationDetailsSource, "AuthenticationDetailsSource required");
        this.authenticationDetailsSource = authenticationDetailsSource;
    }

    @Override
    public void afterPropertiesSet() {
        setAuthenticationFailureHandler(new AuthenticationFailureHandler() {
            public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                    AuthenticationException exception) throws IOException, ServletException {
                if (exception instanceof BadCredentialsException) {
                    exception = new BadCredentialsException(exception.getMessage(),
                            new BadClientCredentialsException());
                }
                authenticationEntryPoint.commence(request, response, exception);
            }
        });
        setAuthenticationSuccessHandler(new AuthenticationSuccessHandler() {
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                    Authentication authentication) throws IOException, ServletException {
                // no-op - just allow filter chain to continue to token endpoint
            }
        });
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {
        final boolean debug = logger.isDebugEnabled();

        Authentication authentication = tokenExtractor.extract(request);

        if (authentication == null) {
            throw new BadCredentialsException("No token provided.");
        }

        try {
            request.setAttribute(OAuth2AuthenticationDetails.ACCESS_TOKEN_VALUE, authentication.getPrincipal());
            if (authentication instanceof AbstractAuthenticationToken) {
                AbstractAuthenticationToken needsDetails = (AbstractAuthenticationToken) authentication;
                needsDetails.setDetails(authenticationDetailsSource.buildDetails(request));
            }
            Authentication authResult = authenticationManager.authenticate(authentication);

            if (debug) {
                logger.debug("Authentication success: " + authResult);
            }

            return authResult;
        } catch (InvalidTokenException ex) {
            throw new BadCredentialsException(ex.getMessage());
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
            Authentication authResult) throws IOException, ServletException {
        super.successfulAuthentication(request, response, chain, authResult);
        chain.doFilter(request, response);
    }

}
