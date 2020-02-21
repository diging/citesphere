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
import edu.asu.diging.citesphere.core.model.oauth.impl.DbAccessToken;
import edu.asu.diging.citesphere.core.repository.oauth.DbAccessTokenRepository;

public class DbTokenStoreTest {
    @Mock
    private DbAccessTokenRepository dbAccessTokenRepository;
    
    @InjectMocks
    private DbTokenStore managerToTest = new DbTokenStore(dbAccessTokenRepository, null);
    
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }
    
    @Test
    public void test_findTokensByUserName() {
        String username = "name";
        List<DbAccessToken> dbAccessTokenList = new ArrayList<>();
        Mockito.when(dbAccessTokenRepository.findByUsername(username)).thenReturn(dbAccessTokenList);
        Assert.assertEquals(dbAccessTokenList, managerToTest.findTokensByUserName(username));
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

}
