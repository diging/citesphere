package edu.asu.diging.citesphere.core.service.oauth;

import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientRegistrationException;

import edu.asu.diging.citesphere.user.IUser;

public interface IUserTokenManager {

    ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException;

    OAuthCredentials create(String name, IUser user);
    
}
