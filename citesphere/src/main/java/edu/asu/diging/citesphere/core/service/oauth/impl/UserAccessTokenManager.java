package edu.asu.diging.citesphere.core.service.oauth.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import org.javers.common.collections.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
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
import org.springframework.stereotype.Service;

import edu.asu.diging.citesphere.core.exceptions.CannotFindClientException;
import edu.asu.diging.citesphere.core.model.Role;
import edu.asu.diging.citesphere.core.model.oauth.IOAuthClient;
import edu.asu.diging.citesphere.core.model.oauth.IUserAccessToken;
import edu.asu.diging.citesphere.core.model.oauth.impl.OAuthClient;
import edu.asu.diging.citesphere.core.model.oauth.impl.UserAccessToken;
import edu.asu.diging.citesphere.core.repository.oauth.UserAccessTokenRepository;
import edu.asu.diging.citesphere.core.service.oauth.IUserTokenManager;
import edu.asu.diging.citesphere.core.service.oauth.OAuthClientResultPage;
import edu.asu.diging.citesphere.core.service.oauth.OAuthCredentials;
import edu.asu.diging.citesphere.core.service.oauth.OAuthScope;
import edu.asu.diging.citesphere.core.service.oauth.UserAccessTokenResultPage;
import edu.asu.diging.citesphere.user.IUser;

@Service
@PropertySource({ "classpath:config.properties", "${appConfigFile:classpath:}/app.properties" })
@SuppressWarnings("deprecation")
@Transactional
public class UserAccessTokenManager implements IUserTokenManager {
 
    @Value("${_citephere_oauth2_app_clientid}")
    private String citesphereClientId;
    
    @Autowired
    private UserAccessTokenRepository userAccessTokenRepository;
    
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    
    @Autowired
    private ClientDetailsService clientDetailsService;
    
    private OAuth2RequestFactory requestFactory;
    
    @PostConstruct
    public void init() {
        this.requestFactory = new DefaultOAuth2RequestFactory(clientDetailsService);
    }
    
    @Override
    public UserAccessToken loadClientByClientId(String clientId) throws ClientRegistrationException {
        Optional<UserAccessToken> userAccessTokenOptional = userAccessTokenRepository.findById(clientId);
        if (userAccessTokenOptional.isPresent()) {
            UserAccessToken details = (UserAccessToken) userAccessTokenOptional.get();
            return details;
        }
        throw new InvalidClientException("Client with id " + clientId + " does not exist.");
    }
    
    @Override
    public OAuthCredentials create(String name, IUser user) {
        final UserAccessToken userAccessToken = new UserAccessToken();
        userAccessToken.setName(name);   
        String token = UUID.randomUUID().toString();
//        userAccessToken.setToken(bCryptPasswordEncoder.encode(token));
        DefaultOAuth2AccessToken accessToken = new DefaultOAuth2AccessToken(UUID.randomUUID().toString());
        accessToken.setScope(Sets.asSet(OAuthScope.READ.getScope()));
        AuthorizationRequest request = new AuthorizationRequest(citesphereClientId, accessToken.getScope());
        TokenRequest implicitRequest = new ImplicitTokenRequest(requestFactory.createTokenRequest(request, "implicit"), requestFactory.createOAuth2Request(request));
        OAuth2Authentication authentication = getOAuth2Authentication(clientDetailsService.loadClientByClientId(citesphereClientId), implicitRequest, user);
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(Role.TRUSTED_CLIENT));
        userAccessToken.setAuthorities(authorities);
        userAccessToken.setScope(new HashSet<>());
        userAccessToken.getScope().add(OAuthScope.READ.getScope());
        userAccessToken.setUser(user);
        UserAccessToken storeToken = userAccessTokenRepository.save(userAccessToken);
        
        return new OAuthCredentials(storeToken.getClientId(), token);
    }
    
    private OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest, IUser user) {
        OAuth2Request storedOAuth2Request = requestFactory.createOAuth2Request(client, tokenRequest);
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, user.getRoles());
        return new OAuth2Authentication(storedOAuth2Request, authentication);
    }
    
    @Override
    public UserAccessTokenResultPage getAllAccessTokensDetails(Pageable pageable, IUser user) {
        List<IUserAccessToken> userAccessTokenList = new ArrayList<>();
        Page<UserAccessToken> userAccessTokens = userAccessTokenRepository.findAllByUser(user, pageable);
        userAccessTokens.forEach(userAccessToken -> userAccessTokenList.add(userAccessToken));
        UserAccessTokenResultPage result = new UserAccessTokenResultPage();
        result.setClientList(userAccessTokenList);
        result.setTotalPages(userAccessTokens.getTotalPages());
        return result;
    }
    
    @Override
    public List<UserAccessToken> getAllUserAccessTokenDetails(IUser user) {
        return userAccessTokenRepository.findAllByUser(user);
    }
    
    @Override
    public void deleteClient(String clientId) {
        if(clientId != null) {
            userAccessTokenRepository.deleteById(clientId);
        }
    }
    
    @Override
    public OAuthCredentials updateSecret(String clientId) throws CannotFindClientException {
        Optional<UserAccessToken> userAccessTokenOptional = userAccessTokenRepository.findById(clientId);
        if (userAccessTokenOptional.isPresent()) {
            UserAccessToken accessToken = userAccessTokenOptional.get();
            String token = UUID.randomUUID().toString();
            accessToken.setToken(bCryptPasswordEncoder.encode(token));
            UserAccessToken storeAccessToken = userAccessTokenRepository.save(accessToken);
            return new OAuthCredentials(storeAccessToken.getClientId(), token);
        }
        throw new CannotFindClientException("Client with id " + clientId + " does not exist.");
    }
}
