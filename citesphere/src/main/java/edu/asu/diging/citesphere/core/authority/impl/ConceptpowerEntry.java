package edu.asu.diging.citesphere.core.authority.impl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class ConceptpowerEntry {
	
	private String id;
	private String equal_to;
	private String lemma;
	private String description;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getEqual_to() {
		return equal_to;
	}
	public void setEqual_to(String equal_to) {
		this.equal_to = equal_to;
	}
	public String getLemma() {
		return lemma;
	}
	public void setLemma(String lemma) {
		this.lemma = lemma;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	

}
