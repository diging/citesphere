package edu.asu.diging.citesphere.core.search.service.impl;

import java.util.List;

import edu.asu.diging.citesphere.model.bib.ICitation;

public class ResultPage {

    private List<ICitation> results;
    private long totalResults;
    private int totalPages;   
    
    public ResultPage(List<ICitation> results, long totalResults, int totalPages) {
        super();
        this.results = results;
        this.totalResults = totalResults;
        this.totalPages = totalPages;
    }
    
    public List<ICitation> getResults() {
        return results;
    }
    public void setResults(List<ICitation> results) {
        this.results = results;
    }
    public long getTotalResults() {
        return totalResults;
    }
    public void setTotalResults(long totalResults) {
        this.totalResults = totalResults;
    }
    public int getTotalPages() {
        return totalPages;
    }
    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
    
}
