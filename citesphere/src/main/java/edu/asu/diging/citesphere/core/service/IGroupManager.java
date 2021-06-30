package edu.asu.diging.citesphere.core.service;

import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.model.bib.ICitationGroup;
import edu.asu.diging.citesphere.user.IUser;

public interface IGroupManager {

    ICitationGroup getGroup(IUser user, String groupId);
    
    /**
     * Does not delete the group completely. Only deletes the local copy of citation group
     * @param groupId Zotero id of the group to be deleted
     */
    void deleteLocalGroupCopy(String groupId);
}