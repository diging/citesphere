package edu.asu.diging.citesphere.core.service.oauth;

import java.util.List;

import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientRegistrationException;

public interface IOAuthClientManager {

    ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException;

    OAuthCredentials create(String name, String description, List<OAuthScope> scopes);

}