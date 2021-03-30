package edu.asu.diging.citesphere.config.core;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.resource.OAuth2AccessDeniedException;
import org.springframework.security.oauth2.common.exceptions.InvalidClientException;
import org.springframework.security.oauth2.provider.ClientDetails;

import edu.asu.diging.citesphere.core.service.oauth.IOAuthClientManager;

public class ClientCredentialsAuthenticationManager implements AuthenticationManager {

    private IOAuthClientManager clientManager;
    private BCryptPasswordEncoder encoder;
    
    
    public ClientCredentialsAuthenticationManager(IOAuthClientManager clientManager, BCryptPasswordEncoder encoder) {
        super();
        this.clientManager = clientManager;
        this.encoder = encoder;
    }


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (authentication == null) {
            throw new OAuth2AccessDeniedException("No authentication information found.");
        }
        
        String clientId = (String) authentication.getPrincipal();
        ClientDetails details = clientManager.loadClientByClientId(clientId);
        if (details == null) {
            throw new InvalidClientException("Invalid client id: " + clientId);
        }
        
        String secret = (String) authentication.getCredentials();
        if (secret == null) {
            throw new OAuth2AccessDeniedException("No secret given.");
        }
        
        if (encoder.matches(secret, details.getClientSecret())) {
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(clientId, secret, details.getAuthorities());
            auth.setDetails(details);
            return auth;
        }
        
        return authentication;
    }

}
