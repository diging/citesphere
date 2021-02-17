package edu.asu.diging.citesphere.web.forms;

import javax.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.URL;

public class CitationConceptForm {

    @NotEmpty(message = "Name cannot be empty.")
    private String name;
    
    private String description;

    @NotEmpty(message = "Uri cannot be empty.")
    @URL
    private String uri;
    
    private String type;
    
    private String conceptId;
    
    private String owner;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getConceptId() {
        return conceptId;
    }

    public void setConceptId(String conceptId) {
    	this.conceptId = conceptId;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
    
}
