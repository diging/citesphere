package edu.asu.diging.citesphere.core.service.oauth.impl;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import javax.transaction.Transactional;

import org.javers.common.collections.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidClientException;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.implicit.ImplicitTokenRequest;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.TokenStore;

import edu.asu.diging.citesphere.core.exceptions.CannotFindClientException;
import edu.asu.diging.citesphere.core.model.Role;
import edu.asu.diging.citesphere.core.model.oauth.IOAuthClient;
import edu.asu.diging.citesphere.core.model.oauth.impl.OAuthClient;
import edu.asu.diging.citesphere.core.repository.oauth.OAuthClientRepository;
import edu.asu.diging.citesphere.core.service.oauth.IOAuthClientManager;
import edu.asu.diging.citesphere.core.service.oauth.OAuthClientResultPage;
import edu.asu.diging.citesphere.core.service.oauth.OAuthCredentials;
import edu.asu.diging.citesphere.core.service.oauth.OAuthScope;
import edu.asu.diging.citesphere.core.service.oauth.UserAccessTokenResultPage;
import edu.asu.diging.citesphere.core.user.IUserManager;
import edu.asu.diging.citesphere.user.IUser;

@Transactional
public class OAuthClientManager implements ClientDetailsService, IOAuthClientManager {

    private OAuthClientRepository clientRepo;
    
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    
    private int accessTokenValidity;
    
    private OAuth2RequestFactory requestFactory;
    
    @Autowired
    private TokenStore tokenStore;
    
    @Autowired
    private ClientDetailsService clientDetailsService;
    
    @Autowired
    private IUserManager userManager;
       
    public OAuthClientManager(OAuthClientRepository repo, BCryptPasswordEncoder bCryptPasswordEncoder, int accessTokenValidity) {
        this.clientRepo = repo;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.accessTokenValidity = accessTokenValidity;
        this.requestFactory = new DefaultOAuth2RequestFactory(clientDetailsService);
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
            for (String scope : details.getScope()) {
                // load authorities, ugly but best I can come up with right now
            }
            return details;
        } 
        throw new InvalidClientException("Client with id " + clientId + " does not exist.");
    }
    
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.service.oauth.impl.IOAuthClientManager#store(org.springframework.security.oauth2.provider.ClientDetails)
     */
    @Override
    public OAuthCredentials create(String name, String description, List<OAuthScope> scopes, Set<String> grantTypes, String redirectUrl, List<GrantedAuthority> authorities) {
        final OAuthClient client = new OAuthClient();
        client.setName(name);
        client.setDescription(description);
        String clientSecret = UUID.randomUUID().toString();
        client.setClientSecret(bCryptPasswordEncoder.encode(clientSecret));
        client.setAuthorizedGrantTypes(new HashSet<>());
        client.getAuthorizedGrantTypes().addAll(grantTypes);
        client.setAccessTokenValiditySeconds(accessTokenValidity);
        client.setScope(new HashSet<>());
        client.setRegisteredRedirectUri(new HashSet<>());
        client.getRegisteredRedirectUri().add(redirectUrl);
        client.setAuthorities(authorities);
        scopes.forEach(s -> client.getScope().add(s.getScope()));
        OAuthClient storeClient = clientRepo.save(client);
        return new OAuthCredentials(storeClient.getClientId(), clientSecret);
    }
    
    @Override
    public OAuthClientResultPage getAllClientDetails(Pageable pageable) {
        List<IOAuthClient> clientList = new ArrayList<>();
        Page<OAuthClient> oAuthClients = clientRepo.findAll(pageable);
        oAuthClients.forEach(oAuthClient -> clientList.add(oAuthClient));
        OAuthClientResultPage result = new OAuthClientResultPage();
        result.setClientList(clientList);
        result.setTotalPages(oAuthClients.getTotalPages());
        return result;
        
    }
    
    @Override
    public List<OAuthClient> getAllApps() {
        return clientRepo.findAll();
    }
    
    @Override
    public void deleteClient(String clientId) {
        if(clientId != null) {
            clientRepo.deleteById(clientId);
        }
    }
    
    @Override
    public OAuthCredentials updateClientSecret(String clientId) throws CannotFindClientException {
        Optional<OAuthClient> clientOptional = clientRepo.findById(clientId);
        if (clientOptional.isPresent()) {
            OAuthClient client = clientOptional.get();
            if (client.getIsUserAccessToken()) {
                IUser user = userManager.findByUsername(client.getCreatedByUsername());
                OAuth2AccessToken accessToken = createAccessToken(client.getClientId(), user);
                client.setClientSecret(bCryptPasswordEncoder.encode(accessToken.getValue()));
                OAuthClient storeClient = clientRepo.save(client);
                return new OAuthCredentials(storeClient.getClientId(), accessToken.getValue());
            }
            String clientSecret = UUID.randomUUID().toString();
            client.setClientSecret(bCryptPasswordEncoder.encode(clientSecret));
            OAuthClient storeClient = clientRepo.save(client);
            return new OAuthCredentials(storeClient.getClientId(), clientSecret);
        }
        throw new CannotFindClientException("Client with id " + clientId + " does not exist.");
    }
    
    @Override
    public List<OAuthClient> getClientsDetails(List<String> clientList){
        List<OAuthClient> clients = new ArrayList<>();
        if(clientList != null && clientList.size()>0) {
            clients = clientRepo.findAllById(clientList);
        }
        return clients;
    }

    @Override
    public UserAccessTokenResultPage getAllUserAccessTokenDetails(Pageable pageable, IUser user) {
        List<IOAuthClient> clientList = new ArrayList<>();
        Page<OAuthClient> userAccessClients = clientRepo.findByIsUserAccessTokenAndCreatedByUsername(true, user.getUsername(), pageable);
        userAccessClients.forEach(oAuthClient -> clientList.add(oAuthClient));
        UserAccessTokenResultPage result = new UserAccessTokenResultPage();
        result.setClientList(clientList);
        result.setTotalPages(userAccessClients.getTotalPages());
        return result;
    }
    
    @Override
    public OAuthCredentials createUserAccessToken(String name, IUser user) {
        final OAuthClient client = new OAuthClient();
        client.setName(name);
        String clientSecret = UUID.randomUUID().toString();
        client.setClientSecret(bCryptPasswordEncoder.encode(clientSecret));          
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(Role.TRUSTED_CLIENT));
        client.setAuthorities(authorities);
        client.setScope(new HashSet<>());
        client.getScope().add(OAuthScope.READ.getScope());
        client.setCreatedByUsername(user.getUsername());
        client.setisUserAccessToken(true);
        OAuthClient storeClient = clientRepo.save(client);
        OAuth2AccessToken accessToken = createAccessToken(storeClient.getClientId(), user);
        storeClient.setClientSecret(bCryptPasswordEncoder.encode(accessToken.getValue()));
        clientRepo.save(storeClient);
        return new OAuthCredentials(storeClient.getClientId(), accessToken.getValue());
    }
    
    private OAuth2AccessToken createAccessToken(String clientId, IUser user) {
        DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken(UUID.randomUUID().toString());
        token.setScope(Sets.asSet(OAuthScope.READ.getScope()));
        AuthorizationRequest request = new AuthorizationRequest(clientId, token.getScope());
        TokenRequest implicitRequest = new ImplicitTokenRequest(requestFactory.createTokenRequest(request, "implicit"), requestFactory.createOAuth2Request(request));
        OAuth2Authentication authentication = getOAuth2Authentication(clientDetailsService.loadClientByClientId(clientId), implicitRequest, user);
        tokenStore.storeAccessToken(token, authentication);
        System.out.println(extractTokenKey(token.getValue()));
        return token;
    }
    
    private OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest, IUser user) {
        OAuth2Request storedOAuth2Request = requestFactory.createOAuth2Request(client, tokenRequest);
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, user.getRoles());
        return new OAuth2Authentication(storedOAuth2Request, authentication);
    }
    
    private String extractTokenKey(String value) {
        if(value == null) {
            return null;
        } else {
            MessageDigest digest;
            try {
                digest = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException var5) {
                throw new IllegalStateException("MD5 algorithm not available.  Fatal (should be in the JDK).");
            }

            try {
                byte[] e = digest.digest(value.getBytes("UTF-8"));
                return String.format("%032x", new Object[]{new BigInteger(1, e)});
            } catch (UnsupportedEncodingException var4) {
                throw new IllegalStateException("UTF-8 encoding not available.  Fatal (should be in the JDK).");
            }
        }
    }
}
