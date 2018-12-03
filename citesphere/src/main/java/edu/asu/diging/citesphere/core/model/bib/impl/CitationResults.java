package edu.asu.diging.citesphere.core.model.bib.impl;

import java.util.List;

import edu.asu.diging.citesphere.core.model.bib.ICitation;

public class CitationResults {

    private long totalResults;
    private List<ICitation> citations;
    
    public long getTotalResults() {
        return totalResults;
    }
    public void setTotalResults(long totalResults) {
        this.totalResults = totalResults;
    }
    public List<ICitation> getCitations() {
        return citations;
    }
    public void setCitations(List<ICitation> citations) {
        this.citations = citations;
    }
    
    
}
