package edu.asu.diging.citesphere.core.model.oauth;

import java.time.OffsetDateTime;

import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

public interface IPersonalAccessToken {

    public String getId();

    public void setId(String id);

    public OAuth2AccessToken getToken();

    public void setToken(OAuth2AccessToken token);
    
    public String getUsername();

    public void setUsername(String username);

    public OffsetDateTime getCreatedOn();

    public void setCreatedOn(OffsetDateTime createdOn);

}