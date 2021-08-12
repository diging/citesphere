package edu.asu.diging.citesphere.core.zotero;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.social.zotero.api.ItemDeletionResponse;
import org.springframework.social.zotero.api.ZoteroUpdateItemsStatuses;
import org.springframework.social.zotero.exception.ZoteroConnectionException;

import com.fasterxml.jackson.core.JsonProcessingException;

import edu.asu.diging.citesphere.core.exceptions.ZoteroHttpStatusException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroItemCreationFailedException;
import edu.asu.diging.citesphere.model.bib.ICitation;
import edu.asu.diging.citesphere.model.bib.ICitationCollection;
import edu.asu.diging.citesphere.model.bib.ICitationGroup;
import edu.asu.diging.citesphere.model.bib.ItemType;
import edu.asu.diging.citesphere.model.bib.impl.BibField;
import edu.asu.diging.citesphere.model.bib.impl.CitationCollectionResult;
import edu.asu.diging.citesphere.model.bib.impl.CitationResults;
import edu.asu.diging.citesphere.user.IUser;

public interface IZoteroManager {

    List<ICitationGroup> getGroups(IUser user);

    CitationResults getGroupItems(IUser user, String groupId, int page, String sortBy, Long lastGroupVersion)
            throws ZoteroHttpStatusException;

    ICitation getGroupItem(IUser user, String groupId, String itemKey) throws ZoteroHttpStatusException;
    
    List<ICitation> getGroupItemAttachments(IUser user, String groupId, String itemKey) throws ZoteroHttpStatusException;

    Map<Long, Long> getGroupsVersion(IUser user);

    ICitationGroup getGroup(IUser user, String groupId, boolean refresh);

    ICitation updateCitation(IUser user, String groupId, ICitation citation) throws ZoteroConnectionException, ZoteroHttpStatusException;
    
    ZoteroUpdateItemsStatuses updateCitations(IUser user, String groupId, List<ICitation> citations)
            throws ZoteroConnectionException, ZoteroHttpStatusException, ExecutionException, JsonProcessingException;

    List<BibField> getFields(IUser user, ItemType itemType);

    long getGroupItemVersion(IUser user, String groupId, String itemKey);

    ICitation createCitation(IUser user, String groupId, List<String> collectionIds, ICitation citation)
            throws ZoteroConnectionException, ZoteroItemCreationFailedException, ZoteroHttpStatusException;

    CitationCollectionResult getCitationCollections(IUser user, String groupId, String parentCollectionId, int page,
            String sortBy, Long lastGroupVersion);

    CitationResults getCollectionItems(IUser user, String groupId, String collectionId, int page, String sortBy,
            Long lastGroupVersion) throws ZoteroHttpStatusException;

    ICitationCollection getCitationCollection(IUser user, String groupId, String collectionId);

    List<String> getValidCreatorTypes(IUser user, ItemType itemType);

    void forceRefresh(IUser user, String zoteroGroupId, String collectionId, int page, String sortBy, Long lastGroupVersion);

    boolean isGroupModified(IUser user, String groupId, long lastGroupVersion) throws ZoteroHttpStatusException;

    Map<String, Long> getGroupItemsVersions(IUser user, String groupId, long version, boolean includeTrashed);

    ZoteroGroupItemsResponse getGroupItemsByKey(IUser user, String groupId, List<String> itemKeys, boolean includeTrashed);

    DeletedZoteroElements getDeletedElements(IUser user, String groupId, long version);

    Map<String, Long> getCollectionsVersions(IUser user, String groupId, String groupVersion);

    ZoteroCollectionsResponse getCollectionsByKey(IUser user, String groupId, List<String> keys);

    long getLatestGroupVersion(IUser user, String groupId);
        
    Map<ItemDeletionResponse, List<String>> deleteMultipleItems(IUser user, String groupId, List<String> citationKeys, Long citationVersion) throws ZoteroConnectionException, ZoteroHttpStatusException;
}