package edu.asu.diging.citesphere.core.service;

import java.util.List;

import edu.asu.diging.citesphere.core.exceptions.AccessForbiddenException;
import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.model.bib.CitationVersion;
import edu.asu.diging.citesphere.model.bib.ICitation;
import edu.asu.diging.citesphere.user.IUser;

public interface ICitationVersionManager {
    
    List<CitationVersion> getCitationVersions(IUser user, String groupId, String key, int page, int pageSize)
            throws AccessForbiddenException, GroupDoesNotExistException;

    int getTotalCitationVersionPages(String groupId, String key, int pageSize);

    ICitation getCitationVersion(IUser user, String groupId, String key, Long version)
            throws AccessForbiddenException, GroupDoesNotExistException;
    
}
