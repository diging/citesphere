package edu.asu.diging.citesphere.api.v1.model.impl;

import java.util.List;

import edu.asu.diging.citesphere.api.v1.model.ICollectionResult;
import edu.asu.diging.citesphere.core.model.bib.ICitation;

public class CollectionResult implements ICollectionResult {

    private List<ICitation> items;
    private long total;
    private long totalPages;
    private int currentPage;
    private String zoteroGroupId;
    private String collectionId;
    
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
    public long getTotalPages() {
        return totalPages;
    }

    @Override
    public void setTotalPages(long totalPages) {
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
    public String getCollectionId() {
        return collectionId;
    }

    @Override
    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }

}
