package edu.asu.diging.citesphere.core.service.oauth.impl;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import edu.asu.diging.citesphere.core.model.oauth.impl.OAuthClient;
import edu.asu.diging.citesphere.core.repository.oauth.OAuthClientRepository;
import edu.asu.diging.citesphere.user.IUser;
import edu.asu.diging.citesphere.user.impl.User;

public class OAuthClientManagerTest {
    
    @Mock
    private OAuthClientRepository clientRepo;
    
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    
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
        IUser user = new User();
        user.setUsername("testUser");
        List<String> clientList = new ArrayList<>();
        clientList.add("Client1");
        List<OAuthClient> clients = new ArrayList<>();
        OAuthClient client1 = new OAuthClient();
        client1.setClientId("Client1");
        client1.setCreatedBy(user);
        clients.add(client1);
        Mockito.when(clientRepo.findAllById(clientList)).thenReturn(clients);
        Assert.assertEquals(client1, managerToTest.getClientsDetails(clientList).get(0));
        Assert.assertEquals(user, managerToTest.getClientsDetails(clientList).get(0).getCreatedBy());
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
    
}
