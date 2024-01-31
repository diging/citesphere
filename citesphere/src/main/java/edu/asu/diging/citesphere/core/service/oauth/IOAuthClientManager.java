package edu.asu.diging.citesphere.core.service.oauth;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientRegistrationException;

import edu.asu.diging.citesphere.core.exceptions.CannotFindClientException;
import edu.asu.diging.citesphere.core.model.oauth.impl.OAuthClient;
import edu.asu.diging.citesphere.user.IUser;

public interface IOAuthClientManager {

    ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException;

    OAuthCredentials create(String name, String description, List<OAuthScope> scopes, Set<String> grantTypes, String redirectUrl, List<GrantedAuthority> authorities);

    OAuthClientResultPage getAllClientDetails(Pageable pageable);

    List<OAuthClient> getAllApps();

    void deleteClient(String clientId);

    OAuthCredentials updateClientSecret(String clientId) throws CannotFindClientException;

    List<OAuthClient> getClientsDetails(List<String> clientList);
    
    UserAccessTokenResultPage getAllUserAccessTokenDetails(Pageable pageable, IUser user);

    OAuthCredentials createUserAccessToken(String name, IUser user);
}
