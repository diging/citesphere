package edu.asu.diging.citesphere.core.model.oauth;

import java.time.OffsetDateTime;

import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

public interface IPersonalAccessToken {

    public Long getId();

    public void setId(Long id);

    public String getToken();

    public void setToken(String token);
    
    public String getUsername();

    public void setUsername(String username);

    public OffsetDateTime getCreatedOn();

    public void setCreatedOn(OffsetDateTime createdOn);

}