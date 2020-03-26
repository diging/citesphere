package edu.asu.diging.citesphere.core.service;

import java.util.List;

import org.springframework.social.zotero.exception.ZoteroConnectionException;

import edu.asu.diging.citesphere.core.exceptions.AccessForbiddenException;
import edu.asu.diging.citesphere.core.exceptions.CannotFindCitationException;
import edu.asu.diging.citesphere.core.exceptions.CitationIsOutdatedException;
import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroHttpStatusException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroItemCreationFailedException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroItemDeletionFailedException;
import edu.asu.diging.citesphere.model.bib.ICitation;
import edu.asu.diging.citesphere.model.bib.ICitationGroup;
import edu.asu.diging.citesphere.model.bib.ItemType;
import edu.asu.diging.citesphere.model.bib.impl.BibField;
import edu.asu.diging.citesphere.model.bib.impl.CitationResults;
import edu.asu.diging.citesphere.user.IUser;

public interface ICitationManager {

    List<ICitationGroup> getGroups(IUser user);

    CitationResults getGroupItems(IUser user, String groupId, String collectionId, int page, String sortBy)
            throws GroupDoesNotExistException, ZoteroHttpStatusException;

    ICitation getCitation(IUser user, String groupId, String key) throws GroupDoesNotExistException,
            CannotFindCitationException, AccessForbiddenException, ZoteroHttpStatusException;

    void updateCitation(IUser user, String groupId, ICitation citation)
            throws ZoteroConnectionException, CitationIsOutdatedException, ZoteroHttpStatusException;

    List<BibField> getItemTypeFields(IUser user, ItemType itemType);

    ICitation getCitationFromZotero(IUser user, String groupId, String key) throws ZoteroHttpStatusException;

    void detachCitation(ICitation citation);

    ICitation updateCitationFromZotero(IUser user, String groupId, String itemKey)
            throws GroupDoesNotExistException, CannotFindCitationException, ZoteroHttpStatusException;

    ICitation createCitation(IUser user, String groupId, List<String> collectionIds, ICitation citation)
            throws ZoteroConnectionException, ZoteroItemCreationFailedException, GroupDoesNotExistException,
            ZoteroHttpStatusException;

    boolean deleteCitation(IUser user, String groupId, ICitation citation) throws ZoteroConnectionException,
            GroupDoesNotExistException, ZoteroHttpStatusException, ZoteroItemDeletionFailedException;

    List<String> getValidCreatorTypes(IUser user, ItemType itemType);

    void forceGroupItemsRefresh(IUser user, String groupId, String collectionId, int page, String sortBy);

}