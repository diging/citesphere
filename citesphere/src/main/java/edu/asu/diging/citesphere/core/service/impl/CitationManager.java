package edu.asu.diging.citesphere.core.service.impl;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.social.zotero.exception.ZoteroConnectionException;
import org.springframework.stereotype.Service;

import edu.asu.diging.citesphere.core.exceptions.CitationIsOutdatedException;
import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroItemCreationFailedException;
import edu.asu.diging.citesphere.core.model.IUser;
import edu.asu.diging.citesphere.core.model.bib.ICitation;
import edu.asu.diging.citesphere.core.model.bib.ICitationGroup;
import edu.asu.diging.citesphere.core.model.bib.ItemType;
import edu.asu.diging.citesphere.core.model.bib.ZoteroObjectType;
import edu.asu.diging.citesphere.core.model.bib.impl.BibField;
import edu.asu.diging.citesphere.core.model.bib.impl.Citation;
import edu.asu.diging.citesphere.core.model.bib.impl.CitationGroup;
import edu.asu.diging.citesphere.core.model.bib.impl.CitationResults;
import edu.asu.diging.citesphere.core.model.cache.IPageRequest;
import edu.asu.diging.citesphere.core.model.cache.impl.PageRequest;
import edu.asu.diging.citesphere.core.repository.bib.CitationGroupRepository;
import edu.asu.diging.citesphere.core.repository.bib.CitationRepository;
import edu.asu.diging.citesphere.core.repository.bib.CustomCitationRepository;
import edu.asu.diging.citesphere.core.repository.cache.PageRequestRepository;
import edu.asu.diging.citesphere.core.service.ICitationManager;
import edu.asu.diging.citesphere.core.zotero.IZoteroManager;

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

    @Autowired
    private CitationGroupRepository groupRepository;

    @Autowired
    private CitationRepository citationRepository;

    @Autowired
    private CustomCitationRepository customCitationRepository;

    @Autowired
    private PageRequestRepository pageRequestRepository;

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
    public ICitation getCitation(IUser user, String groupId, String key) throws GroupDoesNotExistException {
	Optional<Citation> optional = citationRepository.findById(key);
	if (optional.isPresent()) {
	    return optional.get();
	}
	return updateCitationFromZotero(user, groupId, key);
    }

    /**
     * Retrieve a citation from Zotero bypassing the database cache. This method
     * also does not store the retrieved citation in the database cache. Use
     * {@link getCitation(IUser, String, String) getCitation} method instead if you
     * want to make use of the database cache.
     * 
     * @param user
     *            User who is accessing Zotero.
     * @param groupId
     *            Group id of the group a citation is in.
     * @param key
     *            The key of the citation to be retrieved.
     * @return The retrieved citation.
     */
    @Override
    public ICitation getCitationFromZotero(IUser user, String groupId, String key) {
	return zoteroManager.getGroupItem(user, groupId, key);
    }

    @Override
    public void updateCitation(IUser user, String groupId, ICitation citation)
	    throws ZoteroConnectionException, CitationIsOutdatedException {
	long citationVersion = zoteroManager.getGroupItemVersion(user, groupId, citation.getKey());
	Optional<Citation> storedCitationOptional = citationRepository.findById(citation.getKey());
	if (storedCitationOptional.isPresent()) {
	    ICitation storedCitation = storedCitationOptional.get();
	    if (storedCitation.getVersion() != citationVersion) {
		throw new CitationIsOutdatedException();
	    }
	}
	citation = customCitationRepository.mergeCitation(citation);
	ICitation updatedCitation = zoteroManager.updateCitation(user, groupId, citation);
	// save updated info
	citationRepository.delete((Citation) citation);
	citationRepository.save((Citation) updatedCitation);
    }

    @Override
    public ICitation createCitation(IUser user, String groupId, ICitation citation)
	    throws ZoteroConnectionException, ZoteroItemCreationFailedException, GroupDoesNotExistException {
	Optional<CitationGroup> groupOptional = groupRepository.findById(new Long(groupId));
	if (!groupOptional.isPresent()) {
	    throw new GroupDoesNotExistException("Group with id " + groupId + " does not exist.");
	}

	ICitation newCitation = zoteroManager.createCitation(user, groupId, citation);
	citationRepository.save((Citation) newCitation);

	// mark group outdated, so it'll be updated on the next loading
	CitationGroup group = groupOptional.get();
	group.setLastLocallyModifiedOn(OffsetDateTime.now());
	groupRepository.save(group);

	return newCitation;
    }

    @Override
    public void detachCitation(ICitation citation) {
	customCitationRepository.detachCitation(citation);
    }

    @Override
    public ICitation updateCitationFromZotero(IUser user, String groupId, String itemKey)
	    throws GroupDoesNotExistException {
	Optional<CitationGroup> groupOptional = groupRepository.findById(new Long(groupId));
	if (!groupOptional.isPresent()) {
	    throw new GroupDoesNotExistException("Group with id " + groupId + " does not exist.");
	}
	ICitation citation = zoteroManager.getGroupItem(user, groupId, itemKey);
	citation.setGroup(groupOptional.get());
	citationRepository.save((Citation) citation);
	return citation;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.diging.citesphere.core.service.impl.ICitationManager#getGroups(edu.
     * asu.diging.citesphere.core.model.IUser)
     */
    @Override
    public List<ICitationGroup> getGroups(IUser user) {
	Map<Long, Long> groupVersions = zoteroManager.getGroupsVersion(user);
	List<ICitationGroup> groups = new ArrayList<>();
	for (Long id : groupVersions.keySet()) {
	    Optional<CitationGroup> groupOptional = groupRepository.findById(id);
	    if (groupOptional.isPresent()) {
		ICitationGroup group = groupOptional.get();
		if (group.getVersion() != groupVersions.get(id)) {
		    group = zoteroManager.getGroup(user, id + "", true);
		    group.setUpdatedOn(OffsetDateTime.now());
		    groupRepository.save((CitationGroup) group);
		}
		groups.add(group);
	    } else {
		ICitationGroup citGroup = zoteroManager.getGroup(user, id + "", false);
		citGroup.setUpdatedOn(OffsetDateTime.now());
		groups.add(citGroup);
		groupRepository.save((CitationGroup) citGroup);
	    }
	}
	return groups;
    }

    @Override
    public CitationResults getGroupItems(IUser user, String groupId, String collectionId, int page, String sortBy,
	    String conceptTag) throws GroupDoesNotExistException {
	Optional<CitationGroup> groupOptional = groupRepository.findById(new Long(groupId));
	if (groupOptional.isPresent()) {
	    ICitationGroup group = groupOptional.get();

	    List<PageRequest> requests = null;
	    try {
		if (conceptTag == null) {
		    requests = pageRequestRepository
			    .findByUserAndObjectIdAndPageNumberAndZoteroObjectTypeAndSortByAndCollectionId(user,
				    groupId, page, ZoteroObjectType.GROUP, sortBy, collectionId);
		} else {

		    if (collectionId == null) {
			requests = pageRequestRepository.findPageRequestWithObjectIdAndConceptTags(user, groupId, page,
				ZoteroObjectType.GROUP, conceptTag, sortBy);

		    } else {
			requests = pageRequestRepository.findPageRequestWithObjectIdAndCollectionIdAndConceptTags(user,
				groupId, page, ZoteroObjectType.GROUP, conceptTag, sortBy, collectionId);

		    }
		}

	    } catch (JpaObjectRetrievalFailureException ex) {
		logger.warn("Could not retrieve page request.", ex);
	    }

	    IPageRequest localPageRequest = null;
	    if (requests != null && requests.size() > 0) {
		// there should be just one
		localPageRequest = requests.get(0);

		OffsetDateTime updatedOn = localPageRequest.getLastUpdated() != null ? localPageRequest.getLastUpdated()
			: OffsetDateTime.MIN;
		OffsetDateTime groupLastLocallyModified = group.getLastLocallyModifiedOn() != null
			? group.getLastLocallyModifiedOn()
			: OffsetDateTime.MIN;
		if (updatedOn.plusMinutes(syncFrequencey).isAfter(OffsetDateTime.now())
			&& updatedOn.isAfter(groupLastLocallyModified)) {
		    CitationResults results = new CitationResults();
		    results.setCitations(
			    localPageRequest.getCitations().stream().distinct().collect(Collectors.toList()));
		    results.getCitations().sort(new Comparator<ICitation>() {
			@Override
			public int compare(ICitation o1, ICitation o2) {
			    return sortFunctions.get(sortBy).apply(o1, o2);
			}
		    });
		    results.setTotalResults(group.getNumItems());
		    return results;
		}
	    }

	    CitationResults results = new CitationResults();
	    results.setCitations(new ArrayList<>());
	    results.setTotalResults(0);
	    results.setNotModified(true);
//	    if (collectionId == null || collectionId.trim().isEmpty()) {
//		results = zoteroManager.getGroupItems(user, groupId, page, sortBy, group.getVersion());
//	    } else {
//		results = zoteroManager.getCollectionItems(user, groupId, collectionId, page, sortBy,
//			group.getVersion());
//	    }
	    if (!results.isNotModified()) {
		if (requests != null && requests.size() > 0) {
		    // delete last cache
		    pageRequestRepository.deleteAll(requests);
		}
		PageRequest request = createPageRequest(user, page, sortBy, group, results);
		request.setCollectionId(collectionId);
		pageRequestRepository.save(request);
	    } else if (localPageRequest != null) {
		localPageRequest.setLastUpdated(OffsetDateTime.now());
		results.setCitations(localPageRequest.getCitations());
	    }

	    return results;
	}
	throw new GroupDoesNotExistException("There is no group with id " + groupId);
    }

    private PageRequest createPageRequest(IUser user, int page, String sortBy, ICitationGroup group,
	    CitationResults results) {
	PageRequest request = new PageRequest();
	request.setCitations(results.getCitations());
	request.setObjectId(group.getId() + "");
	request.setPageNumber(page);
	request.setPageSize(zoteroPageSize);
	request.setUser(user);
	request.setVersion(group.getVersion());
	request.setZoteroObjectType(ZoteroObjectType.GROUP);
	request.setSortBy(sortBy);
	results.getCitations().forEach(c -> {
	    c.setGroup(group);
	    citationRepository.save((Citation) c);
	});
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
}
