package edu.asu.diging.citesphere.core.service;

import java.util.List;

import org.springframework.social.zotero.exception.ZoteroConnectionException;

import edu.asu.diging.citesphere.core.exceptions.CannotFindCitationException;
import edu.asu.diging.citesphere.core.exceptions.CannotFindCitationVersionException;
import edu.asu.diging.citesphere.core.exceptions.CitationIsOutdatedException;
import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroHttpStatusException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroItemCreationFailedException;
import edu.asu.diging.citesphere.core.model.bib.CitationVersion;
import edu.asu.diging.citesphere.model.bib.ICitation;
import edu.asu.diging.citesphere.user.IUser;

/**
 * Fetches the details about Citation versions
 * 
 * @author Maulik Limbadiya
 */
public interface ICitationVersionManager {

    /**
     * Fetches the list of versions for the given citation key
     * 
     * @param user     User requesting the version list
     * @param groupId  Id of the Zotero group
     * @param key      Citation key
     * @param page     Page number
     * @param pageSize Page size
     * @return List of citation versions on the requested page
     */
    List<CitationVersion> getCitationVersions(IUser user, String groupId, String key, int page, int pageSize)
            throws GroupDoesNotExistException;

    /**
     * Gives the total version count for the given citation key
     * 
     * @param groupId  Id of the Zotero group
     * @param key      Citation key
     * @param pageSize Page size
     * @return total version count
     */
    int getTotalCitationVersionPages(String groupId, String key, int pageSize);

    /**
     * Fetches a citation version
     * 
     * @param user    User requesting the version
     * @param groupId Id of the Zotero group
     * @param key     Citation key
     * @param version Citation version number
     * @return the requested citation version
     */
    ICitation getCitationVersion(IUser user, String groupId, String key, Long version)
            throws GroupDoesNotExistException;
    
    /**
     * Reverts the citation to a previous version
     * @param user    User requesting the revert operation
     * @param groupId Id of the Zotero group
     * @param key     Citation key
     * @param version Citation version number
     */
    void revertCitationVersion(IUser user, String groupId, String key, Long version)
            throws GroupDoesNotExistException, ZoteroConnectionException, CitationIsOutdatedException,
            ZoteroHttpStatusException, CannotFindCitationVersionException, CannotFindCitationException, ZoteroItemCreationFailedException;

}
