package edu.asu.diging.citesphere.core.service.impl;

import java.time.OffsetDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import edu.asu.diging.citesphere.core.service.ICitationManager;
import edu.asu.diging.citesphere.core.service.IGroupManager;
import edu.asu.diging.citesphere.core.zotero.IZoteroManager;
import edu.asu.diging.citesphere.data.bib.CitationGroupRepository;
import edu.asu.diging.citesphere.model.bib.ICitationGroup;
import edu.asu.diging.citesphere.model.bib.impl.CitationGroup;
import edu.asu.diging.citesphere.user.IUser;

@Service
public class GroupManager implements IGroupManager {

    @Autowired
    private CitationGroupRepository groupRepository;

    @Autowired
    private IZoteroManager zoteroManager;
    
    @Autowired
    private ICitationManager citationManager;

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.diging.citesphere.core.service.impl.IGroupManager#getGroup(java.lang.
     * Long)
     */
    @Override
    public ICitationGroup getGroup(IUser user, String groupId) {
        Optional<CitationGroup> groupOptional = groupRepository.findById(new Long(groupId));
        if (groupOptional.isPresent() && groupOptional.get().getUsers().contains(user.getUsername())) {
            return (ICitationGroup) groupOptional.get();
        }

        ICitationGroup group = zoteroManager.getGroup(user, groupId, false);
        if (group != null) {
            group.getUsers().add(user.getUsername());
            groupRepository.save((CitationGroup) group);
            return group;
        }
        return null;
    }
    
    @Async
    @Override
    public void updateGroup(IUser user, Long id, ICitationGroup group) {
        group.setUpdateRequestedOn(OffsetDateTime.now());
        group = zoteroManager.getGroup(user, id + "", true);
        group.setUpdatedOn(OffsetDateTime.now());
        citationManager.addUserToGroup(group, user);
        groupRepository.save((CitationGroup)group);
    }
    
    @Async
    @Override
    public void addGroup(IUser user, Long id) {
        ICitationGroup group = new CitationGroup();
        group.setUpdateRequestedOn(OffsetDateTime.now());
        group = zoteroManager.getGroup(user, id + "", false);
        group.setUpdatedOn(OffsetDateTime.now());
        citationManager.addUserToGroup(group, user);
        groupRepository.save((CitationGroup)group);
    }
}
