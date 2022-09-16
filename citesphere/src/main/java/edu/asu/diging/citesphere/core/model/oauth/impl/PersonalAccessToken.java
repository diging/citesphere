package edu.asu.diging.citesphere.core.model.oauth.impl;

import java.time.OffsetDateTime;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;

import org.springframework.security.oauth2.common.OAuth2AccessToken;

import edu.asu.diging.citesphere.core.model.oauth.IPersonalAccessToken;

@Entity
public class PersonalAccessToken implements IPersonalAccessToken {
 
    @Id
    private String id;
    @Lob
    private String token;
    private String username;
    private OffsetDateTime createdOn;
    
    @Override
    public String getId() {
        return id;
    }
    
    @Override
    public void setId(String id) {
        this.id = id;
    }
    // public String getToken() {
    //     return token;
    // }
    // public void setToken(String token) {
    //     this.token = token;
    // }

    @Override
    public OAuth2AccessToken getToken() {
        return SerializableObjectConverter.deserializeToken(token);
    }
    
    @Override
    public void setToken(OAuth2AccessToken token) {
        this.token = SerializableObjectConverter.serializeToken(token);
    }
    
    @Override
    public String getUsername() {
        return username;
    }
    
    @Override
    public void setUsername(String username) {
        this.username = username;
    }
    
    @Override
    public OffsetDateTime getCreatedOn() {
        return createdOn;
    }
    
    @Override
    public void setCreatedOn(OffsetDateTime createdOn) {
        this.createdOn = createdOn;
    }

}