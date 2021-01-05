package edu.asu.diging.citesphere.core.service.impl;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import com.mongodb.diagnostics.logging.Logger;

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
    public ICitationGroup deleteGroup(IUser user, String groupId) {      
    	System.out.println(groupId);
        ICitationGroup group = zoteroManager.getGroup(user, groupId, false);
//        System.out.println("Group obj" + group);
//        System.out.println("Group obj" + group.getGroupId());
//        System.out.println("Group obj" + group.getId());
//        System.out.println("Group obj" + group.getName());
        group.getUsers().add(user.getUsername());       
        groupRepository.delete((CitationGroup) group);
       
        return null;
        
        
    }
}
