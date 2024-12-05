package edu.asu.diging.citesphere.core.service.oauth.impl;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.javers.common.collections.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;

import edu.asu.diging.citesphere.core.service.oauth.InternalTokenManager;
import edu.asu.diging.citesphere.core.service.oauth.OAuthScope;
import edu.asu.diging.citesphere.user.IUser;

@Service
@PropertySource({ "classpath:config.properties", "${appConfigFile:classpath:}/app.properties" })
@SuppressWarnings("deprecation")
@Transactional
public class InternalTokenManagerImpl extends DefaultTokenServices implements InternalTokenManager {
    
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
        this.setTokenStore(tokenStore);
    }
    
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.service.oauth.impl.InternalTokenManager#getAccessToken()
     */
    @Override
    @Transactional(TxType.REQUIRES_NEW)
    public OAuth2AccessToken getAccessToken(IUser user) {
        Collection<OAuth2AccessToken> tokens = tokenStore.findTokensByClientIdAndUserName(citesphereClientId, user.getUsername());
        Optional<OAuth2AccessToken> validToken = tokens.stream().filter(t -> !t.isExpired()).findFirst();
        if (validToken.isPresent()) {
            return validToken.get();
        }
        return createAccessToken(user);
    }
    
    private OAuth2AccessToken createAccessToken(IUser user) {
        DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken(UUID.randomUUID().toString());
        if (tokenValidity > 0) {
            token.setExpiration(new Date(System.currentTimeMillis() + (tokenValidity * 1000L)));
        }
        token.setScope(Sets.asSet(OAuthScope.READ.getScope()));
        
        AuthorizationRequest request = new AuthorizationRequest(citesphereClientId, token.getScope());
        TokenRequest implicitRequest = new ImplicitTokenRequest(requestFactory.createTokenRequest(request, "implicit"), requestFactory.createOAuth2Request(request));
        
        OAuth2Authentication authentication = getOAuth2Authentication(clientDetailsService.loadClientByClientId(citesphereClientId), implicitRequest, user);
        tokenStore.storeAccessToken(token, authentication);
        return token;
    }
    
    private OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest, IUser user) {
        OAuth2Request storedOAuth2Request = requestFactory.createOAuth2Request(client, tokenRequest);
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, user.getRoles());
        return new OAuth2Authentication(storedOAuth2Request, authentication);
    }
    
    private String extractTokenKey(String value) {
        if(value == null) {
            return null;
        } else {
            MessageDigest digest;
            try {
                digest = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException var5) {
                throw new IllegalStateException("MD5 algorithm not available.  Fatal (should be in the JDK).");
            }

            try {
                byte[] e = digest.digest(value.getBytes("UTF-8"));
                return String.format("%032x", new Object[]{new BigInteger(1, e)});
            } catch (UnsupportedEncodingException var4) {
                throw new IllegalStateException("UTF-8 encoding not available.  Fatal (should be in the JDK).");
            }
        }
    }
}
