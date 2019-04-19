package edu.asu.diging.citesphere.web.forms;

import edu.asu.diging.citesphere.core.model.bib.IConceptType;

public class CitationConceptForm {

    private String name;
    private String description;
    private String uri;
    private IConceptType type;
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getUri() {
        return uri;
    }
    public void setUri(String uri) {
        this.uri = uri;
    }
    public IConceptType getType() {
        return type;
    }
    public void setType(IConceptType type) {
        this.type = type;
    }
    
    
}
