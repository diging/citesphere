package edu.asu.diging.citesphere.core.service;

import java.util.List;

import org.springframework.social.zotero.exception.ZoteroConnectionException;

import edu.asu.diging.citesphere.core.exceptions.CitationIsOutdatedException;
import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroItemCreationFailedException;
import edu.asu.diging.citesphere.core.model.IUser;
import edu.asu.diging.citesphere.core.model.bib.ICitation;
import edu.asu.diging.citesphere.core.model.bib.ICitationGroup;
import edu.asu.diging.citesphere.core.model.bib.ItemType;
import edu.asu.diging.citesphere.core.model.bib.impl.BibField;
import edu.asu.diging.citesphere.core.model.bib.impl.CitationResults;

public interface ICitationManager {

    List<ICitationGroup> getGroups(IUser user);

    CitationResults getGroupItems(IUser user, String groupId, int page, String sortBy) throws GroupDoesNotExistException;

    ICitation getCitation(IUser user, String groupId, String key) throws GroupDoesNotExistException;

    void updateCitation(IUser user, String groupId, ICitation citation) throws ZoteroConnectionException, CitationIsOutdatedException;

    List<BibField> getItemTypeFields(IUser user, ItemType itemType);

    ICitation getCitationFromZotero(IUser user, String groupId, String key);

    void detachCitation(ICitation citation);

    ICitation updateCitationFromZotero(IUser user, String groupId, String itemKey) throws GroupDoesNotExistException;

    ICitation createCitation(IUser user, String groupId, ICitation citation)
            throws ZoteroConnectionException, ZoteroItemCreationFailedException, GroupDoesNotExistException;

}