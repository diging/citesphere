package edu.asu.diging.citesphere.core.zotero.impl;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.support.OAuth1Connection;
import org.springframework.social.oauth1.OAuthToken;
import org.springframework.social.zotero.api.GroupsOperations;
import org.springframework.social.zotero.api.Zotero;
import org.springframework.social.zotero.connect.ZoteroConnectionFactory;
import org.springframework.social.zotero.exception.ZoteroConnectionException;

import edu.asu.diging.citesphere.core.exceptions.ZoteroHttpStatusException;
import edu.asu.diging.citesphere.core.model.IZoteroToken;
import edu.asu.diging.citesphere.core.model.impl.ZoteroToken;
import edu.asu.diging.citesphere.core.service.impl.CitationManager;
import edu.asu.diging.citesphere.model.bib.ICitationGroup;
import edu.asu.diging.citesphere.model.bib.impl.CitationGroup;
import edu.asu.diging.citesphere.user.IUser;
import edu.asu.diging.citesphere.user.impl.User;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.Matchers.*;

public class ZoteroConnectorTest {

    @Mock
    private Zotero zotero;

    @Mock
    private GroupsOperations operations;

    private static String CITATION_KEY = "12";

    private final String GROUP_ID = "12";

    private IUser user;

    private Long currentVersion = new Long(1);

    @Mock
    private ZoteroConnector connectorToTest;

    @Mock
    private ZoteroTokenManager tokenManager;

    @Mock
    private ZoteroConnectionFactory zoteroFactory;

    @Mock
    private Connection<Zotero> connection;

    @Before
    public void setUp() throws ZoteroHttpStatusException {
        MockitoAnnotations.initMocks(this);

        user = new User();
        user.setUsername("USERNAME");

    }

    @Test
    public void test_deleteItem_success() throws ZoteroConnectionException, ZoteroHttpStatusException {
        IZoteroToken token = new ZoteroToken();

        Mockito.when(zotero.getGroupsOperations()).thenReturn(operations);
        Mockito.when(tokenManager.getToken(user)).thenReturn(token);
        Mockito.when(zoteroFactory.createConnection(any(OAuthToken.class))).thenReturn(connection);
        Mockito.when(connection.getApi()).thenReturn(zotero);
        connectorToTest.deleteItem(user, GROUP_ID, CITATION_KEY, currentVersion);

        Mockito.verify(connectorToTest).deleteItem(user, GROUP_ID, CITATION_KEY, currentVersion);

    }
}
