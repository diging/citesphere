package edu.asu.diging.citesphere.core.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.social.zotero.exception.ZoteroConnectionException;
import org.springframework.test.util.ReflectionTestUtils;

import edu.asu.diging.citesphere.core.exceptions.CannotFindCitationException;
import edu.asu.diging.citesphere.core.exceptions.CitationIsOutdatedException;
import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroHttpStatusException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroItemCreationFailedException;
import edu.asu.diging.citesphere.core.service.ICitationStore;
import edu.asu.diging.citesphere.core.service.IGroupManager;
import edu.asu.diging.citesphere.core.zotero.IZoteroManager;
import edu.asu.diging.citesphere.data.bib.CitationGroupRepository;
import edu.asu.diging.citesphere.data.bib.ICitationDao;
import edu.asu.diging.citesphere.model.bib.ICitation;
import edu.asu.diging.citesphere.model.bib.ICitationGroup;
import edu.asu.diging.citesphere.model.bib.impl.Citation;
import edu.asu.diging.citesphere.model.bib.impl.CitationGroup;
import edu.asu.diging.citesphere.model.bib.impl.CitationResults;
import edu.asu.diging.citesphere.user.IUser;
import edu.asu.diging.citesphere.user.impl.User;

public class CitationManagerTest {

    @Mock
    private IZoteroManager zoteroManager;
    
    @Mock
    private CitationGroupRepository groupRepository;
    
    @Mock
    private ICitationStore citationStore;
    
    @Mock
    private IGroupManager groupManager;
    
    @Mock
    private ICitationDao citationDao;
    
    @Mock
    private AsyncCitationManager asyncCitationManager;
    
    @InjectMocks
    private CitationManager managerToTest;
    
    private final String EXISTING_ID = "ID";
    private final Citation existingCitation = new Citation();
    Long currentVersion = new Long(1);
    
    private final String ZOTERO_CITATION_ID = "ZOTERO";
    private final ICitation zoteroCitation = new Citation();
    
    private final String GROUP_ID = "12";
    private IUser user;

    private Long GROUP1_ID = new Long(1);
    private Long GROUP2_ID = new Long(2);
    ICitationGroup group;
    private ICitationGroup group1;
    private ICitationGroup group2;
        
    @Before
    public void setUp() throws ZoteroHttpStatusException {
        MockitoAnnotations.initMocks(this);
        managerToTest.init();
        user = new User();
        user.setUsername("USERNAME");
        
        group = new CitationGroup();
        group.setGroupId(new Long(GROUP_ID));
        group.getUsers().add(user.getUsername());
        Mockito.when(groupRepository.findFirstByGroupId(new Long(GROUP_ID))).thenReturn(Optional.of((CitationGroup)group));
        Mockito.when(groupManager.getGroup(user, GROUP_ID)).thenReturn(group);
        
        existingCitation.setKey(EXISTING_ID);
        existingCitation.setVersion(currentVersion);
        existingCitation.setGroup(GROUP_ID);
        Mockito.when(citationStore.findById(EXISTING_ID)).thenReturn(Optional.of(existingCitation));
    
        zoteroCitation.setKey(ZOTERO_CITATION_ID);
        Mockito.when(zoteroManager.getGroupItem(user, GROUP_ID, ZOTERO_CITATION_ID)).thenReturn(zoteroCitation);
        Mockito.when(citationStore.findById(ZOTERO_CITATION_ID)).thenReturn(Optional.empty());
          
        group1 = new CitationGroup();
        group1.setGroupId(GROUP1_ID);
        group1.setContentVersion(20L);
        Mockito.when(groupRepository.findFirstByGroupId(GROUP1_ID)).thenReturn(Optional.of((CitationGroup)group1));
        
        group2 = new CitationGroup();
        group2.setGroupId(GROUP2_ID);
        group2.setContentVersion(3L);
        Mockito.when(groupRepository.findFirstByGroupId(GROUP2_ID)).thenReturn(Optional.of((CitationGroup)group2));
    }
    
    @Test
    public void test_getCitation_inDb() throws GroupDoesNotExistException, CannotFindCitationException, ZoteroHttpStatusException {
        ICitation actual = managerToTest.getCitation(user, GROUP_ID, EXISTING_ID);
        Assert.assertNotNull(actual);
        Assert.assertEquals(EXISTING_ID, actual.getKey());
    }
    
    
    @Test
    public void test_getCitation_inZotero() throws GroupDoesNotExistException, CannotFindCitationException, ZoteroHttpStatusException {
        ICitation actual = managerToTest.getCitation(user, GROUP_ID, ZOTERO_CITATION_ID);
        Assert.assertNotNull(actual);
        Assert.assertEquals(ZOTERO_CITATION_ID, actual.getKey());
    }
    
    @Test
    public void test_getCitation_doesNotExist() {
        // FIXME: implement when bug is removed
    }
    
    @Test
    public void test_getCitationFromZotero_exists() throws ZoteroHttpStatusException {
        ICitation actual = managerToTest.getCitationFromZotero(user, GROUP_ID, ZOTERO_CITATION_ID);
        Assert.assertNotNull(actual);
        Assert.assertEquals(ZOTERO_CITATION_ID, actual.getKey());
    }
    
    @Test
    public void test_getCitationFromZotero_doesNotExist() {
        // FIXME: implement when bug is removed
    } 
    
    @Test 
    public void test_updateCitation_success() throws ZoteroConnectionException, CitationIsOutdatedException,
            ZoteroHttpStatusException, ZoteroItemCreationFailedException {
        Mockito.when(zoteroManager.getGroupItemVersion(user, GROUP_ID, EXISTING_ID)).thenReturn(currentVersion);
        ICitation updatedCitation = new Citation();
        updatedCitation.setKey(EXISTING_ID);
        updatedCitation.setVersion(new Long(2));
        Mockito.when(zoteroManager.updateCitation(user, GROUP_ID, existingCitation)).thenReturn(updatedCitation);
        
        managerToTest.updateCitation(user, GROUP_ID, existingCitation);
        Mockito.verify(citationStore).save(updatedCitation);
    }
    
    @Test(expected=CitationIsOutdatedException.class)
    public void test_updateCitation_conflict() throws ZoteroConnectionException, CitationIsOutdatedException,
            ZoteroHttpStatusException, ZoteroItemCreationFailedException {
        Mockito.when(zoteroManager.getGroupItemVersion(user, GROUP_ID, EXISTING_ID)).thenReturn(new Long(2));
        managerToTest.updateCitation(user, GROUP_ID, existingCitation);
    }
    
    @Test
    public void test_updateCitationFromZotero_success() throws GroupDoesNotExistException, CannotFindCitationException, ZoteroHttpStatusException {
        ICitation updatedCitation = new Citation();
        updatedCitation.setKey(EXISTING_ID);
        updatedCitation.setVersion(new Long(2));
        Mockito.when(zoteroManager.getGroupItem(user, GROUP_ID, EXISTING_ID)).thenReturn(updatedCitation);
        ICitation actual = managerToTest.updateCitationFromZotero(user, GROUP_ID, EXISTING_ID);
        Mockito.verify(citationStore).save(updatedCitation);
        Assert.assertEquals(updatedCitation, actual);
    }
    
    @Test 
    public void test_getGroups_allUpToDateInDb() {
        Map<Long, Long> groupVersions = new HashMap<>();
        groupVersions.put(GROUP1_ID, new Long(20));
        groupVersions.put(GROUP2_ID, new Long(3));
        Mockito.when(zoteroManager.getGroupsVersion(user)).thenReturn(groupVersions);
        Mockito.when(groupRepository.save((CitationGroup)group1)).thenReturn((CitationGroup)group1);
        Mockito.when(groupRepository.save((CitationGroup)group2)).thenReturn((CitationGroup)group2);
        
        List<ICitationGroup> expected = new ArrayList<>();
        group1.setMetadataVersion(20L);
        group2.setMetadataVersion(3L);
        expected.add(group1);
        expected.add(group2);
        
        List<ICitationGroup> actual = managerToTest.getGroups(user);
        Assert.assertEquals(expected, actual);
    }
    
    @Test 
    public void test_getGroups_notUpToDateInDb() {
        Map<Long, Long> groupVersions = new HashMap<>();
        groupVersions.put(GROUP1_ID, new Long(20));
        groupVersions.put(GROUP2_ID, new Long(4));
        Mockito.when(zoteroManager.getGroupsVersion(user)).thenReturn(groupVersions);
        
        group1.setMetadataVersion(20L);
        
        CitationGroup updatedGroup = new CitationGroup();
        updatedGroup.setGroupId(GROUP2_ID);
        updatedGroup.setContentVersion(new Long(4));
        Mockito.when(zoteroManager.getGroup(user, GROUP2_ID.toString(), true)).thenReturn(updatedGroup);
        Mockito.when(groupRepository.save(updatedGroup)).thenReturn(updatedGroup);
        Mockito.when(groupRepository.save((CitationGroup)group1)).thenReturn((CitationGroup)group1);
        
        List<ICitationGroup> expected = new ArrayList<>();
        expected.add(group1);
        expected.add(updatedGroup);
        
        List<ICitationGroup> actual = managerToTest.getGroups(user);
        Assert.assertEquals(expected, actual);
        Mockito.verify(groupRepository).save((CitationGroup)updatedGroup);
    }
    
    @Test
    public void test_getGroups_notInDb() {
        Long group3Id = new Long(3);
        ICitationGroup group3 = new CitationGroup();
        group3.setGroupId(group3Id);
        group3.setContentVersion(new Long(20));
        
        Map<Long, Long> groupVersions = new HashMap<>();
        groupVersions.put(group3Id, new Long(20));
        Mockito.when(zoteroManager.getGroupsVersion(user)).thenReturn(groupVersions);
        Mockito.when(groupRepository.findFirstByGroupId(group3Id)).thenReturn(Optional.empty());
        Mockito.when(zoteroManager.getGroup(user, group3Id.toString(), false)).thenReturn(group3);
        
        List<ICitationGroup> expected = new ArrayList<>();
        expected.add(group3);
        
        List<ICitationGroup> actual = managerToTest.getGroups(user);
        Assert.assertEquals(expected, actual);
        Mockito.verify(groupRepository).save((CitationGroup)group3);
    }
    
    @Test
    public void test_createCitation_success() throws ZoteroConnectionException, ZoteroItemCreationFailedException, GroupDoesNotExistException, ZoteroHttpStatusException {
        Citation createdCitation = new Citation();
        createdCitation.setKey("KEY");
        
        Citation newCitation = new Citation();
        Mockito.when(zoteroManager.createCitation(user, GROUP_ID, new ArrayList<>(), newCitation)).thenReturn(createdCitation);
        Mockito.when(citationStore.save(createdCitation)).thenReturn(createdCitation);
        
        ICitation actual = managerToTest.createCitation(user, GROUP_ID, new ArrayList<>(), newCitation);
        Mockito.verify(citationStore).save(createdCitation);
        Assert.assertEquals(createdCitation, actual);
    }
    
    @Test(expected=ZoteroItemCreationFailedException.class)
    public void test_createCitation_failure() throws ZoteroConnectionException, ZoteroItemCreationFailedException, GroupDoesNotExistException, ZoteroHttpStatusException {
        ICitation newCitation = new Citation();
        Mockito.when(zoteroManager.createCitation(user, GROUP_ID, new ArrayList<>(), newCitation)).thenThrow(new ZoteroItemCreationFailedException());
        
        managerToTest.createCitation(user, GROUP_ID, new ArrayList<>(), newCitation);
    }
    
    @Test
    public void test_getPrevAndNextCitation_prevAndNextOnCurrentPage() throws GroupDoesNotExistException, ZoteroHttpStatusException {
        String sortBy = "title";
        int page = 1;
        int index = 1;
        group.setNumItems(10);
        
        ReflectionTestUtils.setField(managerToTest, "zoteroPageSize", 50);
        
        List<ICitation> citations = new ArrayList<ICitation>();
        for(int i=0;i<10;i++) {
            Citation citation = new  Citation();
            citation.setKey("key"+i);
            citations.add(citation);
        }
        
        Mockito.doReturn(citations).when(citationDao).findCitations(GROUP_ID, (page-1)*50, 50, false, null, false);
        
        CitationPage actualResult= managerToTest.getPrevAndNextCitation(user, GROUP_ID, "", page, sortBy, index, null);
        Assert.assertEquals("key2", actualResult.getNext());
        Assert.assertEquals("key0", actualResult.getPrev());
    }
    
    @Test
    public void test_getPrevAndNextCitation_prevOnCurrentPageAndNextOnNextPage() throws GroupDoesNotExistException, ZoteroHttpStatusException {
        String sortBy = "title";
        int page = 1;
        int index = 8;
        group.setNumItems(10);
        
        ReflectionTestUtils.setField(managerToTest, "zoteroPageSize", 9);
        
        List<ICitation> citations = new ArrayList<ICitation>();
        for(int i=0;i<9;i++) {
            Citation citationOnPage1 = new  Citation();
            citationOnPage1.setKey("key"+i);
            citations.add(citationOnPage1);
        }
        
        
        List<ICitation> citationsPage2 = new ArrayList<ICitation>();
        Citation citationOnPage2;
        citationOnPage2 = new  Citation();
        citationOnPage2.setKey("key"+9);
        citationsPage2.add(citationOnPage2);
        
        Mockito.doReturn(citations).when(citationDao).findCitations(GROUP_ID, (page-1)*9, 9, false, null, false);
        Mockito.doReturn(citationsPage2).when(citationDao).findCitations(GROUP_ID, (page)*9, 9, false, null, false);
        
        CitationPage actualResult= managerToTest.getPrevAndNextCitation(user, GROUP_ID, "", page, sortBy, index, null);
        Assert.assertEquals("key9", actualResult.getNext());
        Assert.assertEquals("key7", actualResult.getPrev());
    }
    
    @Test
    public void test_getPrevAndNextCitation_prevOnPrevPageAndNextOnCurrentPage() throws GroupDoesNotExistException, ZoteroHttpStatusException {
        String sortBy = "title";
        int page = 2;
        int index = 0;
        group.setNumItems(10);
        
        ReflectionTestUtils.setField(managerToTest, "zoteroPageSize", 9);
       
        List<ICitation> citations = new ArrayList<ICitation>();
        for(int i=0;i<9;i++) {
            Citation citationOnPage1 = new  Citation();
            citationOnPage1.setKey("key"+i);
            citations.add(citationOnPage1);
        }
        
        List<ICitation> citationsPage2 = new ArrayList<ICitation>();
        Citation citationOnPage2;
        citationOnPage2 = new  Citation();
        citationOnPage2.setKey("key"+9);
        citationsPage2.add(citationOnPage2);
        
        Mockito.doReturn(citations).when(citationDao).findCitations(GROUP_ID, (page-2)*9, 9, false, null, false);
        Mockito.doReturn(citationsPage2).when(citationDao).findCitations(GROUP_ID, (page-1)*9, 9, false, null, false);
        CitationPage actualResult= managerToTest.getPrevAndNextCitation(user, GROUP_ID, "", page, sortBy, index, null);
        Assert.assertNull(actualResult.getNext());
        Assert.assertEquals("key8", actualResult.getPrev());
    }
    
    @Test
    public void test_getPrevAndNextCitation_noPrevAndNext() throws GroupDoesNotExistException, ZoteroHttpStatusException {
        String sortBy = "title";
        int page = 1;
        int index = 0;
        ReflectionTestUtils.setField(managerToTest, "zoteroPageSize", 50);
        CitationResults citationResults = new CitationResults();
        List<ICitation> citations = new ArrayList<ICitation>();
        Citation citation;
        citation = new  Citation();
        citation.setKey("key"+0);
        citations.add(citation);
        citationResults.setCitations(citations);
        citationResults.setTotalResults(10);
        citationResults.setNotModified(true);
        Mockito.when(zoteroManager.getGroupItems(user, GROUP_ID, page, sortBy, new Long(0))).thenReturn(citationResults);
        Mockito.when(citationDao.findCitations(GROUP_ID, page-1, 0, false, null, false)).thenReturn(new ArrayList<>());
        CitationPage actualResult= managerToTest.getPrevAndNextCitation(user, GROUP_ID, "", page, sortBy, index, null);
        Assert.assertNull(actualResult.getNext());
        Assert.assertNull(actualResult.getPrev());
    }
}
