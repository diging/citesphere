package edu.asu.diging.citesphere.core.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.Authentication;

import edu.asu.diging.citesphere.core.model.oauth.IPersonalAccessToken;

public interface IPersonalAccessTokenManager {

    public String savePersonalAccessToken(String username);

    List<IPersonalAccessToken> getPersonalAccessTokens(String username);
    
    public Optional<Authentication> verifyToken(Optional<String> token);
}