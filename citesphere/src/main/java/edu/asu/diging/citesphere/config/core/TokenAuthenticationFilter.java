package edu.asu.diging.citesphere.config.core;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import edu.asu.diging.citesphere.core.service.IPersonalAccessTokenService;

@Component
public class TokenAuthenticationFilter extends GenericFilterBean {

    private static final String BASIC = "Basic ";

    private final IPersonalAccessTokenService personalAccessTokenService;

    @Autowired
    public TokenAuthenticationFilter(IPersonalAccessTokenService personalAccessTokenService) {
        this.personalAccessTokenService = personalAccessTokenService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        Optional<String> token = Optional.ofNullable(httpRequest.getHeader("Authorization"))
                .filter(s -> s.length() > BASIC.length() && s.startsWith(BASIC))
                .map(s -> s.substring(BASIC.length(), s.length()));

        Optional<Authentication> authentication = personalAccessTokenService.verifyToken(token);
        if (authentication.isPresent()) {
            SecurityContextHolder.getContext().setAuthentication(authentication.get());
        }
        chain.doFilter(httpRequest, response);
    }
}