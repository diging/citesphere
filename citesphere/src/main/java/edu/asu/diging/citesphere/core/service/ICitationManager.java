package edu.asu.diging.citesphere.core.service;

import java.util.List;

import org.springframework.social.zotero.exception.ZoteroConnectionException;

import edu.asu.diging.citesphere.core.exceptions.CannotFindCitationException;
import edu.asu.diging.citesphere.core.exceptions.CitationIsOutdatedException;
import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroItemCreationFailedException;
import edu.asu.diging.citesphere.core.model.IUser;
import edu.asu.diging.citesphere.core.model.bib.ICitation;
import edu.asu.diging.citesphere.core.model.bib.ICitationGroup;
import edu.asu.diging.citesphere.core.model.bib.ItemType;
import edu.asu.diging.citesphere.core.model.bib.impl.BibField;
import edu.asu.diging.citesphere.core.model.bib.impl.CitationResults;
import edu.asu.diging.citesphere.core.model.jobs.IUploadJob;

public interface ICitationManager {

    List<ICitationGroup> getGroups(IUser user);
    
    List<ICitationGroup> getCitationGroup(List<IUploadJob> jobList);

    CitationResults getGroupItems(IUser user, String groupId, String collectionId, int page, String sortBy) throws GroupDoesNotExistException;

    ICitation getCitation(IUser user, String groupId, String key) throws GroupDoesNotExistException, CannotFindCitationException;

    void updateCitation(IUser user, String groupId, ICitation citation) throws ZoteroConnectionException, CitationIsOutdatedException;

    List<BibField> getItemTypeFields(IUser user, ItemType itemType);

    ICitation getCitationFromZotero(IUser user, String groupId, String key);

    void detachCitation(ICitation citation);

    ICitation updateCitationFromZotero(IUser user, String groupId, String itemKey) throws GroupDoesNotExistException, CannotFindCitationException;

    ICitation createCitation(IUser user, String groupId, List<String> collectionIds, ICitation citation)
            throws ZoteroConnectionException, ZoteroItemCreationFailedException, GroupDoesNotExistException;

    List<String> getValidCreatorTypes(IUser user, ItemType itemType);

}