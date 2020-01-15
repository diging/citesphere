package edu.asu.diging.citesphere.core.service.oauth.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import edu.asu.diging.citesphere.core.repository.oauth.OAuthClientRepository;

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
        Mockito.doNothing().when(clientRepo).deleteById(clientId);
        managerToTest.deleteClient(clientId);
        Mockito.verify(clientRepo).deleteById(clientId);
    }
    
    @Test
    public void test_deleteClient_nullId() {
        String clientId = null;
        managerToTest.deleteClient(clientId);
        Mockito.verify(clientRepo, Mockito.never()).deleteById(clientId);
    }

}
