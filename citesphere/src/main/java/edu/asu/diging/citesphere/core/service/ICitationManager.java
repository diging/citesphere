package edu.asu.diging.citesphere.core.service;

import java.util.List;

import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.model.IUser;
import edu.asu.diging.citesphere.core.model.bib.ICitationGroup;
import edu.asu.diging.citesphere.core.model.bib.impl.CitationResults;

public interface ICitationManager {

    List<ICitationGroup> getGroups(IUser user);

    CitationResults getGroupItems(IUser user, String groupId, int page, String sortBy) throws GroupDoesNotExistException;

}