package edu.asu.diging.citesphere.web.user;

import java.util.List;

import edu.asu.diging.citesphere.model.bib.ICitation;
import edu.asu.diging.citesphere.model.bib.ICitationCollection;
import edu.asu.diging.citesphere.model.bib.impl.CitationResults;

public class ItemsDataDto {
    private List<ICitation> citations;
    private long totalResults;
    private double totalPages;
    private int currentPage;
    private String zoteroGroupId;
    private CitationResults results;
    private List<ICitationCollection> citationCollections;
    private List<String> shownColumns;
    private List<String> allowedColumns;
    
    
    
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
    public CitationResults getResults() {
        return results;
    }
    public void setResults(CitationResults results) {
        this.results = results;
    }
    public List<ICitation> getCitations() {
        return citations;
    }
    public void setCitations(List<ICitation> citations) {
        this.citations = citations;
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
    
     
    
    
}
