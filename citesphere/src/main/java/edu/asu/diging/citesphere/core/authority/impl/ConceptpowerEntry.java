package edu.asu.diging.citesphere.core.authority.impl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ConceptpowerEntry {

    private String id;
    private String concept_uri;
    private String lemma;
    private String description;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getConcept_uri() {
        return concept_uri;
    }

    public void setConcept_uri(String concept_uri) {
        this.concept_uri = concept_uri;
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
