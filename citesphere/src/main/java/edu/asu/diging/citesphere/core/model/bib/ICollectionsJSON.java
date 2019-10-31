package edu.asu.diging.citesphere.core.model.bib;

import java.util.List;
import edu.asu.diging.citesphere.core.service.IGroupManager;

public class ICollectionsJSON {

    private List<ICitation> items;
    private long total;
    private double totalPages;
    private int currentPage;
    private String zoteroGroupId;
    private ICitationGroup group;
    private String collectionId;
    private List<ICitationCollection> citationCollections;
    
    public ICollectionsJSON(List<ICitation> items, long total, double d, Integer currentPage, String zoteroGroupId, 
            ICitationGroup iCitationGroup, String collectionId, List<ICitationCollection> citationCollections) {
        this.items = items;
        this.total = total;
        this.totalPages = d;
        this.currentPage = currentPage;
        this.zoteroGroupId = zoteroGroupId;
        this.group = iCitationGroup;
        this.collectionId = collectionId;
        this.citationCollections = citationCollections;
    }
}
