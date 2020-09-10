package edu.asu.diging.citesphere.core.zotero.impl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.springframework.social.zotero.api.ZoteroFields;
import org.springframework.social.zotero.api.ZoteroResponse;
import org.springframework.social.zotero.exception.ZoteroConnectionException;
import org.springframework.stereotype.Service;

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
        return createCitationResults(response);
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
    public Map<String, Long> getGroupItemVersions(IUser user, String groupId, long version) {
        ZoteroResponse<Item> response = zoteroConnector.getGroupItemVersions(user, groupId, version);
        Map<String, Long> itemVersions = new HashMap<>();
        for (Item item : response.getResults()) {
            itemVersions.put(item.getKey(), item.getVersion());            
        }
        return itemVersions;
    }

    @Override
    public Map<Long, Long> getGroupsVersion(IUser user) {
        ZoteroResponse<Group> response = zoteroConnector.getGroupsVersions(user);
        Map<Long, Long> groupVersions = new HashMap<>();
        for (Group group : response.getResults()) {
            groupVersions.put(group.getId(), group.getVersion());
        }
        return groupVersions;
    }

    @Override
    public ICitation getGroupItem(IUser user, String groupId, String itemKey) throws ZoteroHttpStatusException {
        Item item = zoteroConnector.getItem(user, groupId, itemKey);
        return citationFactory.createCitation(item);
    }
    
    @Override
    public ZoteroGroupItemsResponse getGroupItemsByKey(IUser user, String groupId, List<String> itemKeys) {
        ZoteroResponse<Item> response = zoteroConnector.getGroupItemsByKey(user, groupId, itemKeys);
        
        ZoteroGroupItemsResponse zoteroReponse = new ZoteroGroupItemsResponse();
        zoteroReponse.setContentVersion(response.getLastVersion());
        zoteroReponse.setCitations(new ArrayList<>());
        for(Item item : response.getResults()) {
            zoteroReponse.getCitations().add(citationFactory.createCitation(item));
        }
        
        return zoteroReponse;
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
        return zoteroConnector.isGroupModified(user, groupId, lastGroupVersion)
;    }

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
        return createCitationResults(response);
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

    private CitationResults createCitationResults(ZoteroResponse<Item> response) {
        List<ICitation> citations = new ArrayList<>();
        if (response.getResults() != null) {
            for (Item item : response.getResults()) {
                citations.add(citationFactory.createCitation(item));
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
            throws ZoteroConnectionException, ZoteroHttpStatusException {
        Item item = itemFactory.createItem(citation, new ArrayList<>());

        List<String> itemTypeFields = getItemTypeFields(user, citation.getItemType());
        // add fields that need to be submitted
        itemTypeFields.add(ZoteroFields.VERSION);
        itemTypeFields.add(ZoteroFields.ITEM_TYPE);
        itemTypeFields.add(ZoteroFields.CREATOR);

        List<String> ignoreFields = createIgnoreFields(itemTypeFields, item, false);
        ignoreFields.add("collections");

        List<String> validCreatorTypes = getValidCreatorTypes(user, citation.getItemType());

        Item updatedItem = zoteroConnector.updateItem(user, item, groupId, new ArrayList<>(), ignoreFields,
                validCreatorTypes);
        return citationFactory.createCitation(updatedItem);
    }

    @Override
    public ICitation createCitation(IUser user, String groupId, List<String> collectionIds, ICitation citation)
            throws ZoteroConnectionException, ZoteroItemCreationFailedException, ZoteroHttpStatusException {
        Item item = itemFactory.createItem(citation, collectionIds);

        List<String> itemTypeFields = getItemTypeFields(user, citation.getItemType());
        itemTypeFields.add(ZoteroFields.ITEM_TYPE);
        itemTypeFields.add(ZoteroFields.CREATOR);
        itemTypeFields.add(ZoteroFields.COLLECTIONS);

        List<String> ignoreFields = createIgnoreFields(itemTypeFields, item, true);
        List<String> validCreatorTypes = getValidCreatorTypes(user, citation.getItemType());

        Item newItem = zoteroConnector.createItem(user, item, groupId, collectionIds, ignoreFields, validCreatorTypes);
        return citationFactory.createCitation(newItem);
    }

    private List<String> createIgnoreFields(List<String> itemTypeFields, Item item, boolean ignoreEmpty) {
        List<String> ignoreFields = new ArrayList<>();
        // general fields to ignore for the moment
        ignoreFields.add("tags");
        ignoreFields.add("relations");
        ignoreFields.add("parentItem");
        ignoreFields.add("linkMode");
        ignoreFields.add("note");
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
        FieldInfo[] fieldInfos = zoteroConnector.getFields(user, itemType.getZoteroKey());
        List<String> itemTypeFields = new ArrayList<>();

        // add fields of item type
        for (FieldInfo info : fieldInfos) {
            itemTypeFields.add(info.getField());
        }

        return itemTypeFields;
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
}
