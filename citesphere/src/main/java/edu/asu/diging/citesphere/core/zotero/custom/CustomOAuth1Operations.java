package edu.asu.diging.citesphere.core.zotero.custom;

import org.springframework.social.oauth1.OAuth1Operations;
import org.springframework.social.oauth1.OAuth1Parameters;
import org.springframework.social.oauth1.OAuthToken;
import org.springframework.social.oauth1.AuthorizedRequestToken;
import org.springframework.util.MultiValueMap;

public class CustomOAuth1Operations implements OAuth1Operations {
    private final OAuth1Operations delegate;

    public CustomOAuth1Operations(OAuth1Operations delegate) {
        this.delegate = delegate;
    }

    @Override
    public String buildAuthorizeUrl(String requestToken, OAuth1Parameters parameters) {
        if (parameters == null) {
            parameters = new OAuth1Parameters();
        }
        parameters.set("write_access", "1");
        parameters.set("all_groups", "write");
        return delegate.buildAuthorizeUrl(requestToken, parameters);
    }

    @Override
    public String buildAuthenticateUrl(String requestToken, OAuth1Parameters parameters) {
        if (parameters == null) {
            parameters = new OAuth1Parameters();
        }
        parameters.set("write_access", "1");
        parameters.set("all_groups", "write");
        return delegate.buildAuthenticateUrl(requestToken, parameters);
    }

    @Override
    public OAuthToken fetchRequestToken(String callbackUrl, MultiValueMap<String, String> additionalParameters) {
        return delegate.fetchRequestToken(callbackUrl, additionalParameters);
    }

    @Override
    public OAuthToken exchangeForAccessToken(AuthorizedRequestToken requestToken, MultiValueMap<String, String> additionalParameters) {
        return delegate.exchangeForAccessToken(requestToken, additionalParameters);
    }

    @Override
    public org.springframework.social.oauth1.OAuth1Version getVersion() {
        return delegate.getVersion();
    }
} 