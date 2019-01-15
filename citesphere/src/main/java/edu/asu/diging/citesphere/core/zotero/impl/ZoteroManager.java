package edu.asu.diging.citesphere.core.zotero.impl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.zotero.api.FieldInfo;
import org.springframework.social.zotero.api.Group;
import org.springframework.social.zotero.api.Item;
import org.springframework.social.zotero.api.ZoteroResponse;
import org.springframework.social.zotero.exception.ZoteroConnectionException;
import org.springframework.stereotype.Service;

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
import edu.asu.diging.citesphere.core.zotero.ZoteroFields;

@Service
public class ZoteroManager implements IZoteroManager {
    
    @Autowired
    private IZoteroConnector zoteroConnector;
    
    @Autowired
    private ICitationFactory citationFactory;
    
    @Autowired
    private IGroupFactory groupFactory;
    
    @Autowired
    private IItemFactory itemFactory;
        
    public CitationResults getGroupItems(IUser user, String groupId, int page, String sortBy) {
        ZoteroResponse<Item> response = zoteroConnector.getGroupItems(user, groupId, page, sortBy);
        List<ICitation> citations = new ArrayList<>();
        for (Item item : response.getResults()) {
            citations.add(citationFactory.createCitation(item));
        }
        CitationResults results = new CitationResults();
        results.setCitations(citations);
        results.setTotalResults(response.getTotalResults());
        return results;
    }

    @Override
    public List<ICitationGroup> getGroups(IUser user) {
        ZoteroResponse<Group> response = zoteroConnector.getGroups(user);
        List<ICitationGroup> groups = new ArrayList<>();
        for (Group group : response.getResults()) {
            ICitationGroup citGroup = groupFactory.createGroup(group);
            ZoteroResponse<Item> groupItems = zoteroConnector.getGroupItemsWithLimit(user, group.getId() + "", 1, null);
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
        ZoteroResponse<Item> groupItems = zoteroConnector.getGroupItemsWithLimit(user, group.getId() + "", 1, null);
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
        
        FieldInfo[] fieldInfos = zoteroConnector.getFields(user, citation.getItemType().getZoteroKey());
        List<String> itemTypeFields = new ArrayList<>();
        // add fields that need to be submitted
        itemTypeFields.add(ZoteroFields.VERSION);
        itemTypeFields.add(ZoteroFields.ITEM_TYPE);
        
        // add fields of item type
        for (FieldInfo info : fieldInfos) {
            itemTypeFields.add(info.getField());
        }
        
        Field[] fields = item.getData().getClass().getDeclaredFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            if (!itemTypeFields.contains(fieldName) && !ignoreFields.contains(fieldName)) {
                ignoreFields.add(fieldName);
            }
        }
        
        Item updatedItem = zoteroConnector.updateItem(user, item, groupId, ignoreFields);
        return citationFactory.createCitation(updatedItem);
    }
    
}
