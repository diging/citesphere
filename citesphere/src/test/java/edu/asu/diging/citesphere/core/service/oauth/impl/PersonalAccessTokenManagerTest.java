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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import edu.asu.diging.citesphere.core.exceptions.CannotFindTokenException;
import edu.asu.diging.citesphere.core.model.oauth.IPersonalAccessToken;
import edu.asu.diging.citesphere.core.model.oauth.impl.DbAccessToken;
import edu.asu.diging.citesphere.core.model.oauth.impl.PersonalAccessTokenOAuthClient;
import edu.asu.diging.citesphere.core.repository.oauth.DbAccessTokenRepository;
import edu.asu.diging.citesphere.core.repository.oauth.PersonalAccessTokenOAuthClientRepository;
import edu.asu.diging.citesphere.core.service.oauth.PersonalAccessTokenCredentials;
import edu.asu.diging.citesphere.core.service.oauth.PersonalAccessTokenResultPage;
import edu.asu.diging.citesphere.user.IUser;
import edu.asu.diging.citesphere.user.impl.User;

public class PersonalAccessTokenManagerTest {

    @Mock
    private PersonalAccessTokenOAuthClientRepository patClientRepo;

    @Mock
    private DbAccessTokenRepository accessTokenRepo;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private PersonalAccessTokenManager managerToTest;

    private IUser testUser;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        testUser = new User();
        testUser.setUsername("testuser");
    }

    @Test
    public void test_getTokensForUser_success() {
        Pageable pageable = PageRequest.of(0, 10);

        List<DbAccessToken> mockTokenList = new ArrayList<>();
        DbAccessToken token = new DbAccessToken();
        token.setId("token1");
        token.setName("Test Token");
        token.setUsername("testuser");
        token.setPersonalAccessToken(true);
        mockTokenList.add(token);

        Page<DbAccessToken> mockPage = new PageImpl<>(mockTokenList);

        Mockito.when(accessTokenRepo.findByUsernameAndPersonalAccessToken(
                "testuser", true, pageable))
                .thenReturn(mockPage);

        PersonalAccessTokenResultPage resultPage = managerToTest.getTokensForUser(testUser, pageable);

        Assert.assertEquals(1, resultPage.getTokens().size());
        Assert.assertEquals("Test Token", resultPage.getTokens().get(0).getName());
    }

    @Test
    public void test_getTokensForUser_emptyList() {
        Pageable pageable = PageRequest.of(0, 10);

        List<DbAccessToken> mockTokenList = new ArrayList<>();
        Page<DbAccessToken> mockPage = new PageImpl<>(mockTokenList);

        Mockito.when(accessTokenRepo.findByUsernameAndPersonalAccessToken(
                "testuser", true, pageable))
                .thenReturn(mockPage);

        PersonalAccessTokenResultPage resultPage = managerToTest.getTokensForUser(testUser, pageable);

        Assert.assertEquals(0, resultPage.getTokens().size());
    }

    @Test
    public void test_getTokenById_success() {
        DbAccessToken token = new DbAccessToken();
        token.setId("token1");
        token.setName("Test Token");
        token.setPersonalAccessToken(true);

        Mockito.when(accessTokenRepo.findById("token1"))
                .thenReturn(Optional.of(token));

        IPersonalAccessToken result = managerToTest.getTokenById("token1");

        Assert.assertNotNull(result);
        Assert.assertEquals("Test Token", result.getName());
    }

    @Test
    public void test_getTokenById_notFound() {
        Mockito.when(accessTokenRepo.findById("nonexistent"))
                .thenReturn(Optional.empty());

        IPersonalAccessToken result = managerToTest.getTokenById("nonexistent");

        Assert.assertNull(result);
    }

    @Test
    public void test_getTokenById_notPersonalAccessToken() {
        DbAccessToken token = new DbAccessToken();
        token.setId("token1");
        token.setName("Test Token");
        token.setPersonalAccessToken(false);

        Mockito.when(accessTokenRepo.findById("token1"))
                .thenReturn(Optional.of(token));

        IPersonalAccessToken result = managerToTest.getTokenById("token1");

        Assert.assertNull(result);
    }

    @Test
    public void test_deleteToken_success() throws CannotFindTokenException {
        DbAccessToken token = new DbAccessToken();
        token.setId("token1");
        token.setUsername("testuser");
        token.setPersonalAccessToken(true);

        Mockito.when(accessTokenRepo.findByIdAndUsername("token1", "testuser"))
                .thenReturn(Optional.of(token));

        managerToTest.deleteToken("token1", testUser);

        Mockito.verify(accessTokenRepo).delete(token);
    }

    @Test(expected = CannotFindTokenException.class)
    public void test_deleteToken_notFound() throws CannotFindTokenException {
        Mockito.when(accessTokenRepo.findByIdAndUsername("token1", "testuser"))
                .thenReturn(Optional.empty());

        managerToTest.deleteToken("token1", testUser);
    }

    @Test(expected = CannotFindTokenException.class)
    public void test_deleteToken_notPersonalAccessToken() throws CannotFindTokenException {
        DbAccessToken token = new DbAccessToken();
        token.setId("token1");
        token.setUsername("testuser");
        token.setPersonalAccessToken(false);

        Mockito.when(accessTokenRepo.findByIdAndUsername("token1", "testuser"))
                .thenReturn(Optional.of(token));

        managerToTest.deleteToken("token1", testUser);
    }
}
