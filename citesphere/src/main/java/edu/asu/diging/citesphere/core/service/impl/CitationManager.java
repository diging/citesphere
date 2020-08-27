package edu.asu.diging.citesphere.core.service.impl;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.social.zotero.exception.ZoteroConnectionException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import edu.asu.diging.citesphere.core.exceptions.AccessForbiddenException;
import edu.asu.diging.citesphere.core.exceptions.CannotFindCitationException;
import edu.asu.diging.citesphere.core.exceptions.CitationIsOutdatedException;
import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroHttpStatusException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroItemCreationFailedException;
import edu.asu.diging.citesphere.core.model.cache.IPageRequest;
import edu.asu.diging.citesphere.core.model.cache.impl.PageRequest;
import edu.asu.diging.citesphere.core.repository.cache.PageRequestRepository;
import edu.asu.diging.citesphere.core.service.IAsyncCitationProcessor;
import edu.asu.diging.citesphere.core.service.ICitationManager;
import edu.asu.diging.citesphere.core.service.IGroupManager;
import edu.asu.diging.citesphere.core.zotero.IZoteroManager;
import edu.asu.diging.citesphere.data.bib.CitationGroupRepository;
import edu.asu.diging.citesphere.data.bib.CitationRepository;
import edu.asu.diging.citesphere.data.bib.CustomCitationRepository;
import edu.asu.diging.citesphere.data.bib.ICitationDao;
import edu.asu.diging.citesphere.model.bib.ICitation;
import edu.asu.diging.citesphere.model.bib.ICitationGroup;
import edu.asu.diging.citesphere.model.bib.ItemType;
import edu.asu.diging.citesphere.model.bib.ZoteroObjectType;
import edu.asu.diging.citesphere.model.bib.impl.BibField;
import edu.asu.diging.citesphere.model.bib.impl.Citation;
import edu.asu.diging.citesphere.model.bib.impl.CitationGroup;
import edu.asu.diging.citesphere.model.bib.impl.CitationResults;
import edu.asu.diging.citesphere.user.IUser;

@Service
@PropertySource("classpath:/config.properties")
@Transactional
public class CitationManager implements ICitationManager {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Value("${_zotero_page_size}")
    private Integer zoteroPageSize;
    
    @Value("${_sync_frequency}")
    private Integer syncFrequencey;
    
    @Autowired
    private IZoteroManager zoteroManager;
    
    // FIXME: needs to be replaced with group manager
    @Autowired
    private CitationGroupRepository groupRepository;
    
    @Autowired
    private CitationRepository citationRepository;
    
    @Autowired
    private ICitationDao citationDao;
    
    @Autowired
    private CustomCitationRepository customCitationRepository;
     
    @Autowired
    private IGroupManager groupManager;
    
    @Autowired
    private IAsyncCitationProcessor asyncCitationProcessor;
    
    private Map<String, BiFunction<ICitation, ICitation, Integer>> sortFunctions;
    
    @PostConstruct
    public void init() {
        sortFunctions = new HashMap<>();
        sortFunctions.put("title", ((o1, o2) -> {
            String o1Title = o1 != null && o1.getTitle() != null ? o1.getTitle() : "";
            String o2Title = o2 != null && o2.getTitle() != null ? o2.getTitle() : "";
            return o1Title.toLowerCase().compareTo(o2Title.toLowerCase());
        }));
    }
    
    @Override
    public ICitation getCitation(IUser user, String groupId, String key) throws GroupDoesNotExistException, CannotFindCitationException, ZoteroHttpStatusException {
        Optional<ICitation> optional = citationRepository.findByKey(key);
        if (optional.isPresent()) {
            ICitation citation = optional.get();
            ICitationGroup group = groupManager.getGroup(user, groupId);
            if (group != null && group.getGroupId() == new Long(groupId)) {
                if (!group.getUsers().contains(user.getUsername())) {
                    if (!group.getUsers().contains(user.getUsername())) {
                        throw new AccessForbiddenException("User does not have access this citation.");
                    }
                }
                
                return citation;
            }
            return null;
        }
        return updateCitationFromZotero(user, groupId, key);
    }
    
    /**
     * Retrieve a citation from Zotero bypassing the database cache. This method also
     * does not store the retrieved citation in the database cache. 
     * Use {@link getCitation(IUser, String, String) getCitation} method instead if you 
     * want to make use of the database cache.
     * @param user User who is accessing Zotero.
     * @param groupId Group id of the group a citation is in.
     * @param key The key of the citation to be retrieved.
     * @return The retrieved citation.
     * @throws ZoteroHttpStatusException 
     */
    @Override
    public ICitation getCitationFromZotero(IUser user, String groupId, String key) throws ZoteroHttpStatusException {
        return zoteroManager.getGroupItem(user, groupId, key);
    }
    
    @Override
    public void updateCitation(IUser user, String groupId, ICitation citation) throws ZoteroConnectionException, CitationIsOutdatedException, ZoteroHttpStatusException {
        long citationVersion = zoteroManager.getGroupItemVersion(user, groupId, citation.getKey());
        Optional<ICitation> storedCitationOptional = citationRepository.findByKey(citation.getKey());
        if (storedCitationOptional.isPresent()) {
            ICitation storedCitation = storedCitationOptional.get();
            if (storedCitation.getVersion() != citationVersion) {
                throw new CitationIsOutdatedException();
            }
        }
        citation = customCitationRepository.mergeCitation(citation);
        ICitation updatedCitation = zoteroManager.updateCitation(user, groupId, citation);
        
        Optional<ICitationGroup> groupOptional = groupRepository.findByGroupId(new Long(groupId));
        updatedCitation.setGroup(groupOptional.get().getGroupId() + "");
        
        citationRepository.save((Citation)updatedCitation);
    }
    
    @Override
    public ICitation createCitation(IUser user, String groupId, List<String> collectionIds, ICitation citation) throws ZoteroConnectionException, ZoteroItemCreationFailedException, GroupDoesNotExistException, ZoteroHttpStatusException {
        Optional<ICitationGroup> groupOptional = groupRepository.findByGroupId(new Long(groupId));
        if (!groupOptional.isPresent()) {
            throw new GroupDoesNotExistException("Group with id " + groupId + " does not exist.");
        }
        
        ICitation newCitation = zoteroManager.createCitation(user, groupId, collectionIds, citation);
        citationRepository.save((Citation) newCitation);
        
        // mark group outdated, so it'll be updated on the next loading
        ICitationGroup group = groupOptional.get();
        group.setLastLocallyModifiedOn(OffsetDateTime.now().toString());
        groupRepository.save((CitationGroup)group);
        
        return newCitation;
    }
    
    @Override
    public void detachCitation(ICitation citation) {
        customCitationRepository.detachCitation(citation);
    }
    
    @Override
    public ICitation updateCitationFromZotero(IUser user, String groupId, String itemKey) throws GroupDoesNotExistException, CannotFindCitationException, ZoteroHttpStatusException {
        Optional<ICitationGroup> groupOptional = groupRepository.findByGroupId(new Long(groupId));
        if (!groupOptional.isPresent()) {
            throw new GroupDoesNotExistException("Group with id " + groupId + " does not exist.");
        }
        try {
            ICitation citation = zoteroManager.getGroupItem(user, groupId, itemKey); 
            citation.setGroup(groupOptional.get().getGroupId() + "");
            citationRepository.save((Citation)citation);
            return citation;
        } catch (HttpClientErrorException ex) {
            throw new CannotFindCitationException(ex);
        }
        
    }

    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.service.impl.ICitationManager#getGroups(edu.asu.diging.citesphere.core.model.IUser)
     */
    @Override
    public List<ICitationGroup> getGroups(IUser user) {
        Map<Long, Long> groupVersions = zoteroManager.getGroupsVersion(user);
        List<ICitationGroup> groups = new ArrayList<>();
        for (Long id : groupVersions.keySet()) {
            Optional<ICitationGroup> groupOptional = groupRepository.findByGroupId(id);
            if (groupOptional.isPresent()) {
                ICitationGroup group = groupOptional.get();
                if (group.getMetadataVersion() != groupVersions.get(id)) {
                    groupRepository.delete((CitationGroup)group);
                    group = zoteroManager.getGroup(user, id + "", true);
                    group.setUpdatedOn(OffsetDateTime.now().toString());
                }
                addUserToGroup(group, user);
                groupRepository.save((CitationGroup)group);
                groups.add(group);
            } else {
                ICitationGroup citGroup = zoteroManager.getGroup(user, id + "", false);
                citGroup.setUpdatedOn(OffsetDateTime.now().toString());
                addUserToGroup(citGroup, user);
                groups.add(citGroup);
                groupRepository.save((CitationGroup)citGroup);
            }
        }
        return groups;
    }
    
    private void addUserToGroup(ICitationGroup group, IUser user) {
        if (!group.getUsers().contains(user.getUsername())) {
            group.getUsers().add(user.getUsername());
        }
    }
    
    @Override
    public CitationResults getGroupItems(IUser user, String groupId, String collectionId, int page, String sortBy) throws GroupDoesNotExistException, ZoteroHttpStatusException {
        Optional<ICitationGroup> groupOptional = groupRepository.findByGroupId(new Long(groupId));
        if (groupOptional.isPresent()) {
            ICitationGroup group = groupOptional.get();
            boolean isModified = zoteroManager.isGroupModified(user, groupId, group.getContentVersion());
            if (isModified) {
                asyncCitationProcessor.loadCitations(user, group, collectionId, sortBy);
            }
           
          CitationResults results = new CitationResults();
          List<ICitation> citations = (List<ICitation>) citationDao.findCitations(groupId, (page-1)*zoteroPageSize, zoteroPageSize);
          results.setCitations(citations);
          return results;
        }
        throw new GroupDoesNotExistException("There is no group with id " + groupId);
    }
    
    @Override
    public void forceGroupItemsRefresh(IUser user, String groupId, String collectionId, int page, String sortBy) {
        Optional<ICitationGroup> groupOptional = groupRepository.findByGroupId(new Long(groupId));
        if (groupOptional.isPresent()) {
            ICitationGroup group = groupOptional.get();
            zoteroManager.forceRefresh(user, groupId, collectionId, page, sortBy, group.getContentVersion());
            group.setLastLocallyModifiedOn(OffsetDateTime.now().toString());
            groupRepository.save((CitationGroup)group);
        }
    }

    private PageRequest createPageRequest(IUser user, int page, String sortBy, ICitationGroup group,
            CitationResults results) {
        List<Citation> citations = new ArrayList<>();
        results.getCitations().forEach(c -> {
            c.setGroup(group.getGroupId()+"");
            citations.add((Citation)c);
        });
        citationRepository.saveAll(citations);
        
        PageRequest request = new PageRequest();
//        request.setCitations(new HashSet<>(results.getCitations()));
        request.setObjectId(group.getId() + "");
        request.setPageNumber(page);
        request.setPageSize(zoteroPageSize);
        request.setTotalNumResults(results.getTotalResults());
        request.setUser(user);
        request.setVersion(group.getMetadataVersion());
        request.setZoteroObjectType(ZoteroObjectType.GROUP);
        request.setSortBy(sortBy);
        
        request.setLastUpdated(OffsetDateTime.now());
        return request;
    }
    
    @Override
    public List<BibField> getItemTypeFields(IUser user, ItemType itemType) {
        return zoteroManager.getFields(user, itemType);
    }
    
    @Override
    public List<String> getValidCreatorTypes(IUser user, ItemType itemType) {
        return zoteroManager.getValidCreatorTypes(user, itemType);
    }
    
    @Override
    public CitationPage getPrevAndNextCitation(IUser user, String groupId, String collectionId, int page, String sortBy, int index) throws GroupDoesNotExistException, ZoteroHttpStatusException {
        CitationResults citationResults = getGroupItems(user, groupId, collectionId, page, sortBy);
        List<ICitation> citations = citationResults.getCitations();
        CitationPage result = new CitationPage();
        result.setIndex(String.valueOf(index));
        result.setZoteroGroupId(groupId);
        result.setCollectionId(collectionId);
        result.setSortBy(sortBy);
        if(citations != null && citations.size()>0) {
            int maxPage = (int) Math.ceil((citationResults.getTotalResults() / Float.valueOf(zoteroPageSize)));
            if(index == citations.size() - 1 && page < maxPage) {
                CitationResults nextPageCitationResults = getGroupItems(user, groupId, collectionId, page+1, sortBy);
                if(nextPageCitationResults != null && nextPageCitationResults.getCitations().size()>0) {
                    result.setNext(nextPageCitationResults.getCitations().get(0).getKey());
                    result.setNextIndex(String.valueOf(0));
                    result.setNextPage(String.valueOf(page+1));
                }
            }else if(index < citations.size() - 1) {
                result.setNext(citations.get(index+1).getKey());
                result.setNextIndex(String.valueOf(index+1));
                result.setNextPage(String.valueOf(page));
            }
            if(index > 0) {
                result.setPrev(citations.get(index-1).getKey());
                result.setPrevIndex(String.valueOf(index-1));
                result.setPrevPage(String.valueOf(page));
            } else if(index == 0 && page>1) {
                CitationResults prevPageCitationResults = getGroupItems(user, groupId, collectionId, page-1, sortBy);
                int pageSize = prevPageCitationResults.getCitations().size();
                result.setPrev(prevPageCitationResults.getCitations().get(pageSize-1).getKey());
                result.setPrevIndex(String.valueOf(pageSize-1));
                result.setPrevPage(String.valueOf(page-1));
            }
        }
        return result; 
    }
}
