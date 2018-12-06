package edu.asu.diging.citesphere.core.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.asu.diging.citesphere.core.model.IUser;
import edu.asu.diging.citesphere.core.model.bib.ICitationGroup;
import edu.asu.diging.citesphere.core.model.bib.impl.CitationGroup;
import edu.asu.diging.citesphere.core.repository.bib.CitationGroupRepository;
import edu.asu.diging.citesphere.core.service.ICitationManager;
import edu.asu.diging.citesphere.core.zotero.IZoteroManager;

@Service
public class CitationManager implements ICitationManager {
    
    @Autowired
    private IZoteroManager zoteroManager;
    
    @Autowired
    private CitationGroupRepository groupRepository;

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
                groups.add(groupOptional.get());
            } else {
                ICitationGroup citGroup = zoteroManager.getGroup(user, id + "");
                groups.add(citGroup);
                groupRepository.save((CitationGroup)citGroup);
            }
        }
        return groups;
    }
}
