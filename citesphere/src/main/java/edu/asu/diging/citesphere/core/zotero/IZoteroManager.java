package edu.asu.diging.citesphere.core.zotero;

import java.util.List;
import java.util.Map;

import org.springframework.social.zotero.exception.ZoteroConnectionException;

import edu.asu.diging.citesphere.core.exceptions.ZoteroItemCreationFailedException;
import edu.asu.diging.citesphere.core.model.IUser;
import edu.asu.diging.citesphere.core.model.bib.ICitation;
import edu.asu.diging.citesphere.core.model.bib.ICitationCollection;
import edu.asu.diging.citesphere.core.model.bib.ICitationGroup;
import edu.asu.diging.citesphere.core.model.bib.ItemType;
import edu.asu.diging.citesphere.core.model.bib.impl.BibField;
import edu.asu.diging.citesphere.core.model.bib.impl.CitationCollectionResult;
import edu.asu.diging.citesphere.core.model.bib.impl.CitationResults;

public interface IZoteroManager {

    List<ICitationGroup> getGroups(IUser user);

    CitationResults getGroupItems(IUser user, String groupId, int page, String sortBy, Long lastGroupVersion);

    ICitation getGroupItem(IUser user, String groupId, String itemKey);

    Map<Long, Long> getGroupsVersion(IUser user);

    ICitationGroup getGroup(IUser user, String groupId, boolean refresh);

    ICitation updateCitation(IUser user, String groupId, ICitation citation) throws ZoteroConnectionException;

    List<BibField> getFields(IUser user, ItemType itemType);

    long getGroupItemVersion(IUser user, String groupId, String itemKey);

    ICitation createCitation(IUser user, String groupId, ICitation citation)
            throws ZoteroConnectionException, ZoteroItemCreationFailedException;

    CitationCollectionResult getCitationCollections(IUser user, String groupId, String parentCollectionId, int page, String sortBy, Long lastGroupVersion);

    CitationResults getCollectionItems(IUser user, String groupId, String collectionId, int page, String sortBy, Long lastGroupVersion);

    ICitationCollection getCitationCollection(IUser user, String groupId, String collectionId);
    
    List<String> getValidCreatorTypes(IUser user, ItemType itemType);

}