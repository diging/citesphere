package edu.asu.diging.citesphere.core.service.oauth.impl;

import java.util.Collection;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import org.javers.common.collections.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.implicit.ImplicitTokenRequest;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;

import edu.asu.diging.citesphere.core.service.oauth.InternalTokenManager;
import edu.asu.diging.citesphere.core.service.oauth.OAuthScope;

@Service
@PropertySource({ "classpath:config.properties", "${appConfigFile:classpath:}/app.properties" })
@SuppressWarnings("deprecation")
@Transactional
public class InternalTokenManagerImpl implements InternalTokenManager {
    
    @Value("${_oauth_token_validity}")
    private int tokenValidity;
    
    @Value("${_citephere_oauth2_app_clientid}")
    private String citesphereClientId;
    
    @Autowired
    private TokenStore tokenStore;
    
    @Autowired
    private ClientDetailsService clientDetailsService;
    
    private OAuth2RequestFactory requestFactory;
    
    @PostConstruct
    public void init() {
        this.requestFactory = new DefaultOAuth2RequestFactory(clientDetailsService);
    }
    
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.service.oauth.impl.InternalTokenManager#getAccessToken()
     */
    @Override
    public OAuth2AccessToken getAccessToken() {
        Collection<OAuth2AccessToken> tokens = tokenStore.findTokensByClientId(citesphereClientId);
        Optional<OAuth2AccessToken> validToken = tokens.stream().filter(t -> !t.isExpired()).findFirst();
        if (validToken.isPresent()) {
            return validToken.get();
        }
        return createAccessToken();
    }
    
    private OAuth2AccessToken createAccessToken() {
        DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken(UUID.randomUUID().toString());
        if (tokenValidity > 0) {
            token.setExpiration(new Date(System.currentTimeMillis() + (tokenValidity * 1000L)));
        }
        token.setScope(Sets.asSet(OAuthScope.READ.getScope()));
        
        AuthorizationRequest request = new AuthorizationRequest(citesphereClientId, token.getScope());
        TokenRequest implicitRequest = new ImplicitTokenRequest(requestFactory.createTokenRequest(request, "implicit"), requestFactory.createOAuth2Request(request));
        
        OAuth2Authentication authentication = getOAuth2Authentication(clientDetailsService.loadClientByClientId(citesphereClientId), implicitRequest);
        tokenStore.storeAccessToken(token, authentication);

        return token;
    }
    
    private OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {
        OAuth2Request storedOAuth2Request = requestFactory.createOAuth2Request(client, tokenRequest);
        return new OAuth2Authentication(storedOAuth2Request, null);
    }
}
