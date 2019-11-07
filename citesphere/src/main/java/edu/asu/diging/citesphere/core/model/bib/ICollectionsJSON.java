package edu.asu.diging.citesphere.core.model.bib;

import java.util.List;

public class ICollectionsJSON {

    public ICollectionsJSON() {}
    public List<ICitation> getItems() {
        return items;
    }

    public void setItems(List<ICitation> items) {
        this.items = items;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public double getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(double totalPages) {
        this.totalPages = totalPages;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public String getZoteroGroupId() {
        return zoteroGroupId;
    }

    public void setZoteroGroupId(String zoteroGroupId) {
        this.zoteroGroupId = zoteroGroupId;
    }

    public ICitationGroup getGroup() {
        return group;
    }

    public void setGroup(ICitationGroup group) {
        this.group = group;
    }

    public String getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }

    public List<ICitationCollection> getCitationCollections() {
        return citationCollections;
    }

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
