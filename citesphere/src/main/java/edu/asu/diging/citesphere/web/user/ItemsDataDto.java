package edu.asu.diging.citesphere.web.user;

import java.util.List;

import edu.asu.diging.citesphere.model.bib.ICitation;
import edu.asu.diging.citesphere.model.bib.ICitationCollection;
import edu.asu.diging.citesphere.model.bib.ICitationGroup;
import edu.asu.diging.citesphere.model.bib.impl.CitationResults;

public class ItemsDataDto {
    private List<CitationsDto> citationsData;
    private long totalResults;
    private double totalPages;
    private int currentPage;
    private String zoteroGroupId;
    private List<ICitationCollection> citationCollections;
    private List<String> shownColumns;
    private List<String> allowedColumns;
    private boolean notModified;
    private String sort;
    private String collectionId;
    private ICitationGroup group;
    
    public List<String> getAllowedColumns() {
        return allowedColumns;
    }
    public void setAllowedColumns(List<String> allowedColumns) {
        this.allowedColumns = allowedColumns;
    }
    public List<String> getShownColumns() {
        return shownColumns;
    }
    public void setShownColumns(List<String> shownColumns) {
        this.shownColumns = shownColumns;
    }
    public List<ICitationCollection> getCitationCollections() {
        return citationCollections;
    }
    public void setCitationCollections(List<ICitationCollection> citationCollections) {
        this.citationCollections = citationCollections;
    }
    public List<CitationsDto> getCitationsData() {
        return citationsData;
    }
    public void setCitationsData(List<CitationsDto> citationsData) {
        this.citationsData = citationsData;
    }
    public long getTotalResults() {
        return totalResults;
    }
    public void setTotalResults(long totalResults) {
        this.totalResults = totalResults;
    }
    public double getTotalPages() {
        return totalPages;
    }
    public void setTotalPages(double totalPages) {
        this.totalPages = totalPages;
    }
    public double getCurrentPage() {
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
    public boolean getNotModified() {
        return notModified;
    }
    public void setNotModified(boolean notModified) {
        this.notModified = notModified;
    }
    public void setSort(String sort) {
       this.sort = sort;
    }   
    public String getSort() {
        return sort;
    }
    public String getCollectionId() {
        return collectionId;
    }
    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }
    public ICitationGroup getGroup() {
        return group;
    }
    public void setGroup(ICitationGroup group) {
        this.group = group;
    }
}