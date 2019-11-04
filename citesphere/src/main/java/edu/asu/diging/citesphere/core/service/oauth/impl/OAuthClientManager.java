package edu.asu.diging.citesphere.core.service.oauth.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;

import edu.asu.diging.citesphere.core.model.IOAuthClient;
import edu.asu.diging.citesphere.core.model.bib.ICitation;
import edu.asu.diging.citesphere.core.model.bib.IConceptType;
import edu.asu.diging.citesphere.core.model.oauth.OAuthClient;
import edu.asu.diging.citesphere.core.model.oauth.OAuthClientCollectionResult;
import edu.asu.diging.citesphere.core.repository.oauth.OAuthClientRepository;
import edu.asu.diging.citesphere.core.service.oauth.GrantTypes;
import edu.asu.diging.citesphere.core.service.oauth.IOAuthClientManager;
import edu.asu.diging.citesphere.core.service.oauth.OAuthCredentials;
import edu.asu.diging.citesphere.core.service.oauth.OAuthScope;

@Transactional
public class OAuthClientManager implements ClientDetailsService, IOAuthClientManager {

    private OAuthClientRepository clientRepo;
    
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    
    private int accessTokenValidity;
       
    public OAuthClientManager(OAuthClientRepository repo, BCryptPasswordEncoder bCryptPasswordEncoder, int accessTokenValidity) {
        this.clientRepo = repo;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.accessTokenValidity = accessTokenValidity;
    }

    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.service.oauth.impl.IOAuthClientManager#loadClientByClientId(java.lang.String)
     */
    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        Optional<OAuthClient> clientOptional = clientRepo.findById(clientId);
        if (clientOptional.isPresent()) {
            ClientDetails details = clientOptional.get();
            details.getAuthorities().size();
            return details;
        }
        return null;
    }
    
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.service.oauth.impl.IOAuthClientManager#store(org.springframework.security.oauth2.provider.ClientDetails)
     */
    @Override
    public OAuthCredentials create(String name, String description, List<OAuthScope> scopes) {
        final OAuthClient client = new OAuthClient();
        client.setName(name);
        client.setDescription(description);
        String clientSecret = UUID.randomUUID().toString();
        client.setClientSecret(bCryptPasswordEncoder.encode(clientSecret));
        client.setAuthorizedGrantTypes(new HashSet<>());
        client.getAuthorizedGrantTypes().add(GrantTypes.CLIENT_CREDENTIALS);
        client.setAccessTokenValiditySeconds(accessTokenValidity);
        client.setScope(new HashSet<>());
        scopes.forEach(s -> client.getScope().add(s.getScope()));
        OAuthClient storeClient = clientRepo.save(client);
        return new OAuthCredentials(storeClient.getClientId(), clientSecret);
    }
    
    @Override
    public OAuthClientCollectionResult getClientDetails(Pageable pageable) {
        OAuthClientCollectionResult result = new OAuthClientCollectionResult();
        List<IOAuthClient> clientList = new ArrayList<>();
        Page<OAuthClient> oAuthClients = clientRepo.findAll(pageable);
        oAuthClients.forEach(oAuthClient -> clientList.add(oAuthClient));
        result.setClientList(clientList);
        result.setTotalPages(oAuthClients.getTotalPages());
        return result;
        
    }
    
}
