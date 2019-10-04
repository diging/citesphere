package edu.asu.diging.citesphere.core.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.asu.diging.citesphere.core.model.IUser;
import edu.asu.diging.citesphere.core.model.bib.ICitationGroup;
import edu.asu.diging.citesphere.core.model.bib.impl.CitationGroup;
import edu.asu.diging.citesphere.core.model.jobs.IUploadJob;
import edu.asu.diging.citesphere.core.repository.bib.CitationGroupRepository;
import edu.asu.diging.citesphere.core.service.IGroupManager;
import edu.asu.diging.citesphere.core.zotero.IZoteroManager;

@Service
public class GroupManager implements IGroupManager {

    @Autowired
    private CitationGroupRepository groupRepository;
    
    @Autowired
    private IZoteroManager zoteroManager;
   
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.service.impl.IGroupManager#getGroup(java.lang.Long)
     */
    @Override
    public ICitationGroup getGroup(IUser user, String groupId) {
        Optional<CitationGroup> groupOptional = groupRepository.findById(new Long(groupId));
        if (groupOptional.isPresent()) {
            return (ICitationGroup) groupOptional.get();
        }
        
        ICitationGroup group = zoteroManager.getGroup(user, groupId, false);
        groupRepository.save((CitationGroup)group);
        return null;
    }
}
