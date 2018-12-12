package edu.asu.diging.citesphere.core.service.impl;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.model.IUser;
import edu.asu.diging.citesphere.core.model.bib.ICitationGroup;
import edu.asu.diging.citesphere.core.model.bib.ZoteroObjectType;
import edu.asu.diging.citesphere.core.model.bib.impl.CitationGroup;
import edu.asu.diging.citesphere.core.model.bib.impl.CitationResults;
import edu.asu.diging.citesphere.core.model.cache.IPageRequest;
import edu.asu.diging.citesphere.core.model.cache.impl.PageRequest;
import edu.asu.diging.citesphere.core.repository.bib.CitationGroupRepository;
import edu.asu.diging.citesphere.core.repository.cache.PageRequestRepository;
import edu.asu.diging.citesphere.core.service.ICitationManager;
import edu.asu.diging.citesphere.core.zotero.IZoteroManager;

@Service
@PropertySource("classpath:/config.properties")
@Transactional
public class CitationManager implements ICitationManager {
    
    @Value("${_zotero_page_size}")
    private Integer zoteroPageSize;
    
    @Autowired
    private IZoteroManager zoteroManager;
    
    @Autowired
    private CitationGroupRepository groupRepository;
    
    @Autowired
    private PageRequestRepository pageRequestRepository;

    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.service.impl.ICitationManager#getGroups(edu.asu.diging.citesphere.core.model.IUser)
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
                    groupRepository.save((CitationGroup)group);
                }
                groups.add(group);
            } else {
                ICitationGroup citGroup = zoteroManager.getGroup(user, id + "", false);
                citGroup.setUpdatedOn(OffsetDateTime.now());
                groups.add(citGroup);
                groupRepository.save((CitationGroup)citGroup);
            }
        }
        return groups;
    }
    
    @Override
    public CitationResults getGroupItems(IUser user, String groupId, int page) throws GroupDoesNotExistException {
        Optional<CitationGroup> groupOptional = groupRepository.findById(new Long(groupId));
        if (groupOptional.isPresent()) {
            CitationGroup group = groupOptional.get();
            List<PageRequest> requests = pageRequestRepository.findByUserAndObjectIdAndPageNumberAndZoteroObjectType(user, groupId, page, ZoteroObjectType.GROUP);
//            List<PageRequest> requests = pageRequestRepository.findPageRequestWithCitations(user, groupId, page, ZoteroObjectType.GROUP);
            if(requests.size() > 0) {
                // there should be just one
                IPageRequest request = requests.get(0);
                // if we have the current version locally
                if (request.getVersion() == group.getVersion()) {
                    CitationResults results = new CitationResults();
                    results.setCitations(request.getCitations().stream().distinct().collect(Collectors.toList()));
                    results.setTotalResults(group.getNumItems());
                    return results;
                }
                // if we don't have the current version locally
                pageRequestRepository.deleteAll(requests);
            } 
            
            CitationResults results = zoteroManager.getGroupItems(user, groupId, page);
            PageRequest request = new PageRequest();
            request.setCitations(results.getCitations());
            request.setObjectId(groupId);
            request.setPageNumber(page);
            request.setPageSize(zoteroPageSize);
            request.setUser(user);
            request.setVersion(group.getVersion());
            request.setZoteroObjectType(ZoteroObjectType.GROUP);
            pageRequestRepository.save(request);
            return results;
        }
        throw new GroupDoesNotExistException("There is no group with id " + groupId);
    }
}
