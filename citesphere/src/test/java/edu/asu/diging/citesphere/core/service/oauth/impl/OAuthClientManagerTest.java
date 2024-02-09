package edu.asu.diging.citesphere.core.service.oauth.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.TokenStore;

import edu.asu.diging.citesphere.core.exceptions.CannotFindClientException;
import edu.asu.diging.citesphere.core.model.oauth.impl.OAuthClient;
import edu.asu.diging.citesphere.core.repository.oauth.OAuthClientRepository;
import edu.asu.diging.citesphere.core.service.oauth.OAuthCredentials;
import edu.asu.diging.citesphere.core.service.oauth.UserAccessTokenResultPage;
import edu.asu.diging.citesphere.core.user.IUserManager;
import edu.asu.diging.citesphere.user.IUser;
import edu.asu.diging.citesphere.user.impl.User;

public class OAuthClientManagerTest {
    
    @Mock
    private OAuthClientRepository clientRepo;
    
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    
    @Mock
    private TokenStore tokenStore;
    
    @Mock
    private ClientDetailsService clientDetailsService;
    
    @Mock
    private IUserManager userManager;
    
    @Mock
    private OAuth2RequestFactory requestFactory;
    
    @Mock
    private DefaultOAuth2RequestFactory defaultOAuth2RequestFactory;
    
    private int accessTokenValidity = 3600;
    
    @InjectMocks
    private OAuthClientManager managerToTest = new OAuthClientManager(clientRepo, bCryptPasswordEncoder, accessTokenValidity);
    
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }
    
    @Test
    public void test_deleteClient() {
        String clientId = "clientId";
        managerToTest.deleteClient(clientId);
        Mockito.verify(clientRepo).deleteById(clientId);
    }
    
    @Test
    public void test_deleteClient_nullId() {
        String clientId = null;
        managerToTest.deleteClient(clientId);
        Mockito.verify(clientRepo, Mockito.never()).deleteById(clientId);
    }
    
    @Test
    public void test_getClientsDetails() {
        List<String> clientList = new ArrayList<>();
        clientList.add("Client1");
        List<OAuthClient> clients = new ArrayList<>();
        OAuthClient client1 = new OAuthClient();
        client1.setClientId("Client1");
        clients.add(client1);
        Mockito.when(clientRepo.findAllById(clientList)).thenReturn(clients);
        Assert.assertEquals(client1, managerToTest.getClientsDetails(clientList).get(0));
    }
    
    @Test
    public void test_getClientsDetails_clientNotFound() {
        List<String> clientList = new ArrayList<>();
        clientList.add("Client1");
        Mockito.when(clientRepo.findAllById(clientList)).thenReturn(new ArrayList<>());
        Assert.assertEquals(0, managerToTest.getClientsDetails(clientList).size());
    }
    
    @Test
    public void test_getClientsDetails_fewClientsNotFound() {
        List<String> clientList = new ArrayList<>();
        clientList.add("Client1");
        clientList.add("Client2");
        List<OAuthClient> clients = new ArrayList<>();
        OAuthClient client1 = new OAuthClient();
        client1.setClientId("Client1");
        clients.add(client1);
        Mockito.when(clientRepo.findAllById(clientList)).thenReturn(clients);
        Assert.assertEquals(1, managerToTest.getClientsDetails(clientList).size());
        Assert.assertEquals(client1, managerToTest.getClientsDetails(clientList).get(0));
    }
    
    @Test
    public void test_getClientsDetails_emptyList() {
        Assert.assertEquals(0, managerToTest.getClientsDetails(new ArrayList<>()).size());
    }

    @Test
    public void test_getAllApps() {
        List<OAuthClient> clientList = new ArrayList<>();
        OAuthClient client1 = new OAuthClient();
        OAuthClient client2 = new OAuthClient();
        clientList.add(client1);
        clientList.add(client2);
        Mockito.when(clientRepo.findAll()).thenReturn(clientList);
        List<OAuthClient> apps = managerToTest.getAllApps();
        Assert.assertEquals(clientList.size(), apps.size());
        clientList.forEach(app -> Assert.assertTrue(apps.contains(app)));
    }
    
    @Test
    public void test_getAllApps_emptyList() {
        Assert.assertEquals(0, managerToTest.getAllApps().size());
    }
    
    @Test
    public void test_getAllUserAccessTokenDetails_success() {
        Pageable pageable = PageRequest.of(0, 10);
        IUser user = new User();
        user.setUsername("testuser");

        List<OAuthClient> mockClientList = new ArrayList<>();
        mockClientList.add(new OAuthClient());

        Page<OAuthClient> mockPage = new PageImpl<>(mockClientList);

        Mockito.when(clientRepo.findByIsUserAccessTokenAndCreatedByUsername(
                true, "testuser", pageable))
                .thenReturn(mockPage);

        UserAccessTokenResultPage resultPage = managerToTest.getAllUserAccessTokenDetails(pageable, user);

        Assert.assertEquals(1, resultPage.getClientList().size());
    }
    
    @Test
    public void test_getAllUserAccessTokenDetails_returnsEmptyList() {
        Pageable pageable = PageRequest.of(0, 10);
        IUser user = new User();
        user.setUsername("testuser");

        List<OAuthClient> mockClientList = new ArrayList<>();
        Page<OAuthClient> mockPage = new PageImpl<>(mockClientList);

        Mockito.when(clientRepo.findByIsUserAccessTokenAndCreatedByUsername(
                true, "testuser", pageable))
                .thenReturn(mockPage);

        UserAccessTokenResultPage resultPage = managerToTest.getAllUserAccessTokenDetails(pageable, user);

        Assert.assertEquals(0, resultPage.getClientList().size());
    }
}
