package edu.asu.diging.citesphere.core.service;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.data.util.CloseableIterator;
import org.springframework.social.zotero.api.ZoteroUpdateItemsStatuses;
import org.springframework.social.zotero.exception.ZoteroConnectionException;

import com.fasterxml.jackson.core.JsonProcessingException;

import edu.asu.diging.citesphere.core.exceptions.AccessForbiddenException;
import edu.asu.diging.citesphere.core.exceptions.CannotFindCitationException;
import edu.asu.diging.citesphere.core.exceptions.CitationIsOutdatedException;
import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.exceptions.SyncInProgressException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroHttpStatusException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroItemCreationFailedException;
import edu.asu.diging.citesphere.core.service.impl.CitationPage;
import edu.asu.diging.citesphere.model.bib.ICitation;
import edu.asu.diging.citesphere.model.bib.ICitationGroup;
import edu.asu.diging.citesphere.model.bib.ItemType;
import edu.asu.diging.citesphere.model.bib.impl.BibField;
import edu.asu.diging.citesphere.model.bib.impl.CitationResults;
import edu.asu.diging.citesphere.user.IUser;

public interface ICitationManager {

    List<ICitationGroup> getGroups(IUser user);

    CitationResults getGroupItems(IUser user, String groupId, String collectionId, int page, String sortBy, List<String> conceptIds)
            throws GroupDoesNotExistException, ZoteroHttpStatusException;

    /**
     * Method to retrieve a citation object from the database. This method will retrieve the citation stored in the database.
     * It will not do any access checks, nor will it update the citation from Zotero or retrieve it for the first time. This method
     * should only be used if the current stored version is required.
     * @param key The key of the citation.
     * @return Citation to be retrieve or null.
     */
    ICitation getCitation(String key);
    
    ICitation getCitation(IUser user, String groupId, String key) throws GroupDoesNotExistException,
            CannotFindCitationException, AccessForbiddenException, ZoteroHttpStatusException;
    
    List<ICitation> getAttachments(IUser user, String groupId, String key) throws AccessForbiddenException,
            GroupDoesNotExistException, CannotFindCitationException, ZoteroHttpStatusException;
    
    List<ICitation> getNotes(IUser user, String groupId, String key)
            throws GroupDoesNotExistException, CannotFindCitationException, ZoteroHttpStatusException;

    void updateCitation(IUser user, String groupId, ICitation citation) throws ZoteroConnectionException,
            CitationIsOutdatedException, ZoteroHttpStatusException, ZoteroItemCreationFailedException;
   
    ZoteroUpdateItemsStatuses updateCitations(IUser user, String groupId, List<ICitation> citations)
            throws ZoteroConnectionException, CitationIsOutdatedException, ZoteroHttpStatusException,
            ExecutionException, JsonProcessingException;

    List<BibField> getItemTypeFields(IUser user, ItemType itemType);

    ICitation getCitationFromZotero(IUser user, String groupId, String key) throws ZoteroHttpStatusException;

    ICitation updateCitationFromZotero(IUser user, String groupId, String itemKey)
            throws GroupDoesNotExistException, CannotFindCitationException, ZoteroHttpStatusException;
    
    List<ICitation> updateAttachmentsFromZotero(IUser user, String groupId, String itemKey)
            throws GroupDoesNotExistException, CannotFindCitationException, ZoteroHttpStatusException;
    
    List<ICitation> updateNotesFromZotero(IUser user, String groupId, String itemKey)
            throws GroupDoesNotExistException, CannotFindCitationException, ZoteroHttpStatusException;

    ICitation createCitation(IUser user, String groupId, List<String> collectionIds, ICitation citation)
            throws ZoteroConnectionException, ZoteroItemCreationFailedException, GroupDoesNotExistException,
            ZoteroHttpStatusException;

    List<String> getValidCreatorTypes(IUser user, ItemType itemType);
    
    CitationPage getPrevAndNextCitation(IUser user, String groupId, String collectionId, int page, String sortBy,
            int index, List<String> conceptIds) throws GroupDoesNotExistException, ZoteroHttpStatusException;

    void forceGroupItemsRefresh(IUser user, String groupId, String collectionId, int page, String sortBy);

    CloseableIterator<ICitation> getAllGroupItems(IUser user, String groupId, String collectionId) throws
            ZoteroHttpStatusException, SyncInProgressException, GroupDoesNotExistException, AccessForbiddenException;
    
    void deleteLocalGroupCitations(String groupId);
    
    ICitation updateCitationReference(ICitation citation, String reference);
    
}