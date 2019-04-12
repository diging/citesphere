package edu.asu.diging.citesphere.config;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.util.Assert;

public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private String defaultFailureUrl;
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    public CustomAuthenticationFailureHandler() {
    }

    public CustomAuthenticationFailureHandler(String defaultFailureUrl) {
	setDefaultFailureUrl(defaultFailureUrl);
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
	    AuthenticationException exception) throws IOException, ServletException {

	String errorKey;

	if (exception.getClass().equals(LockedException.class)) {
	    errorKey = "user_not_approved";
	} else {
	    errorKey = "bad_credentials";
	}

	logger.debug("Redirecting to " + defaultFailureUrl + errorKey);
	redirectStrategy.sendRedirect(request, response, defaultFailureUrl + errorKey);
    }

    /**
     * The URL which will be used as the failure destination.
     *
     * @param defaultFailureUrl
     *            the failure URL, for example "/loginFailed.jsp".
     */
    public void setDefaultFailureUrl(String defaultFailureUrl) {
	Assert.isTrue(UrlUtils.isValidRedirectUrl(defaultFailureUrl),
		() -> "'" + defaultFailureUrl + "' is not a valid redirect URL");
	this.defaultFailureUrl = defaultFailureUrl;
    }
}