package edu.asu.diging.citesphere.core.service;

import edu.asu.diging.citesphere.core.model.IUser;
import edu.asu.diging.citesphere.core.model.bib.ICitationGroup;

public interface IGroupManager {

    ICitationGroup getGroup(IUser user, String groupId);

}