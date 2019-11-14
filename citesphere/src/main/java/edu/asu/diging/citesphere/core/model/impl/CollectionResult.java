package edu.asu.diging.citesphere.core.model.impl;

import java.util.List;
import edu.asu.diging.citesphere.core.model.ICollectionResult;
import edu.asu.diging.citesphere.core.model.bib.ICitation;
import edu.asu.diging.citesphere.core.model.bib.ICitationCollection;
import edu.asu.diging.citesphere.core.model.bib.ICitationGroup;

public class CollectionResult implements ICollectionResult {

    public CollectionResult() {}
    
    @Override
    public List<ICitation> getItems() {
        return items;
    }
    
    @Override
    public void setItems(List<ICitation> items) {
        this.items = items;
    }
    
    @Override
    public long getTotal() {
        return total;
    }


    @Override
    public void setTotal(long total) {
        this.total = total;
    }

    @Override
    public double getTotalPages() {
        return totalPages;
    }

    @Override
    public void setTotalPages(double totalPages) {
        this.totalPages = totalPages;
    }

    @Override
    public int getCurrentPage() {
        return currentPage;
    }

    @Override
    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    @Override
    public String getZoteroGroupId() {
        return zoteroGroupId;
    }

    @Override
    public void setZoteroGroupId(String zoteroGroupId) {
        this.zoteroGroupId = zoteroGroupId;
    }

    @Override
    public ICitationGroup getGroup() {
        return group;
    }

    @Override
    public void setGroup(ICitationGroup group) {
        this.group = group;
    }

    @Override
    public String getCollectionId() {
        return collectionId;
    }

    @Override
    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }

    @Override
    public List<ICitationCollection> getCitationCollections() {
        return citationCollections;
    }

    @Override
    public void setCitationCollections(List<ICitationCollection> list) {
        this.citationCollections = list;
    }
    private List<ICitation> items;
    private long total;
    private double totalPages;
    private int currentPage;
    private String zoteroGroupId;
    private ICitationGroup group;
    private String collectionId;
    private List<ICitationCollection> citationCollections;
}
