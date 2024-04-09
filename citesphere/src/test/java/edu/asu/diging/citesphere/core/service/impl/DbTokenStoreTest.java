package edu.asu.diging.citesphere.core.service.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.security.oauth2.provider.token.AuthenticationKeyGenerator;

import edu.asu.diging.citesphere.core.model.oauth.impl.DbAccessToken;
import edu.asu.diging.citesphere.core.model.oauth.impl.SerializableObjectConverter;
import edu.asu.diging.citesphere.core.repository.oauth.DbAccessTokenRepository;

public class DbTokenStoreTest {
    @Mock
    private DbAccessTokenRepository dbAccessTokenRepository;
    
    @Mock
    private SerializableObjectConverter serializableObjectConverter;
    
    @Mock
    private AuthenticationKeyGenerator authenticationKeyGenerator;
    
    @InjectMocks
    private DbTokenStore managerToTest = new DbTokenStore(dbAccessTokenRepository, null);
    
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }
    
    @Test
    public void test_findTokensByUserName_userNotFound() {
        String username = "name";
        List<DbAccessToken> dbAccessTokenList = new ArrayList<>();
        Mockito.when(dbAccessTokenRepository.findByUsername(username)).thenReturn(dbAccessTokenList);
        Assert.assertEquals(dbAccessTokenList, managerToTest.findTokensByUserName(username));
    }
    
    @Test
    public void test_findTokensByUserName_userFound() {
        String username = "name";
        List<DbAccessToken> dbAccessTokenList = new ArrayList<>();
        DbAccessToken token = new DbAccessToken();
        dbAccessTokenList.add(token);
        Mockito.when(dbAccessTokenRepository.findByUsername(username)).thenReturn(dbAccessTokenList);
        Assert.assertEquals(token, managerToTest.findTokensByUserName(username).get(0));
    }
    
    @Test
    public void test_revokeAccessToken() {
        String username = "name";
        String clientId = "client1";
        managerToTest.revokeAccessToken(clientId,username);
        Mockito.verify(dbAccessTokenRepository).deleteByClientIdAndUsername(clientId,username);
    }
    
    @Test
    public void test_revokeAccessToken_nullClientId() {
        String username = "name";
        managerToTest.revokeAccessToken(null,username);
        Mockito.verify(dbAccessTokenRepository,Mockito.never()).deleteByClientIdAndUsername(null,username);
    }
    
    @Test
    public void test_storeAccessToken_success() {
        OAuth2AccessToken accessToken = new DefaultOAuth2AccessToken("test");
        OAuth2RefreshToken refreshToken = Mockito.mock(OAuth2RefreshToken.class);
        
        Map<String, String> requestParameters = new HashMap<>();
        requestParameters.put("param1", "value1");
        requestParameters.put("param2", "value2");

        String clientId = "yourClientId";
        Set<GrantedAuthority> authorities = new HashSet<>();
        boolean approved = true;
        Set<String> scope = new HashSet<>();
        scope.add("read");
        scope.add("write");
        Set<String> resourceIds = new HashSet<>();
        resourceIds.add("resource1");
        String redirectUri = "http://yourredirecturi.com";
        Set<String> responseTypes = new HashSet<>();
        responseTypes.add("code");
        Map<String, Serializable> extensionProperties = new HashMap<>();
        extensionProperties.put("key1", "value1");
        extensionProperties.put("key2", "value2");
        OAuth2Request oauth2Request = new OAuth2Request(requestParameters, clientId, authorities, approved,
                scope, resourceIds, redirectUri, responseTypes, extensionProperties);
        OAuth2Authentication authentication = new OAuth2Authentication(oauth2Request, null);
        Mockito.when(dbAccessTokenRepository.save(Mockito.any(DbAccessToken.class))).thenReturn(null);
        Mockito.when(authenticationKeyGenerator.extractKey(authentication)).thenReturn("key");
        Mockito.when(dbAccessTokenRepository.findByTokenId(Mockito.anyString())).thenReturn(Optional.empty());
        managerToTest.storeAccessToken(accessToken, authentication);
        Mockito.verify(dbAccessTokenRepository, Mockito.atLeastOnce()).save(Mockito.any(DbAccessToken.class));
    }
    
    @Test
    public void test_storeAccessToken_withExistingToken_success() {
        OAuth2AccessToken accessToken = new DefaultOAuth2AccessToken("test");
        OAuth2RefreshToken refreshToken = Mockito.mock(OAuth2RefreshToken.class);
        
        Map<String, String> requestParameters = new HashMap<>();
        requestParameters.put("param1", "value1");
        requestParameters.put("param2", "value2");

        String clientId = "yourClientId";
        Set<GrantedAuthority> authorities = new HashSet<>();
        boolean approved = true;
        Set<String> scope = new HashSet<>();
        scope.add("read");
        scope.add("write");
        Set<String> resourceIds = new HashSet<>();
        resourceIds.add("resource1");
        String redirectUri = "http://yourredirecturi.com";
        Set<String> responseTypes = new HashSet<>();
        responseTypes.add("code");
        Map<String, Serializable> extensionProperties = new HashMap<>();
        extensionProperties.put("key1", "value1");
        extensionProperties.put("key2", "value2");
        OAuth2Request oauth2Request = new OAuth2Request(requestParameters, clientId, authorities, approved,
                scope, resourceIds, redirectUri, responseTypes, extensionProperties);
        OAuth2Authentication authentication = new OAuth2Authentication(oauth2Request, null);
        Mockito.when(dbAccessTokenRepository.save(Mockito.any(DbAccessToken.class))).thenReturn(null);
        Mockito.when(authenticationKeyGenerator.extractKey(authentication)).thenReturn("key");
        DbAccessToken cat = new DbAccessToken();
        cat.setId(UUID.randomUUID().toString()+UUID.randomUUID().toString());
        cat.setTokenId("tokenId");
        cat.setToken(accessToken);
        cat.setAuthenticationId(authenticationKeyGenerator.extractKey(authentication));
        cat.setUsername(authentication.isClientOnly() ? null : authentication.getName());
        cat.setClientId(authentication.getOAuth2Request().getClientId());
        cat.setAuthentication(authentication);
        cat.setRefreshToken("refreshToken");
        DbAccessToken token = new DbAccessToken();
        token.setId(UUID.randomUUID().toString()+UUID.randomUUID().toString());
        token.setTokenId("tokenId");
        token.setToken(accessToken);
        token.setAuthenticationId(authenticationKeyGenerator.extractKey(authentication));
        token.setUsername(authentication.isClientOnly() ? null : authentication.getName());
        token.setClientId(authentication.getOAuth2Request().getClientId());
        token.setAuthentication(authentication);
        token.setRefreshToken("refreshToken");
        Mockito.when(dbAccessTokenRepository.findByTokenId(Mockito.anyString())).thenReturn(Optional.of(cat));
        Mockito.when(dbAccessTokenRepository.findByAuthenticationId(Mockito.anyString())).thenReturn(List.of(token));
        managerToTest.storeAccessToken(accessToken, authentication);
        Mockito.verify(dbAccessTokenRepository, Mockito.atLeastOnce()).save(Mockito.any(DbAccessToken.class));
        Mockito.verify(dbAccessTokenRepository, Mockito.atLeastOnce()).delete(Mockito.any(DbAccessToken.class));
    }
}