package edu.asu.diging.citesphere.core.zotero.impl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.zotero.api.Collection;
import org.springframework.social.zotero.api.CreatorType;
import org.springframework.social.zotero.api.Data;
import org.springframework.social.zotero.api.DeletedElements;
import org.springframework.social.zotero.api.FieldInfo;
import org.springframework.social.zotero.api.Group;
import org.springframework.social.zotero.api.Item;
import org.springframework.social.zotero.api.ItemDeletionResponse;
import org.springframework.social.zotero.api.ZoteroFields;
import org.springframework.social.zotero.api.ZoteroResponse;
import org.springframework.social.zotero.api.ZoteroUpdateItemsStatuses;
import org.springframework.social.zotero.exception.ZoteroConnectionException;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;

import edu.asu.diging.citesphere.core.exceptions.ZoteroHttpStatusException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroItemCreationFailedException;
import edu.asu.diging.citesphere.core.factory.zotero.IItemFactory;
import edu.asu.diging.citesphere.core.zotero.DeletedZoteroElements;
import edu.asu.diging.citesphere.core.zotero.IZoteroConnector;
import edu.asu.diging.citesphere.core.zotero.IZoteroManager;
import edu.asu.diging.citesphere.core.zotero.ZoteroCollectionsResponse;
import edu.asu.diging.citesphere.core.zotero.ZoteroGroupItemsResponse;
import edu.asu.diging.citesphere.factory.ICitationCollectionFactory;
import edu.asu.diging.citesphere.factory.ICitationFactory;
import edu.asu.diging.citesphere.factory.IGroupFactory;
import edu.asu.diging.citesphere.model.bib.ICitation;
import edu.asu.diging.citesphere.model.bib.ICitationCollection;
import edu.asu.diging.citesphere.model.bib.ICitationGroup;
import edu.asu.diging.citesphere.model.bib.ItemType;
import edu.asu.diging.citesphere.model.bib.impl.BibField;
import edu.asu.diging.citesphere.model.bib.impl.CitationCollectionResult;
import edu.asu.diging.citesphere.model.bib.impl.CitationResults;
import edu.asu.diging.citesphere.user.IUser;

@Service
public class ZoteroManager implements IZoteroManager {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private IZoteroConnector zoteroConnector;

    @Autowired
    private ICitationFactory citationFactory;

    @Autowired
    private IGroupFactory groupFactory;

    @Autowired
    private IItemFactory itemFactory;

    @Autowired
    private ICitationCollectionFactory collectionFactory;

    public CitationResults getGroupItems(IUser user, String groupId, int page, String sortBy, Long lastGroupVersion)
            throws ZoteroHttpStatusException {
        ZoteroResponse<Item> response = zoteroConnector.getGroupItems(user, groupId, page, sortBy, lastGroupVersion);
        return createCitationResults(user, groupId, response);
    }

    @Override
    public List<ICitationGroup> getGroups(IUser user) {
        ZoteroResponse<Group> response = zoteroConnector.getGroups(user);
        List<ICitationGroup> groups = new ArrayList<>();
        for (Group group : response.getResults()) {
            ICitationGroup citGroup = groupFactory.createGroup(group);
            ZoteroResponse<Item> groupItems = zoteroConnector.getGroupItemsWithLimit(user, group.getId() + "", 1, null,
                    null);
            citGroup.setNumItems(groupItems.getTotalResults());
            groups.add(citGroup);
        }
        return groups;
    }
    
    @Override
    public Map<String, Long> getGroupItemsVersions(IUser user, String groupId, long version, boolean includeTrashed) {
        ZoteroResponse<Item> response = zoteroConnector.getGroupItemsVersions(user, groupId, version, includeTrashed);
        Map<String, Long> itemVersions = new HashMap<>();
        for (Item item : response.getResults()) {
            itemVersions.put(item.getKey(), item.getVersion());            
        }
        return itemVersions;
    }

    @Override
    public Map<Long, Long> getGroupsVersion(IUser user) {
        ZoteroResponse<Group> response = zoteroConnector.getGroupsVersions(user);
        if (response == null) {
            return null;
        }
        Map<Long, Long> groupVersions = new HashMap<>();
        for (Group group : response.getResults()) {
            groupVersions.put(group.getId(), group.getVersion());
        }
        return groupVersions;
    }

    @Override
    public ICitation getGroupItem(IUser user, String groupId, String itemKey) throws ZoteroHttpStatusException {
        Item item = zoteroConnector.getItem(user, groupId, itemKey);
        Item metaData = null;
        if (!item.getData().getItemType().equals(ItemType.NOTE.getZoteroKey())
                && !item.getData().getItemType().equals(ItemType.ATTACHMENT.getZoteroKey())) {
            metaData = zoteroConnector.getCitesphereMetaData(user, groupId, itemKey);
        }
        return citationFactory.createCitation(item, metaData);
    }
    
    @Override
    public List<ICitation> getGroupItemAttachments(IUser user, String groupId, String itemKey) throws ZoteroHttpStatusException {
        List<Item> attachmentResponse = zoteroConnector.getAttachments(user, groupId, itemKey);
        List<ICitation> attachments = new ArrayList<>();
        attachmentResponse.forEach(attachment -> {
            attachments.add(citationFactory.createCitation(attachment, null));
        });
        return attachments;
    }
    
    @Override
    public List<ICitation> getGroupItemNotes(IUser user, String groupId, String itemKey) throws ZoteroHttpStatusException {
        List<Item> notesResponse = zoteroConnector.getNotes(user, groupId, itemKey);
        List<ICitation> notes = new ArrayList<>();
        notesResponse.forEach(note -> {
            notes.add(citationFactory.createCitation(note, null));
        });
        return notes;
    }
    
    @Override
    public ZoteroGroupItemsResponse getGroupItemsByKey(IUser user, String groupId, List<String> itemKeys,
            boolean includeTrashed) throws ZoteroHttpStatusException {
        ZoteroResponse<Item> response = zoteroConnector.getGroupItemsByKey(user, groupId, itemKeys, includeTrashed);

        ZoteroGroupItemsResponse zoteroResponse = new ZoteroGroupItemsResponse();
        zoteroResponse.setContentVersion(response.getLastVersion());
        zoteroResponse.setCitations(new ArrayList<>());
        // Holds the metadata note against the item key
        HashMap<String, Item> metaDataMap = new HashMap<>();
        // Holds the items to be updated
        HashMap<String, Item> itemMap = new HashMap<>();
        for (Item item : response.getResults()) {
            // If the item is not already visited, process it
            if (!itemMap.containsKey(item.getKey())) {
                // If the item is an active(non-deleted) metadata note, fetch its parent item if
                // it is not already present in the map.
                // Else check for metadata note for the item and fetch it.
                if (item.getData().getDeleted() != 1 && item.isMetaDataNote()) {
                    metaDataMap.put(item.getData().getParentItem(), item);
                    if (!itemMap.containsKey(item.getData().getParentItem())) {
                        Item parentItem = zoteroConnector.getItem(user, groupId, item.getData().getParentItem());
                        itemMap.put(parentItem.getKey(), parentItem);
                    }
                } else if (!item.getData().getItemType().equals(ItemType.NOTE.getZoteroKey())
                        && !item.getData().getItemType().equals(ItemType.ATTACHMENT.getZoteroKey())) {
                    Item metaData = zoteroConnector.getCitesphereMetaData(user, groupId, item.getKey());
                    if (metaData != null) {
                        metaDataMap.put(item.getKey(), metaData);
                    }
                }
                itemMap.put(item.getKey(), item);
            }
        }
        itemMap.values().forEach(item -> {
            zoteroResponse.getCitations()
                    .add(citationFactory.createCitation(item, metaDataMap.getOrDefault(item.getKey(), null)));
        });
        return zoteroResponse;
    }

    @Override
    public long getGroupItemVersion(IUser user, String groupId, String itemKey) {
        return zoteroConnector.getItemVersion(user, groupId, itemKey);
    }

    @Override
    public ICitationGroup getGroup(IUser user, String groupId, boolean forceRefresh) {
        Group group = zoteroConnector.getGroup(user, groupId, forceRefresh);
        ZoteroResponse<Item> groupItems = zoteroConnector.getGroupItemsWithLimit(user, group.getId() + "", 1, null,
                null);
        ICitationGroup citGroup = groupFactory.createGroup(group);
        citGroup.setNumItems(groupItems.getTotalResults());
        return citGroup;
    }
    
    @Override
    public long getLatestGroupVersion(IUser user, String groupId) {
        ZoteroResponse<Item> groupItems = zoteroConnector.getGroupItemsWithLimit(user, groupId, 1, null,
                null);
        return groupItems.getLastVersion();
    }
    
    @Override
    public DeletedZoteroElements getDeletedElements(IUser user, String groupId, long version) {
        DeletedElements deletedElements = zoteroConnector.getDeletedElements(user, groupId, version);
        DeletedZoteroElements deletedZoteroElems = new DeletedZoteroElements();
        deletedZoteroElems.setCollections(deletedElements.getCollections());
        deletedZoteroElems.setItems(deletedElements.getItems());
        deletedZoteroElems.setTags(deletedElements.getTags());
        return deletedZoteroElems;
    }
    
    @Override
    public boolean isGroupModified(IUser user, String groupId, long lastGroupVersion) throws ZoteroHttpStatusException {
        return zoteroConnector.isGroupModified(user, groupId, lastGroupVersion);
    }

    @Override
    public ICitationCollection getCitationCollection(IUser user, String groupId, String collectionId) {
        Collection collection = zoteroConnector.getCitationCollection(user, groupId, collectionId);
        return collectionFactory.createCitationCollection(collection);
    }

    @Override
    public CitationCollectionResult getCitationCollections(IUser user, String groupId, String parentCollectionId,
            int page, String sortBy, Long lastGroupVersion) {
        ZoteroResponse<Collection> response = zoteroConnector.getCitationCollections(user, groupId, parentCollectionId,
                page, sortBy, lastGroupVersion);
        List<ICitationCollection> citationCollections = new ArrayList<>();
        if (response.getResults() != null) {
            for (Collection collection : response.getResults()) {
                citationCollections.add(collectionFactory.createCitationCollection(collection));
            }
        }
        CitationCollectionResult results = new CitationCollectionResult();
        results.setTotalResults(response.getTotalResults());
        if (response.getNotModified() != null && response.getNotModified()) {
            results.setNotModified(true);
        }
        results.setCitationCollections(citationCollections);
        return results;
    }

    @Override
    public CitationResults getCollectionItems(IUser user, String groupId, String collectionId, int page, String sortBy,
            Long lastGroupVersion) throws ZoteroHttpStatusException {
        ZoteroResponse<Item> response = zoteroConnector.getCollectionItems(user, groupId, collectionId, page, sortBy,
                lastGroupVersion);
        return createCitationResults(user, groupId, response);
    }
    
    @Override
    public Map<String, Long> getCollectionsVersions(IUser user, String groupId, String groupVersion) {
        ZoteroResponse<Collection> response = zoteroConnector.getCitationCollectionVersions(user, groupId, new Long(groupVersion));
        Map<String, Long> collectionVersions = new HashMap<>();
        for (Collection collection : response.getResults()) {
            collectionVersions.put(collection.getKey(), collection.getVersion());            
        }
        return collectionVersions;
    }
    
    @Override
    public ZoteroCollectionsResponse getCollectionsByKey(IUser user, String groupId, List<String> keys) {
        ZoteroResponse<Collection> response = zoteroConnector.getCitationCollectionsByKey(user, groupId, keys);
        List<ICitationCollection> collections = new ArrayList<>();
        for (Collection collection : response.getResults()) {
            collections.add(collectionFactory.createCitationCollection(collection));
        }
        ZoteroCollectionsResponse zoteroResponse = new ZoteroCollectionsResponse();
        zoteroResponse.setCollections(collections);
        zoteroResponse.setContentVersion(response.getLastVersion());
        return zoteroResponse;
    }

    @Override
    public void forceRefresh(IUser user, String zoteroGroupId, String collectionId, int page, String sortBy,
            Long lastGroupVersion) {
        if (collectionId == null || collectionId.trim().isEmpty()) {
            zoteroConnector.clearGroupItemsCache(user, zoteroGroupId, new Integer(page), sortBy, lastGroupVersion);
        } else {
            zoteroConnector.clearCollectionItemsCache(user, zoteroGroupId, collectionId, new Integer(page), sortBy,
                    lastGroupVersion);
        }
    }

    private CitationResults createCitationResults(IUser user, String groupId, ZoteroResponse<Item> response) throws ZoteroHttpStatusException {
        List<ICitation> citations = new ArrayList<>();
        if (response.getResults() != null) {
            for (Item item : response.getResults()) {
                Item metaData = null;
                if (!item.getData().getItemType().equals(ItemType.NOTE.getZoteroKey())) {
                    metaData = zoteroConnector.getCitesphereMetaData(user, groupId, item.getKey());
                }
                citations.add(citationFactory.createCitation(item, metaData));
            }
        }
        CitationResults results = new CitationResults();
        results.setTotalResults(response.getTotalResults());
        if (response.getNotModified() != null && response.getNotModified()) {
            results.setNotModified(true);
            return results;
        }
        results.setCitations(citations);
        return results;
    }

    @Override
    public List<BibField> getFields(IUser user, ItemType itemType) {
        FieldInfo[] fieldInfos = zoteroConnector.getFields(user, itemType.getZoteroKey());
        List<BibField> bibFields = new ArrayList<>();
        for (FieldInfo info : fieldInfos) {
            bibFields.add(new BibField(info.getField(), info.getLocalized()));
        }
        return bibFields;
    }

    @Override
    public ICitation updateCitation(IUser user, String groupId, ICitation citation)
            throws ZoteroConnectionException, ZoteroHttpStatusException, ZoteroItemCreationFailedException {
        Item item = itemFactory.createItem(citation, citation.getCollections());
        Item metaData = itemFactory.createMetaDataItem(citation);

        List<String> itemTypeFields = getItemTypeFields(user, citation.getItemType());
        // add fields that need to be submitted
        itemTypeFields.add(ZoteroFields.VERSION);
        itemTypeFields.add(ZoteroFields.ITEM_TYPE);
        itemTypeFields.add(ZoteroFields.CREATOR);
        

        List<String> ignoreFields = createIgnoreFields(itemTypeFields, item, false);

        if (citation.getCollections() != null) {
            ignoreFields.remove("collections");
        }

        List<String> validCreatorTypes = getValidCreatorTypes(user, citation.getItemType());
        Item updatedItem = zoteroConnector.updateItem(user, item, groupId, new ArrayList<>(), ignoreFields,
                validCreatorTypes);

        Item updatedMetaData = null;
        //If the metadata note already exists, update it or else create a new note
        itemTypeFields = getItemTypeFields(user, ItemType.NOTE);
        if (metaData.getKey() != null && !metaData.getKey().isEmpty()) {
            itemTypeFields.add(ZoteroFields.KEY);
            itemTypeFields.add(ZoteroFields.VERSION);
            ignoreFields = createIgnoreFields(itemTypeFields, metaData, true);
            updatedMetaData = zoteroConnector.updateItem(user, metaData, groupId, new ArrayList<>(), ignoreFields, null);
        } else {
            ignoreFields = createIgnoreFields(itemTypeFields, metaData, true);
            updatedMetaData = zoteroConnector.createItem(user, metaData, groupId, new ArrayList<>(), ignoreFields, null);
        }
        
        return citationFactory.createCitation(updatedItem, updatedMetaData);
    }
    
    /**
     * Using this method we can update multiple citations.
     * 
     * @param user      user accessing Zotero.
     * @param groupId   group id of citations.
     * @param citations list of citations that has to be updated.
     * 
     * @return ZoteroUpdateItemsStatuses returns statuses of citations
     */
    @Override
    public ZoteroUpdateItemsStatuses updateCitations(IUser user, String groupId, List<ICitation> citations)
            throws ZoteroConnectionException, ZoteroHttpStatusException, ExecutionException, JsonProcessingException {
        List<Item> items = new ArrayList<>();
        List<String> itemTypeFields = new ArrayList<>();
        List<List<String>> ignoreFieldsList = new ArrayList<>();
        List<List<String>> validCreatorTypesList = new ArrayList<>();
        for (ICitation citation : citations) {
            Item item = itemFactory.createItem(citation, citation.getCollections());            
            items.add(item);
            itemTypeFields = getItemTypeFields(user, citation.getItemType());
            itemTypeFields.add(ZoteroFields.ITEM_TYPE);
            itemTypeFields.add(ZoteroFields.CREATOR);
            itemTypeFields.add(ZoteroFields.COLLECTIONS);
            itemTypeFields.add(ZoteroFields.KEY);
            itemTypeFields.add(ZoteroFields.VERSION);
            List<String> ignoreFields = createIgnoreFields(itemTypeFields, item, true);
            List<String> validCreatorTypes = getValidCreatorTypes(user, citation.getItemType());
            ignoreFieldsList.add(ignoreFields);
            validCreatorTypesList.add(validCreatorTypes);
            
            //Add metadata note to the list
            Item metaData = itemFactory.createMetaDataItem(citation);
            items.add(metaData);
            itemTypeFields = getItemTypeFields(user, ItemType.NOTE);
            if (metaData.getKey() != null && !metaData.getKey().isEmpty()) {
                itemTypeFields.add(ZoteroFields.KEY);
                itemTypeFields.add(ZoteroFields.VERSION);
            }
            ignoreFields = createIgnoreFields(itemTypeFields, metaData, true);
            ignoreFieldsList.add(ignoreFields);
            validCreatorTypesList.add(new ArrayList<>());
        }
        return zoteroConnector.updateItems(user, items, groupId, ignoreFieldsList, validCreatorTypesList);
    }
    
    /**
     * Using this method we can move multiple citations to collection. This method
     * does not create or update citesphere metadata note as compared to the batch
     * update method.
     * 
     * @param user      user accessing Zotero.
     * @param groupId   group id of citations.
     * @param citations list of citations that has to be moved.
     * 
     * @return ZoteroUpdateItemsStatuses returns statuses of citations
     */
    @Override
    public ZoteroUpdateItemsStatuses moveCitationsToCollection(IUser user, String groupId, List<ICitation> citations)
            throws ZoteroConnectionException, ZoteroHttpStatusException, ExecutionException, JsonProcessingException {
        List<Item> items = new ArrayList<>();
        List<String> itemTypeFields = new ArrayList<>();
        List<List<String>> ignoreFieldsList = new ArrayList<>();
        List<List<String>> validCreatorTypesList = new ArrayList<>();
        for (ICitation citation : citations) {
            Item item = itemFactory.createItem(citation, citation.getCollections());            
            items.add(item);
            itemTypeFields = getItemTypeFields(user, citation.getItemType());
            itemTypeFields.add(ZoteroFields.ITEM_TYPE);
            itemTypeFields.add(ZoteroFields.CREATOR);
            itemTypeFields.add(ZoteroFields.COLLECTIONS);
            itemTypeFields.add(ZoteroFields.KEY);
            itemTypeFields.add(ZoteroFields.VERSION);
            List<String> ignoreFields = createIgnoreFields(itemTypeFields, item, true);
            List<String> validCreatorTypes = getValidCreatorTypes(user, citation.getItemType());
            ignoreFieldsList.add(ignoreFields);
            validCreatorTypesList.add(validCreatorTypes);
        }
        return zoteroConnector.updateItems(user, items, groupId, ignoreFieldsList, validCreatorTypesList);
    }
    
    @Override
    public ICitation createCitation(IUser user, String groupId, List<String> collectionIds, ICitation citation)
            throws ZoteroConnectionException, ZoteroItemCreationFailedException, ZoteroHttpStatusException {
        Item item = itemFactory.createItem(citation, collectionIds);
        Item metaData = itemFactory.createMetaDataItem(citation);

        List<String> itemTypeFields = getItemTypeFields(user, citation.getItemType());
        itemTypeFields.add(ZoteroFields.ITEM_TYPE);
        itemTypeFields.add(ZoteroFields.CREATOR);
        itemTypeFields.add(ZoteroFields.COLLECTIONS);

        List<String> ignoreFields = createIgnoreFields(itemTypeFields, item, true);
        List<String> validCreatorTypes = getValidCreatorTypes(user, citation.getItemType());

        Item newItem = zoteroConnector.createItem(user, item, groupId, collectionIds, ignoreFields, validCreatorTypes);

        metaData.getData().setParentItem(newItem.getKey());
        itemTypeFields = getItemTypeFields(user, ItemType.NOTE);
        ignoreFields = createIgnoreFields(itemTypeFields, metaData, true);
        Item newMetaData = zoteroConnector.createItem(user, metaData, groupId, new ArrayList<>(), ignoreFields, null);
        return citationFactory.createCitation(newItem, newMetaData);
    }
    
    private List<String> createIgnoreFields(List<String> itemTypeFields, Item item, boolean ignoreEmpty) {
        List<String> ignoreFields = new ArrayList<>();
        // general fields to ignore for the moment
        ignoreFields.add("relations");
        ignoreFields.add("linkMode");
        ignoreFields.add("contentType");
        ignoreFields.add("charset");
        ignoreFields.add("filename");
        ignoreFields.add("md5");
        ignoreFields.add("mtime");
        ignoreFields.add("dateModified");

        Field[] fields = Data.class.getDeclaredFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            // already ignored
            if (ignoreFields.contains(fieldName)) {
                continue;
            }

            if (!itemTypeFields.contains(fieldName)) {
                ignoreFields.add(fieldName);
            }
            if (ignoreEmpty) {
                field.setAccessible(true);
                try {
                    Object value = field.get(item.getData());
                    if (value == null || (value instanceof String && value.toString().isEmpty())
                            || (List.class.isAssignableFrom(value.getClass()) && ((List) value).isEmpty())) {
                        ignoreFields.add(fieldName);
                    }
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    logger.error("Could not access field to retrive ignore fields.", e);
                }
            }
        }

        return ignoreFields;
    }
    
    private List<String> getItemTypeFields(IUser user, ItemType itemType) {
        if (itemType.equals(ItemType.NOTE)) {
            return getNoteFieldList();
        }
        FieldInfo[] fieldInfos = zoteroConnector.getFields(user, itemType.getZoteroKey());
        List<String> itemTypeFields = new ArrayList<>();

        // add fields of item type
        for (FieldInfo info : fieldInfos) {
            itemTypeFields.add(info.getField());
        }

        return itemTypeFields;
    }

    private List<String> getNoteFieldList() {
        List<String> noteFields = new ArrayList<>();
        noteFields.add(ZoteroFields.ITEM_TYPE);
        noteFields.add(ZoteroFields.PARENT_ITEM);
        noteFields.add(ZoteroFields.TAGS);
        noteFields.add(ZoteroFields.NOTE);
        return noteFields;
    }

    @Override
    public List<String> getValidCreatorTypes(IUser user, ItemType itemType) {
        CreatorType[] creatorTypes = zoteroConnector.getItemTypeCreatorTypes(user, itemType.getZoteroKey());
        List<String> validTypes = new ArrayList<>();
        for (CreatorType type : creatorTypes) {
            validTypes.add(type.getCreatorType());
        }
        return validTypes;
    }

    @Override
    public Map<ItemDeletionResponse, List<String>> deleteMultipleItems(IUser user, String groupId, List<String> citationKeys, Long citationVersion)
            throws ZoteroConnectionException, ZoteroHttpStatusException {
        return zoteroConnector.deleteMultipleItems(user, groupId, citationKeys, citationVersion);
    }
}
