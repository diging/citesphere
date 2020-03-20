package edu.asu.diging.citesphere.core.authority.impl;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class ConceptpowerResponse {

    private List<ConceptpowerEntry> conceptEntries;
    
    private Pagination pagination;

	public List<ConceptpowerEntry> getConceptEntries() {
		return conceptEntries;
	}

	public void setConceptEntries(List<ConceptpowerEntry> conceptEntries) {
		this.conceptEntries = conceptEntries;
	}

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }
	

    
}
