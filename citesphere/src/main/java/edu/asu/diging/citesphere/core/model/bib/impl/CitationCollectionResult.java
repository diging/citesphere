package edu.asu.diging.citesphere.core.model.bib.impl;

import java.util.List;

import edu.asu.diging.citesphere.core.model.bib.ICitationCollection;

public class CitationCollectionResult {

    private long totalResults;
    private List<ICitationCollection> citationCollections;
    private boolean notModified;
    
    public long getTotalResults() {
        return totalResults;
    }
    public void setTotalResults(long totalResults) {
        this.totalResults = totalResults;
    }
    public List<ICitationCollection> getCitationCollections() {
        return citationCollections;
    }
    public void setCitationCollections(List<ICitationCollection> citationCollections) {
        this.citationCollections = citationCollections;
    }
    public boolean isNotModified() {
        return notModified;
    }
    public void setNotModified(boolean notModified) {
        this.notModified = notModified;
    }
    
}
