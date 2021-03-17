package edu.asu.diging.citesphere.core.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.asu.diging.citesphere.core.mongo.CustomCitationGroupRepository;
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
    private CustomCitationGroupRepository customGroupRepo;

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.diging.citesphere.core.service.impl.IGroupManager#getGroup(java.lang.
     * Long)
     */
    @Override
    public ICitationGroup getGroup(IUser user, String groupId) {
        Optional<ICitationGroup> groupOptional = groupRepository.findFirstByGroupId(new Long(groupId));
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
    
    @Override
    public void deleteLocalGroupCopy(String groupId) {
        customGroupRepo.deleteByGroupId(Integer.parseInt(groupId));
    }
}
