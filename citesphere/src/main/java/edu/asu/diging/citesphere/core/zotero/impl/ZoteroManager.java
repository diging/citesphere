package edu.asu.diging.citesphere.core.zotero.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.zotero.api.Group;
import org.springframework.social.zotero.api.Item;
import org.springframework.social.zotero.api.ZoteroResponse;
import org.springframework.stereotype.Service;

import edu.asu.diging.citesphere.core.factory.ICitationFactory;
import edu.asu.diging.citesphere.core.factory.IGroupFactory;
import edu.asu.diging.citesphere.core.model.IUser;
import edu.asu.diging.citesphere.core.model.bib.ICitation;
import edu.asu.diging.citesphere.core.model.bib.ICitationGroup;
import edu.asu.diging.citesphere.core.model.bib.impl.CitationResults;
import edu.asu.diging.citesphere.core.zotero.IZoteroConnector;
import edu.asu.diging.citesphere.core.zotero.IZoteroManager;

@Service
public class ZoteroManager implements IZoteroManager {
    
    @Autowired
    private IZoteroConnector zoteroConnector;
    
    @Autowired
    private ICitationFactory citationFactory;
    
    @Autowired
    private IGroupFactory groupFactory;
        
    public CitationResults getGroupItems(IUser user, String groupId, int page) {
        ZoteroResponse<Item> response = zoteroConnector.getGroupItems(user, groupId, page);
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
            ZoteroResponse<Item> groupItems = zoteroConnector.getGroupItemsWithLimit(user, group.getId() + "", 1);
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
    public ICitationGroup getGroup(IUser user, String groupId) {
        Group group = zoteroConnector.getGroup(user, groupId);
        ZoteroResponse<Item> groupItems = zoteroConnector.getGroupItemsWithLimit(user, group.getId() + "", 1);
        ICitationGroup citGroup = groupFactory.createGroup(group);
        citGroup.setNumItems(groupItems.getTotalResults());
        return citGroup;
    }
    
}
