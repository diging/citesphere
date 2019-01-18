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

import edu.asu.diging.citesphere.core.exceptions.CitationIsOutdatedException;
import edu.asu.diging.citesphere.core.model.IUser;
import edu.asu.diging.citesphere.core.model.bib.ICitation;
import edu.asu.diging.citesphere.core.model.bib.ICitationGroup;
import edu.asu.diging.citesphere.core.model.bib.impl.Citation;
import edu.asu.diging.citesphere.core.model.bib.impl.CitationGroup;
import edu.asu.diging.citesphere.core.model.impl.User;
import edu.asu.diging.citesphere.core.repository.bib.CitationGroupRepository;
import edu.asu.diging.citesphere.core.repository.bib.CitationRepository;
import edu.asu.diging.citesphere.core.repository.bib.CustomCitationRepository;
import edu.asu.diging.citesphere.core.repository.cache.PageRequestRepository;
import edu.asu.diging.citesphere.core.zotero.IZoteroManager;

public class CitationManagerTest {

    @Mock
    private IZoteroManager zoteroManager;
    
    @Mock
    private CitationGroupRepository groupRepository;
    
    @Mock
    private CitationRepository citationRepository;
    
    @Mock
    private CustomCitationRepository customCitationRepository;
    
    @Mock
    private PageRequestRepository pageRequestRepository;
    
    @InjectMocks
    private CitationManager managerToTest;
    
    private final String EXISTING_ID = "ID";
    private final Citation existingCitation = new Citation();
    Long currentVersion = new Long(1);
    
    private final String ZOTERO_CITATION_ID = "ZOTERO";
    private final Citation zoteroCitation = new Citation();
    
    private final String GROUP_ID = "GROUP_ID";
    private IUser user;

    private Long GROUP1_ID = new Long(1);
    private Long GROUP2_ID = new Long(2);
    private ICitationGroup group1;
    private ICitationGroup group2;
    
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        managerToTest.init();
        
        existingCitation.setKey(EXISTING_ID);
        existingCitation.setVersion(currentVersion);
        Mockito.when(citationRepository.findById(EXISTING_ID)).thenReturn(Optional.of(existingCitation));
    
        user = new User();
        user.setUsername("USERNAME");
        zoteroCitation.setKey(ZOTERO_CITATION_ID);
        Mockito.when(zoteroManager.getGroupItem(user, GROUP_ID, ZOTERO_CITATION_ID)).thenReturn(zoteroCitation);
        Mockito.when(citationRepository.findById(ZOTERO_CITATION_ID)).thenReturn(Optional.empty());
        
        group1 = new CitationGroup();
        group1.setId(GROUP1_ID);
        group1.setVersion(20L);
        Mockito.when(groupRepository.findById(GROUP1_ID)).thenReturn(Optional.of((CitationGroup)group1));
        
        group2 = new CitationGroup();
        group2.setId(GROUP2_ID);
        group2.setVersion(3L);
        Mockito.when(groupRepository.findById(GROUP2_ID)).thenReturn(Optional.of((CitationGroup)group2));
        
    }
    
    @Test
    public void test_getCitation_inDb() {
        ICitation actual = managerToTest.getCitation(user, GROUP_ID, EXISTING_ID);
        Assert.assertNotNull(actual);
        Assert.assertEquals(EXISTING_ID, actual.getKey());
    }
    
    
    @Test
    public void test_getCitation_inZotero() {
        ICitation actual = managerToTest.getCitation(user, GROUP_ID, ZOTERO_CITATION_ID);
        Assert.assertNotNull(actual);
        Assert.assertEquals(ZOTERO_CITATION_ID, actual.getKey());
    }
    
    @Test
    public void test_getCitation_doesNotExist() {
        // FIXME: implement when bug is removed
    }
    
    @Test
    public void test_getCitationFromZotero_exists() {
        ICitation actual = managerToTest.getCitationFromZotero(user, GROUP_ID, ZOTERO_CITATION_ID);
        Assert.assertNotNull(actual);
        Assert.assertEquals(ZOTERO_CITATION_ID, actual.getKey());
    }
    
    @Test
    public void test_getCitationFromZotero_doesNotExist() {
        // FIXME: implement when bug is removed
    }
    
    @Test 
    public void test_updateCitation_success() throws ZoteroConnectionException, CitationIsOutdatedException {
        Mockito.when(zoteroManager.getGroupItemVersion(user, GROUP_ID, EXISTING_ID)).thenReturn(currentVersion);
        Mockito.when(customCitationRepository.mergeCitation(existingCitation)).thenReturn(existingCitation);
        ICitation updatedCitation = new Citation();
        updatedCitation.setKey(EXISTING_ID);
        updatedCitation.setVersion(new Long(2));
        Mockito.when(zoteroManager.updateCitation(user, GROUP_ID, existingCitation)).thenReturn(updatedCitation);
        
        managerToTest.updateCitation(user, GROUP_ID, existingCitation);
        Mockito.verify(citationRepository).delete(existingCitation);
        Mockito.verify(citationRepository).save((Citation)updatedCitation);
    }
    
    @Test(expected=CitationIsOutdatedException.class)
    public void test_updateCitation_conflict() throws ZoteroConnectionException, CitationIsOutdatedException {
        Mockito.when(zoteroManager.getGroupItemVersion(user, GROUP_ID, EXISTING_ID)).thenReturn(new Long(2));
        managerToTest.updateCitation(user, GROUP_ID, existingCitation);
    }
    
    @Test
    public void test_detachCitation_success() {
        managerToTest.detachCitation(existingCitation);
        Mockito.verify(customCitationRepository).detachCitation(existingCitation);
    }
    
    @Test
    public void test_updateCitationFromZotero_success() {
        ICitation updatedCitation = new Citation();
        updatedCitation.setKey(EXISTING_ID);
        updatedCitation.setVersion(new Long(2));
        Mockito.when(zoteroManager.getGroupItem(user, GROUP_ID, EXISTING_ID)).thenReturn(updatedCitation);
        ICitation actual = managerToTest.updateCitationFromZotero(user, GROUP_ID, EXISTING_ID);
        Mockito.verify(citationRepository).save((Citation)updatedCitation);
        Assert.assertEquals(updatedCitation, actual);
    }
    
    @Test 
    public void test_getGroups_allUpToDateInDb() {
        Map<Long, Long> groupVersions = new HashMap<>();
        groupVersions.put(GROUP1_ID, new Long(20));
        groupVersions.put(GROUP2_ID, new Long(3));
        Mockito.when(zoteroManager.getGroupsVersion(user)).thenReturn(groupVersions);
        
        List<ICitationGroup> expected = new ArrayList<>();
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
        
        ICitationGroup updatedGroup = new CitationGroup();
        updatedGroup.setId(GROUP2_ID);
        updatedGroup.setVersion(new Long(4));
        Mockito.when(zoteroManager.getGroup(user, GROUP2_ID.toString(), true)).thenReturn(updatedGroup);
        
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
        group3.setId(group3Id);
        group3.setVersion(new Long(20));
        
        Map<Long, Long> groupVersions = new HashMap<>();
        groupVersions.put(group3Id, new Long(20));
        Mockito.when(zoteroManager.getGroupsVersion(user)).thenReturn(groupVersions);
        Mockito.when(groupRepository.findById(group3Id)).thenReturn(Optional.empty());
        Mockito.when(zoteroManager.getGroup(user, group3Id.toString(), false)).thenReturn(group3);
        
        List<ICitationGroup> expected = new ArrayList<>();
        expected.add(group3);
        
        List<ICitationGroup> actual = managerToTest.getGroups(user);
        Assert.assertEquals(expected, actual);
        Mockito.verify(groupRepository).save((CitationGroup)group3);
    }
}
