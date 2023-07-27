package edu.asu.diging.citesphere.core.service.giles;

import org.springframework.social.zotero.exception.ZoteroConnectionException;

import edu.asu.diging.citesphere.core.exceptions.CitationIsOutdatedException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroHttpStatusException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroItemCreationFailedException;
import edu.asu.diging.citesphere.model.bib.ICitation;
import edu.asu.diging.citesphere.model.bib.IGilesUpload;
import edu.asu.diging.citesphere.user.IUser;

public interface IGilesDeletionChecker {
    void add(IGilesUpload upload, ICitation citation, String zoteroId, IUser user);
    
    void checkDeletion() throws ZoteroConnectionException, CitationIsOutdatedException, ZoteroHttpStatusException, ZoteroItemCreationFailedException;
}
