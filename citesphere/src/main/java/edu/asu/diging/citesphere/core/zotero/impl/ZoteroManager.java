package edu.asu.diging.citesphere.core.zotero.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.zotero.api.Group;
import org.springframework.social.zotero.api.Item;
import org.springframework.stereotype.Service;

import edu.asu.diging.citesphere.core.factory.ICitationFactory;
import edu.asu.diging.citesphere.core.model.IUser;
import edu.asu.diging.citesphere.core.model.bib.ICitation;
import edu.asu.diging.citesphere.core.zotero.IZoteroConnector;
import edu.asu.diging.citesphere.core.zotero.IZoteroManager;

@Service
public class ZoteroManager implements IZoteroManager {
    
    @Autowired
    private IZoteroConnector zoteroConnector;
    
    @Autowired
    private ICitationFactory citationFactory;
        
    public List<ICitation> getGroupItems(IUser user, String groupId, int page) {
        Item[] items = zoteroConnector.getGroupItems(user, groupId, page);
        List<ICitation> citations = new ArrayList<>();
        for (Item item : items) {
            citations.add(citationFactory.createCitation(item));
        }
        return citations;
    }

    @Override
    public Group[] getGroups(IUser user) {
        return zoteroConnector.getGroups(user);
    }
    
    
}
