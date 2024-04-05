package edu.asu.diging.citesphere.core.service.impl;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.DefaultAuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.transaction.annotation.Transactional;

import edu.asu.diging.citesphere.core.model.oauth.impl.DbAccessToken;
import edu.asu.diging.citesphere.core.model.oauth.impl.DbRefreshToken;
import edu.asu.diging.citesphere.core.repository.oauth.DbAccessTokenRepository;
import edu.asu.diging.citesphere.core.repository.oauth.DbRefreshTokenRepository;

/**
 * Modeled after:
 * https://blog.couchbase.com/custom-token-store-spring-securtiy-oauth2/
 * @author jdamerow
 *
 */
@Transactional
public class DbTokenStore implements TokenStore {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private DbAccessTokenRepository dbAccessTokenRepository;

    private DbRefreshTokenRepository dbRefreshTokenRepository;

    public DbTokenStore(DbAccessTokenRepository cbAccessTokenRepository, DbRefreshTokenRepository cbRefreshTokenRepository){
        this.dbAccessTokenRepository = cbAccessTokenRepository;
        this.dbRefreshTokenRepository = cbRefreshTokenRepository;
    }

    private AuthenticationKeyGenerator authenticationKeyGenerator = new DefaultAuthenticationKeyGenerator();

    @Override
    public OAuth2Authentication readAuthentication(OAuth2AccessToken accessToken) {
        return readAuthentication(accessToken.getValue());
    }

    @Override
    public OAuth2Authentication readAuthentication(String token) {
        Optional<DbAccessToken> accessToken = dbAccessTokenRepository.findByTokenId(extractTokenKey(token));
        if (accessToken.isPresent()) {
            return accessToken.get().getAuthentication();
        }
        return null;
    }

    @Override
    public void storeAccessToken(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        String refreshToken = null;
        if (accessToken.getRefreshToken() != null) {
            refreshToken = accessToken.getRefreshToken().getValue();
        }
        
        // If access token with the same token id is present it will be deleted before creating the token.
        Optional<DbAccessToken> existingToken = getExistingAccessTokenByTokenId(accessToken);
        if (existingToken.isPresent()) {
            DbAccessToken token = existingToken.get();
            logger.info("Inside storeAccessToken - Existing tokens already present - " + token.getTokenId());
            dbAccessTokenRepository.delete(token);
        }
        
        List<DbAccessToken> tokensByAuthId = getAccessTokensByAuthenticationId(authentication);
        deleteAccessTokens(tokensByAuthId);
        
        DbAccessToken cat =  new DbAccessToken();
        cat.setId(UUID.randomUUID().toString()+UUID.randomUUID().toString());
        cat.setTokenId(extractTokenKey(accessToken.getValue()));
        cat.setToken(accessToken);
        cat.setAuthenticationId(authenticationKeyGenerator.extractKey(authentication));
        cat.setUsername(authentication.isClientOnly() ? null : authentication.getName());
        cat.setClientId(authentication.getOAuth2Request().getClientId());
        cat.setAuthentication(authentication);
        cat.setRefreshToken(extractTokenKey(refreshToken));

        dbAccessTokenRepository.save(cat);
    }

    @Override
    public OAuth2AccessToken readAccessToken(String tokenValue) {
        Optional<DbAccessToken> accessToken = dbAccessTokenRepository.findByTokenId(extractTokenKey(tokenValue));
        if (accessToken.isPresent()) {
            return accessToken.get().getToken();
        }
        return null;
        
    }

    @Override
    public void removeAccessToken(OAuth2AccessToken oAuth2AccessToken) {
        Optional<DbAccessToken> accessToken = dbAccessTokenRepository.findByTokenId(extractTokenKey(oAuth2AccessToken.getValue()));
        if (accessToken.isPresent()) {
            dbAccessTokenRepository.delete(accessToken.get());
        }
    }

    @Override
    public void storeRefreshToken(OAuth2RefreshToken refreshToken, OAuth2Authentication authentication) {
        DbRefreshToken crt = new DbRefreshToken();
        crt.setId(UUID.randomUUID().toString()+UUID.randomUUID().toString());
        crt.setTokenId(extractTokenKey(refreshToken.getValue()));
        crt.setToken(refreshToken);
        crt.setAuthentication(authentication);
        dbRefreshTokenRepository.save(crt);
    }

    @Override
    public OAuth2RefreshToken readRefreshToken(String tokenValue) {
        Optional<DbRefreshToken> refreshToken = dbRefreshTokenRepository.findByTokenId(extractTokenKey(tokenValue));
        return refreshToken.isPresent()? refreshToken.get().getToken() :null;
    }

    @Override
    public OAuth2Authentication readAuthenticationForRefreshToken(OAuth2RefreshToken refreshToken) {
        Optional<DbRefreshToken> rtk = dbRefreshTokenRepository.findByTokenId(extractTokenKey(refreshToken.getValue()));
        return rtk.isPresent()? rtk.get().getAuthentication() :null;
    }

    @Override
    public void removeRefreshToken(OAuth2RefreshToken refreshToken) {
        Optional<DbRefreshToken> rtk = dbRefreshTokenRepository.findByTokenId(extractTokenKey(refreshToken.getValue()));
        if (rtk.isPresent()) {
            dbRefreshTokenRepository.delete(rtk.get());
        }
    }

    @Override
    public void removeAccessTokenUsingRefreshToken(OAuth2RefreshToken refreshToken) {
        Optional<DbAccessToken> token = dbAccessTokenRepository.findByRefreshToken(extractTokenKey(refreshToken.getValue()));
        if(token.isPresent()){
            dbAccessTokenRepository.delete(token.get());
        }
    }

    @Override
    public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {
        String authenticationId = authenticationKeyGenerator.extractKey(authentication);
        List<DbAccessToken> tokens = dbAccessTokenRepository.findByAuthenticationId(authenticationId);

        tokens.sort(new Comparator<DbAccessToken>() {

            @Override
            public int compare(DbAccessToken o1, DbAccessToken o2) {
                if (o1.getToken().getExpiration().before(o2.getToken().getExpiration())) {
                    return -1;
                } 
                if (o1.getToken().getExpiration().after(o2.getToken().getExpiration())) {
                    return 1;
                } 
                return 0;
            }
        });
        if(!tokens.isEmpty()) {
            OAuth2AccessToken accessToken = tokens.get(0).getToken();
            return accessToken;
        }
        return null;
    }

    @Override
    public Collection<OAuth2AccessToken> findTokensByClientIdAndUserName(String clientId, String userName) {
        Collection<OAuth2AccessToken> tokens = new ArrayList<OAuth2AccessToken>();
        List<DbAccessToken> result = dbAccessTokenRepository.findByClientIdAndUsername(clientId, userName);
        result.forEach(e-> tokens.add(e.getToken()));
        return tokens;
    }

    @Override
    public Collection<OAuth2AccessToken> findTokensByClientId(String clientId) {
        Collection<OAuth2AccessToken> tokens = new ArrayList<OAuth2AccessToken>();
        List<DbAccessToken> result = dbAccessTokenRepository.findByClientId(clientId);
        result.forEach(e-> tokens.add(e.getToken()));
        return tokens;
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
    
    public List<DbAccessToken> findTokensByUserName(String userName) {
        return (List<DbAccessToken>)dbAccessTokenRepository.findByUsername(userName);
    }
    
    public void revokeAccessToken(String clientId, String username) {
        if(clientId != null) {
            dbAccessTokenRepository.deleteByClientIdAndUsername(clientId,username);
        }
    }
    
    private Optional<DbAccessToken> getExistingAccessTokenByTokenId(OAuth2AccessToken accessToken) {
        return dbAccessTokenRepository.findByTokenId(extractTokenKey(accessToken.getValue()));
    }
    
    private List<DbAccessToken> getAccessTokensByAuthenticationId(OAuth2Authentication authentication) {
        String authenticationId = authenticationKeyGenerator.extractKey(authentication);
        return dbAccessTokenRepository.findByAuthenticationId(authenticationId);
    }
    
    private void deleteAccessTokens(List<DbAccessToken> tokens) {
        for(DbAccessToken token: tokens) {
            logger.debug("Deleting Access Token " + token.getTokenId());
            dbAccessTokenRepository.delete(token);
        }
    }
    
}
