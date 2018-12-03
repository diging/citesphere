package edu.asu.diging.citesphere.core.zotero.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.zotero.api.Group;
import org.springframework.social.zotero.api.Item;
import org.springframework.social.zotero.api.ZoteroResponse;
import org.springframework.stereotype.Service;

import edu.asu.diging.citesphere.core.factory.ICitationFactory;
import edu.asu.diging.citesphere.core.model.IUser;
import edu.asu.diging.citesphere.core.model.bib.ICitation;
import edu.asu.diging.citesphere.core.model.bib.impl.CitationResults;
import edu.asu.diging.citesphere.core.zotero.IZoteroConnector;
import edu.asu.diging.citesphere.core.zotero.IZoteroManager;

@Service
public class ZoteroManager implements IZoteroManager {
    
    @Autowired
    private IZoteroConnector zoteroConnector;
    
    @Autowired
    private ICitationFactory citationFactory;
        
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
    public Group[] getGroups(IUser user) {
        return zoteroConnector.getGroups(user);
    }
    
    
}
