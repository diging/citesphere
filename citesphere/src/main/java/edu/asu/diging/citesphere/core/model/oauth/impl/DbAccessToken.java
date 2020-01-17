package edu.asu.diging.citesphere.core.model.oauth.impl;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;

import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

/**
 * Modeled after:
 * https://blog.couchbase.com/custom-token-store-spring-securtiy-oauth2/
 * @author jdamerow
 *
 */
@Entity
public class DbAccessToken {
 
    @Id
    private String id;
    private String tokenId;
    @Lob
    private String token;
    private String authenticationId;
    private String username;
    private String clientId;
    @Lob
    private String authentication;
    @Lob
    private String refreshToken;
 
 
    public OAuth2Authentication getAuthentication() {
        return SerializableObjectConverter.deserialize(authentication);
    }
 
    public void setAuthentication(OAuth2Authentication authentication) {
        this.authentication = SerializableObjectConverter.serialize(authentication);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public OAuth2AccessToken getToken() {
        return SerializableObjectConverter.deserializeToken(token);
    }

    public void setToken(OAuth2AccessToken token) {
        this.token = SerializableObjectConverter.serializeToken(token);
    }

    public String getAuthenticationId() {
        return authenticationId;
    }

    public void setAuthenticationId(String authenticationId) {
        this.authenticationId = authenticationId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void setAuthentication(String authentication) {
        this.authentication = authentication;
    }
 
}