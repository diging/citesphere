package edu.asu.diging.citesphere.core.model.oauth.impl;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;

import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

/**
 * Modeled after:
 * https://blog.couchbase.com/custom-token-store-spring-securtiy-oauth2/
 * @author jdamerow
 *
 */
@Entity
public class DbRefreshToken {
 
    @Id
    private String id;
    private String tokenId;
    @Lob
    private String token;
    @Lob
    private String authentication;
 
    
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

    public OAuth2RefreshToken getToken() {
        return SerializableObjectConverter.deserializeRefreshToken(token);
    }

    public void setToken(OAuth2RefreshToken token) {
        this.token = SerializableObjectConverter.serializeRefreshToken(token);
    }

    public void setAuthentication(String authentication) {
        this.authentication = authentication;
    }

    public OAuth2Authentication getAuthentication() {
        return SerializableObjectConverter.deserialize(authentication);
    }
 
    public void setAuthentication(OAuth2Authentication authentication) {
        this.authentication = SerializableObjectConverter.serialize(authentication);
    }
}