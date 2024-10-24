package edu.asu.diging.citesphere.core.service.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.social.zotero.exception.ZoteroConnectionException;
import org.springframework.test.util.ReflectionTestUtils;

import edu.asu.diging.citesphere.core.exceptions.CannotFindCitationException;
import edu.asu.diging.citesphere.core.exceptions.CitationIsOutdatedException;
import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.exceptions.SelfCitationException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroHttpStatusException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroItemCreationFailedException;
import edu.asu.diging.citesphere.core.service.ICitationStore;
import edu.asu.diging.citesphere.core.service.IGroupManager;
import edu.asu.diging.citesphere.core.zotero.IZoteroManager;
import edu.asu.diging.citesphere.data.bib.CitationGroupRepository;
import edu.asu.diging.citesphere.data.bib.ICitationDao;
import edu.asu.diging.citesphere.model.bib.ICitation;
import edu.asu.diging.citesphere.model.bib.ICitationGroup;
import edu.asu.diging.citesphere.model.bib.IReference;
import edu.asu.diging.citesphere.model.bib.impl.Citation;
import edu.asu.diging.citesphere.model.bib.impl.CitationGroup;
import edu.asu.diging.citesphere.model.bib.impl.CitationResults;
import edu.asu.diging.citesphere.model.bib.impl.Reference;
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
    private ICitation citation;
    private final String REFERENCE = "reference";
    private final String CITATION_KEY = "citationKey";
    private ICitation updatedCitation;
    private  Set<IReference> references;
    private IReference reference;

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
        
        citation = new Citation();
        citation.setKey(CITATION_KEY);
        citation.setReferences(new HashSet<>());
        
        updatedCitation = new Citation();
        updatedCitation.setKey(CITATION_KEY);
        references = new HashSet<>();
        reference = new Reference();
        reference.setReferenceString(REFERENCE);
        references.add(reference);
        updatedCitation.setReferences(references);
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
        List<ICitationGroup> citationGroups = new ArrayList<>();
        CitationGroup citationGroup = new CitationGroup();
        citationGroup.setGroupId(new Long(GROUP_ID));
        citationGroup.setNumItems(1);
        citationGroups.add(citationGroup);
        
        Citation newCitation = new Citation();
        Mockito.when(zoteroManager.createCitation(user, GROUP_ID, new ArrayList<>(), newCitation)).thenReturn(createdCitation);
        Mockito.when(citationStore.save(createdCitation)).thenReturn(createdCitation);
        Mockito.when(groupRepository.findByGroupId(new Long(GROUP_ID))).thenReturn(citationGroups);
        Mockito.when(groupRepository.save((CitationGroup)citationGroups.get(0))).thenReturn(citationGroup);
        Mockito.when(zoteroManager.getGroup(user, GROUP_ID, true)).thenReturn(citationGroup);

        ICitation actual = managerToTest.createCitation(user, GROUP_ID, new ArrayList<>(), newCitation);
        
        ArgumentCaptor<CitationGroup> groupCaptor = ArgumentCaptor.forClass(CitationGroup.class);
        Mockito.verify(groupRepository, Mockito.times(1)).save(groupCaptor.capture());
        
        Mockito.verify(citationStore).save(createdCitation);
        Mockito.verify(groupRepository).findByGroupId(Long.valueOf(GROUP_ID));
        Mockito.verify(zoteroManager).createCitation(user, GROUP_ID, new ArrayList<>(), newCitation);

        List<CitationGroup> capturedGroups = groupCaptor.getAllValues();
        Assert.assertEquals(1, capturedGroups.size());
        Assert.assertEquals(citationGroup.getGroupId(), capturedGroups.get(0).getGroupId());
        Assert.assertEquals(citationGroup.getNumItems(), capturedGroups.get(0).getNumItems());

        Assert.assertEquals(createdCitation, actual);
    }
    
    @Test(expected=ZoteroItemCreationFailedException.class)
    public void test_createCitation_failure() throws ZoteroConnectionException, ZoteroItemCreationFailedException, GroupDoesNotExistException, ZoteroHttpStatusException {
        List<ICitationGroup> citationGroups = new ArrayList<>();
        CitationGroup citationGroup = new CitationGroup();
        citationGroup.setGroupId(new Long(GROUP_ID));
        citationGroups.add(citationGroup);
        ICitation newCitation = new Citation();
        Mockito.when(groupRepository.findByGroupId(new Long(GROUP_ID))).thenReturn(citationGroups);
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
        
        ICitationGroup mockGroup = Mockito.mock(ICitationGroup.class);
        Mockito.when(zoteroManager.getGroup(user, GROUP_ID, true)).thenReturn(mockGroup);
        
        Mockito.doReturn(citations).when(citationDao).findCitations(GROUP_ID, (page-1)*50, 50, false, null);
        
        CitationPage actualResult= managerToTest.getPrevAndNextCitation(user, GROUP_ID, "", page, sortBy, index, null);
        Assert.assertEquals("key2", actualResult.getNext());
        Assert.assertEquals("key0", actualResult.getPrev());
    }
    
    @Test
    public void test_getPrevAndNextCitation_prevOnCurrentPageAndNextOnNextPage() throws GroupDoesNotExistException, ZoteroHttpStatusException {
        String sortBy = "title";
        int page = 1;
        int index = 8;
        group.setNumItems(20);
        
        ReflectionTestUtils.setField(managerToTest, "zoteroPageSize", 9);
        
        List<ICitation> citations = new ArrayList<ICitation>();
        for(int i=0;i<10;i++) {
            Citation citationOnPage1 = new  Citation();
            citationOnPage1.setKey("key"+i);
            citations.add(citationOnPage1);
        }
        
        List<ICitation> citationsPage2 = new ArrayList<ICitation>();
        Citation citationOnPage2 = new  Citation();
        citationOnPage2.setKey("key11");
        citationsPage2.add(citationOnPage2);
        
        ICitationGroup mockGroup = Mockito.mock(ICitationGroup.class);
        Mockito.when(zoteroManager.getGroup(user, GROUP_ID, true)).thenReturn(mockGroup);
               
        CitationResults citationResultsPage1 = new CitationResults();
        citationResultsPage1.setCitations(citations);
        citationResultsPage1.setTotalResults(10);

        CitationResults citationResultsPage2 = new CitationResults();
        citationResultsPage2.setCitations(citationsPage2);
        citationResultsPage2.setTotalResults(10);

        Mockito.when(zoteroManager.getGroupItems(user, GROUP_ID, page, sortBy, 0L)).thenReturn(citationResultsPage1);
        Mockito.when(zoteroManager.getGroupItems(user, GROUP_ID, page + 1, sortBy, 0L)).thenReturn(citationResultsPage2);

        Mockito.doReturn(citations).when(citationDao).findCitations(GROUP_ID, (page - 1) * 9, 9, false, null);
        Mockito.doReturn(citationsPage2).when(citationDao).findCitations(GROUP_ID, page * 9, 9, false, null);

        CitationPage actualResult = managerToTest.getPrevAndNextCitation(user, GROUP_ID, "", page, sortBy, index, null);

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
        
        ICitationGroup mockGroup = Mockito.mock(ICitationGroup.class);
        Mockito.when(zoteroManager.getGroup(user, GROUP_ID, true)).thenReturn(mockGroup);
        
        Mockito.doReturn(citations).when(citationDao).findCitations(GROUP_ID, (page-2)*9, 9, false, null);
        Mockito.doReturn(citationsPage2).when(citationDao).findCitations(GROUP_ID, (page-1)*9, 9, false, null);
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
        Citation citation = new  Citation();
        citation.setKey("key" + 0);
        citations.add(citation);
        citationResults.setCitations(citations);
        citationResults.setTotalResults(10);
        citationResults.setNotModified(true);
        
        ICitationGroup mockGroup = Mockito.mock(ICitationGroup.class);
        Mockito.when(zoteroManager.getGroup(user, GROUP_ID, true)).thenReturn(mockGroup);
        
        Mockito.when(zoteroManager.getGroupItems(user, GROUP_ID, page, sortBy, new Long(0))).thenReturn(citationResults);
        Mockito.when(citationDao.findCitations(GROUP_ID, page-1, 0, false, null)).thenReturn(new ArrayList<>());
       
        
        CitationPage actualResult= managerToTest.getPrevAndNextCitation(user, GROUP_ID, "", page, sortBy, index, null);
        
        Assert.assertNull(actualResult.getNext());
        Assert.assertNull(actualResult.getPrev());
    }
    
    @Test
    public void test_getNotes_success() throws GroupDoesNotExistException, CannotFindCitationException, ZoteroHttpStatusException {
        List<ICitation> notes = new ArrayList<>();
        
        Citation note1 = new Citation();
        note1.setKey("note1");
        note1.setGroup(GROUP_ID);
        note1.setNote("note 1");
        notes.add(note1);
        
        Citation note2 = new Citation();
        note2.setKey("note2");
        note2.setGroup(GROUP_ID);
        note2.setNote("note 2");
        notes.add(note2);
        
        Mockito.when(citationStore.getNotes(EXISTING_ID)).thenReturn(notes);
        Mockito.when(groupManager.getGroup(user, GROUP_ID)).thenReturn(group);
        
        List<ICitation> actualNotes = managerToTest.getNotes(user, GROUP_ID, EXISTING_ID);
        Assert.assertEquals(notes.size(), actualNotes.size());
        for(ICitation citation : notes) {
            Assert.assertTrue(actualNotes.stream().anyMatch(note->note.getKey().equals(citation.getKey())));
        }
    }
    
    @Test
    public void test_getNotes_empty() throws GroupDoesNotExistException, CannotFindCitationException, ZoteroHttpStatusException {
        List<ICitation> emptyList = new ArrayList<>();
        
        Mockito.when(citationStore.getNotes(EXISTING_ID)).thenReturn(emptyList);
        Mockito.when(groupManager.getGroup(user, GROUP_ID)).thenReturn(group);
        Mockito.when(zoteroManager.getGroupItemNotes(user, GROUP_ID, EXISTING_ID)).thenReturn(new ArrayList<>());
        
        List<ICitation> response = managerToTest.getNotes(user, GROUP_ID, EXISTING_ID);
        Assert.assertTrue(response.size() == 0);
    }

    @Test
    public void test_addCitationToReferences_citationFound() throws SelfCitationException, ZoteroConnectionException, CitationIsOutdatedException, ZoteroHttpStatusException, ZoteroItemCreationFailedException {
        String referenceCitationKey = "referenceCitationKey";
        citation.getReferences().add(reference);
        citation.setVersion(1L);
        
        Mockito.when(zoteroManager.getGroupItemVersion(user, GROUP_ID, CITATION_KEY)).thenReturn(currentVersion);
        ICitation updatedCitation = new Citation();
        updatedCitation.setKey(CITATION_KEY);
        updatedCitation.setVersion(new Long(2));
        Mockito.when(zoteroManager.updateCitation(user, GROUP_ID, citation)).thenReturn(updatedCitation);
        
        when(citationStore.save(citation)).thenReturn(updatedCitation);
        when(citationStore.findById(citation.getKey())).thenReturn(Optional.of(citation));
        doNothing().when(citationStore).delete(any(ICitation.class));
        
        when(citationStore.save(citation)).thenReturn(updatedCitation);
        ICitation result = managerToTest.addCitationToReferences(user, citation, GROUP_ID, referenceCitationKey, REFERENCE);
        Assert.assertEquals(updatedCitation, result);
    }

    @Test
    public void test_addCitationToReferences_nullReferences() throws SelfCitationException, ZoteroConnectionException, CitationIsOutdatedException, ZoteroHttpStatusException, ZoteroItemCreationFailedException {
        String referenceCitationKey = "referenceCitationKey";
        citation.setReferences(null);
        citation.setVersion(1L);
        
        Mockito.when(zoteroManager.getGroupItemVersion(user, GROUP_ID, CITATION_KEY)).thenReturn(currentVersion);
        ICitation updatedCitation = new Citation();
        updatedCitation.setKey(CITATION_KEY);
        updatedCitation.setVersion(new Long(2));
        Mockito.when(zoteroManager.updateCitation(user, GROUP_ID, citation)).thenReturn(updatedCitation);
        
        when(citationStore.save(citation)).thenReturn(updatedCitation);
        when(citationStore.findById(citation.getKey())).thenReturn(Optional.of(citation));
        doNothing().when(citationStore).delete(any(ICitation.class));

        ICitation result = managerToTest.addCitationToReferences(user, citation, GROUP_ID, referenceCitationKey, REFERENCE);
        Assert.assertEquals(updatedCitation, result);
    }

    @Test(expected = SelfCitationException.class)
    public void test_addCitationToReferences_selfCitation() throws SelfCitationException, ZoteroConnectionException, CitationIsOutdatedException, ZoteroHttpStatusException, ZoteroItemCreationFailedException {
        String referenceCitationKey = CITATION_KEY;
        updatedCitation.setReferences(references);
        
        when(citationStore.save(citation)).thenReturn(updatedCitation);
        ICitation result = managerToTest.addCitationToReferences(user, citation, GROUP_ID, referenceCitationKey, REFERENCE);        
    }
}
