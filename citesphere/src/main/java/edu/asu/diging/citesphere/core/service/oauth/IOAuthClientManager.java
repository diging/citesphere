package edu.asu.diging.citesphere.core.service.oauth;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientRegistrationException;

public interface IOAuthClientManager {

    ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException;

    OAuthCredentials create(String name, String description, List<OAuthScope> scopes, Set<String> grantTypes, String redirectUrl);

    OAuthClientResultPage getAllClientDetails(Pageable pageable);

    void deleteClient(String clientId);

}