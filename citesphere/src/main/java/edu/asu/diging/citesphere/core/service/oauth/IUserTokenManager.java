package edu.asu.diging.citesphere.core.service.oauth;

import org.springframework.security.oauth2.common.OAuth2AccessToken;

import edu.asu.diging.citesphere.user.IUser;

public interface IUserTokenManager {
    OAuth2AccessToken getAccessToken(IUser user);
}
