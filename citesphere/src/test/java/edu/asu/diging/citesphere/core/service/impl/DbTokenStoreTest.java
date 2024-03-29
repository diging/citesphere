package edu.asu.diging.citesphere.core.service.impl;

import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
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
        OAuth2AccessToken accessToken = Mockito.mock(OAuth2AccessToken.class);
        OAuth2RefreshToken refreshToken = Mockito.mock(OAuth2RefreshToken.class);
        OAuth2Authentication authentication = Mockito.mock(OAuth2Authentication.class);
        Mockito.when(accessToken.getRefreshToken()).thenReturn(refreshToken);
        Mockito.when(serializableObjectConverter.serializeToken(accessToken)).thenReturn("token");
        Mockito.when(authenticationKeyGenerator.extractKey(authentication)).thenReturn("key");
        managerToTest.storeAccessToken(accessToken, authentication);
    }
    
    private BaseClientDetails createClientDetails() {
        BaseClientDetails clientDetails = new BaseClientDetails();
        clientDetails.setClientId("test-client-id");
        // Set other properties as needed
        return clientDetails;
    }
    
    private OAuth2Authentication createAuthentication() {
        return Mockito.mock(OAuth2Authentication.class);
    }

}