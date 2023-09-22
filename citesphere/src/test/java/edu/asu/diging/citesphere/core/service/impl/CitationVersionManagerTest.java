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
import org.springframework.social.zotero.exception.ZoteroConnectionException;

import edu.asu.diging.citesphere.core.bib.ICitationVersionsDao;
import edu.asu.diging.citesphere.core.exceptions.AccessForbiddenException;
import edu.asu.diging.citesphere.core.exceptions.CannotFindCitationException;
import edu.asu.diging.citesphere.core.exceptions.CannotFindCitationVersionException;
import edu.asu.diging.citesphere.core.exceptions.CitationIsOutdatedException;
import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroHttpStatusException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroItemCreationFailedException;
import edu.asu.diging.citesphere.core.model.bib.CitationVersion;
import edu.asu.diging.citesphere.model.bib.ICitation;
import edu.asu.diging.citesphere.model.bib.ICitationGroup;
import edu.asu.diging.citesphere.model.bib.impl.Citation;
import edu.asu.diging.citesphere.model.bib.impl.CitationGroup;
import edu.asu.diging.citesphere.user.IUser;
import edu.asu.diging.citesphere.user.impl.User;

public class CitationVersionManagerTest {

    @Mock
    private ICitationVersionsDao citationVersionsDao;

    @Mock
    private GroupManager groupManager;

    @Mock
    private CitationManager citationManager;

    @InjectMocks
    private CitationVersionManager managerToTest;

    private final String EXISTING_ID = "ID";

    private final String GROUP_ID = "12";
    private IUser user;
    ICitationGroup group;

    private final int PAGE = 0;
    private final int PAGESIZE = 10;
    private final long CITATION_VERSION_1 = 1;
    private final long CITATION_VERSION_2 = 2;
    private final long CITATION_VERSION_3 = 3;
    private final String TITLE_1 = "title1";
    private final String TITLE_2 = "title2";
    private ICitation citation1;
    private ICitation citation2;
    private CitationVersion citationVersion1;
    private CitationVersion citationVersion2;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        user = new User();
        user.setUsername("USERNAME");

        group = new CitationGroup();
        group.setGroupId(new Long(GROUP_ID));
        group.getUsers().add(user.getUsername());
        Mockito.when(groupManager.getGroup(user, GROUP_ID)).thenReturn(group);

        citation1 = new Citation();
        citation1.setKey(EXISTING_ID);
        citation1.setTitle(TITLE_1);
        citationVersion1 = new CitationVersion();
        citationVersion1.setKey(EXISTING_ID);
        citationVersion1.setVersion(CITATION_VERSION_1);

        citation2 = new Citation();
        citation2.setKey(EXISTING_ID);
        citation2.setTitle(TITLE_2);
        citationVersion2 = new CitationVersion();
        citationVersion2.setKey(EXISTING_ID);
        citationVersion2.setVersion(CITATION_VERSION_2);
    }

    @Test
    public void test_getCitationVersions_inDb() throws AccessForbiddenException, GroupDoesNotExistException {
        List<CitationVersion> citationVersions = new ArrayList<>();
        citationVersions.add(citationVersion1);
        citationVersions.add(citationVersion2);
        Mockito.when(citationVersionsDao.getVersions(GROUP_ID, EXISTING_ID, PAGE, PAGESIZE))
                .thenReturn(citationVersions);
        List<CitationVersion> actual = managerToTest.getCitationVersions(user, GROUP_ID, EXISTING_ID, PAGE, PAGESIZE);
        Assert.assertEquals(2, actual.size());
        for (CitationVersion version : actual) {
            Assert.assertEquals(EXISTING_ID, version.getKey());
        }
    }

    @Test
    public void test_getCitationVersions_doesNotExist() throws AccessForbiddenException, GroupDoesNotExistException {
        List<CitationVersion> actualEmpty = managerToTest.getCitationVersions(user, GROUP_ID, EXISTING_ID, PAGE,
                PAGESIZE);
        Assert.assertTrue(actualEmpty.isEmpty());
    }

    @Test
    public void test_getTotalCitationVersionPages_exists() {
        Mockito.when(citationVersionsDao.getTotalCount(GROUP_ID, EXISTING_ID)).thenReturn(11);
        Assert.assertEquals(2, managerToTest.getTotalCitationVersionPages(GROUP_ID, EXISTING_ID, PAGESIZE));
    }

    @Test
    public void test_getTotalCitationVersionPages_empty() {
        Mockito.when(citationVersionsDao.getTotalCount(GROUP_ID, EXISTING_ID)).thenReturn(0);
        Assert.assertEquals(0, managerToTest.getTotalCitationVersionPages(GROUP_ID, EXISTING_ID, PAGESIZE));
    }

    @Test
    public void test_getCitationVersion_exists() throws AccessForbiddenException, GroupDoesNotExistException {
        Mockito.when(managerToTest.getCitationVersion(user, GROUP_ID, EXISTING_ID, CITATION_VERSION_1))
                .thenReturn(citation1);
        Mockito.when(managerToTest.getCitationVersion(user, GROUP_ID, EXISTING_ID, CITATION_VERSION_2))
                .thenReturn(citation2);

        ICitation actual1 = managerToTest.getCitationVersion(user, GROUP_ID, EXISTING_ID, CITATION_VERSION_1);
        Assert.assertEquals(EXISTING_ID, actual1.getKey());
        Assert.assertEquals(TITLE_1, actual1.getTitle());

        ICitation actual2 = managerToTest.getCitationVersion(user, GROUP_ID, EXISTING_ID, CITATION_VERSION_2);
        Assert.assertEquals(EXISTING_ID, actual2.getKey());
        Assert.assertEquals(TITLE_2, actual2.getTitle());
    }

    @Test
    public void test_getCitationVersion_doesNotExist() throws AccessForbiddenException, GroupDoesNotExistException {
        ICitation actual = managerToTest.getCitationVersion(user, GROUP_ID, EXISTING_ID, CITATION_VERSION_3);
        Assert.assertNull(actual);
    }

    @Test(expected = GroupDoesNotExistException.class)
    public void test_revertCitationVersion_noSuchGroup()
            throws GroupDoesNotExistException, ZoteroConnectionException, CitationIsOutdatedException,
            ZoteroHttpStatusException, CannotFindCitationVersionException, CannotFindCitationException, ZoteroItemCreationFailedException {
        Mockito.when(groupManager.getGroup(user, GROUP_ID)).thenReturn(null);
        managerToTest.revertCitationVersion(user, GROUP_ID, EXISTING_ID, CITATION_VERSION_1);
    }

    @Test(expected = CannotFindCitationException.class)
    public void test_revertCitationVersion_noCitation()
            throws GroupDoesNotExistException, ZoteroConnectionException, CitationIsOutdatedException,
            ZoteroHttpStatusException, CannotFindCitationVersionException, CannotFindCitationException, ZoteroItemCreationFailedException {
        Mockito.when(groupManager.getGroup(user, GROUP_ID)).thenReturn(group);
        Mockito.when(citationManager.getCitation(EXISTING_ID)).thenReturn(null);
        managerToTest.revertCitationVersion(user, GROUP_ID, EXISTING_ID, CITATION_VERSION_1);
    }

    @Test(expected = CannotFindCitationVersionException.class)
    public void test_revertCitationVersion_noCitationVersion()
            throws GroupDoesNotExistException, ZoteroConnectionException, CitationIsOutdatedException,
            ZoteroHttpStatusException, CannotFindCitationVersionException, CannotFindCitationException, ZoteroItemCreationFailedException {
        Mockito.when(groupManager.getGroup(user, GROUP_ID)).thenReturn(group);
        Mockito.when(citationManager.getCitation(EXISTING_ID)).thenReturn(citation1);
        Mockito.when(citationVersionsDao.getVersion(GROUP_ID, EXISTING_ID, CITATION_VERSION_1)).thenReturn(null);
        managerToTest.revertCitationVersion(user, GROUP_ID, EXISTING_ID, CITATION_VERSION_1);
    }

}
