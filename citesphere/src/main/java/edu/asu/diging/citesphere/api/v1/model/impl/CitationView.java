package edu.asu.diging.citesphere.api.v1.model.impl;

import java.util.Set;

import edu.asu.diging.citesphere.model.bib.IPerson;

public class CitationView {

    private Set<IPerson> authors;
    private String title;
    private String key;
    private String dateFreetext;
    private String url;
    public CitationView(Set<IPerson> authors, String title, String key, String dateFreetext, String url) {
        super();
        this.authors = authors;
        this.title = title;
        this.key = key;
        this.dateFreetext = dateFreetext;
        this.url = url;
    }
    public CitationView() {
        super();
    }
    public Set<IPerson> getAuthors() {
        return authors;
    }
    public void setAuthors(Set<IPerson> authors) {
        this.authors = authors;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public String getDateFreetext() {
        return dateFreetext;
    }
    public void setDateFreetext(String dateFreetext) {
        this.dateFreetext = dateFreetext;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    
    
    
}
