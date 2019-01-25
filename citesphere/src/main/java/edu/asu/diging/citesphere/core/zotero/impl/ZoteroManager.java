package edu.asu.diging.citesphere.core.zotero.impl;

import java.lang.reflect.Field;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.zotero.api.CreatorType;
import org.springframework.social.zotero.api.Data;
import org.springframework.social.zotero.api.FieldInfo;
import org.springframework.social.zotero.api.Group;
import org.springframework.social.zotero.api.Item;
import org.springframework.social.zotero.api.ZoteroFields;
import org.springframework.social.zotero.api.ZoteroResponse;
import org.springframework.social.zotero.exception.ZoteroConnectionException;
import org.springframework.stereotype.Service;

import edu.asu.diging.citesphere.core.exceptions.ZoteroItemCreationFailedException;
import edu.asu.diging.citesphere.core.factory.ICitationFactory;
import edu.asu.diging.citesphere.core.factory.IGroupFactory;
import edu.asu.diging.citesphere.core.factory.zotero.IItemFactory;
import edu.asu.diging.citesphere.core.model.IUser;
import edu.asu.diging.citesphere.core.model.bib.ICitation;
import edu.asu.diging.citesphere.core.model.bib.ICitationGroup;
import edu.asu.diging.citesphere.core.model.bib.ItemType;
import edu.asu.diging.citesphere.core.model.bib.impl.BibField;
import edu.asu.diging.citesphere.core.model.bib.impl.CitationResults;
import edu.asu.diging.citesphere.core.zotero.IZoteroConnector;
import edu.asu.diging.citesphere.core.zotero.IZoteroManager;

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
        
    public CitationResults getGroupItems(IUser user, String groupId, int page, String sortBy, Long lastGroupVersion) {
        ZoteroResponse<Item> response = zoteroConnector.getGroupItems(user, groupId, page, sortBy, lastGroupVersion);
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
    public List<ICitationGroup> getGroups(IUser user) {
        ZoteroResponse<Group> response = zoteroConnector.getGroups(user);
        List<ICitationGroup> groups = new ArrayList<>();
        for (Group group : response.getResults()) {
            ICitationGroup citGroup = groupFactory.createGroup(group);
            ZoteroResponse<Item> groupItems = zoteroConnector.getGroupItemsWithLimit(user, group.getId() + "", 1, null, null);
            citGroup.setNumItems(groupItems.getTotalResults());
            groups.add(citGroup);
        }
        return groups;
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
    public ICitation getGroupItem(IUser user, String groupId, String itemKey) {
        Item item = zoteroConnector.getItem(user, groupId, itemKey);
        return citationFactory.createCitation(item);
    }
    
    @Override
    public long getGroupItemVersion(IUser user, String groupId, String itemKey) {
        return zoteroConnector.getItemVersion(user, groupId, itemKey);
    }
    
    @Override
    public ICitationGroup getGroup(IUser user, String groupId, boolean forceRefresh) {
        Group group = zoteroConnector.getGroup(user, groupId, forceRefresh);
        ZoteroResponse<Item> groupItems = zoteroConnector.getGroupItemsWithLimit(user, group.getId() + "", 1, null, null);
        ICitationGroup citGroup = groupFactory.createGroup(group);
        citGroup.setNumItems(groupItems.getTotalResults());
        return citGroup;
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
    public ICitation updateCitation(IUser user, String groupId, ICitation citation) throws ZoteroConnectionException {
        Item item = itemFactory.createItem(citation);
        System.out.println(item.getData().getCreators().size());
        List<String> itemTypeFields = getItemTypeFields(user, citation.getItemType());
        // add fields that need to be submitted
        itemTypeFields.add(ZoteroFields.VERSION);
        itemTypeFields.add(ZoteroFields.ITEM_TYPE);
        itemTypeFields.add(ZoteroFields.CREATOR);
        
        List<String> ignoreFields = createIgnoreFields(itemTypeFields, item, false);
        List<String> validCreatorTypes = getValidCreatorTypes(user, citation.getItemType());
        
        Item updatedItem = zoteroConnector.updateItem(user, item, groupId, ignoreFields, validCreatorTypes);
        return citationFactory.createCitation(updatedItem);
    }
    
    @Override
    public ICitation createCitation(IUser user, String groupId, ICitation citation) throws ZoteroConnectionException, ZoteroItemCreationFailedException {
        Item item = itemFactory.createItem(citation);
        
        List<String> itemTypeFields = getItemTypeFields(user, citation.getItemType());
        itemTypeFields.add(ZoteroFields.ITEM_TYPE);
        itemTypeFields.add(ZoteroFields.CREATOR);
        
        List<String> ignoreFields = createIgnoreFields(itemTypeFields, item, true);
        List<String> validCreatorTypes = getValidCreatorTypes(user, citation.getItemType());
        
        Item newItem = zoteroConnector.createItem(user, item, groupId, ignoreFields, validCreatorTypes);
        return citationFactory.createCitation(newItem);
    }
    
    private List<String> createIgnoreFields(List<String> itemTypeFields, Item item, boolean ignoreEmpty) {
        List<String> ignoreFields = new ArrayList<>();
        // general fields to ignore for the moment
        ignoreFields.add("tags");
        ignoreFields.add("collections");
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
                    if (value == null || (value instanceof String && value.toString().isEmpty())) {
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
    
    private List<String> getValidCreatorTypes(IUser user, ItemType itemType) {
        CreatorType[] creatorTypes = zoteroConnector.getItemTypeCreatorTypes(user, itemType.getZoteroKey());
        List<String> validTypes = new ArrayList<>();
        for (CreatorType type : creatorTypes) {
            validTypes.add(type.getCreatorType());
        }
        return validTypes;
    }
}
