package edu.asu.diging.citesphere.core.authority.impl;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class ConceptpowerResponse {

    private List<ConceptpowerEntry> conceptEntries;

	public List<ConceptpowerEntry> getConceptEntries() {
		return conceptEntries;
	}

	public void setConceptEntries(List<ConceptpowerEntry> conceptEntries) {
		this.conceptEntries = conceptEntries;
	}
    
}
