package edu.asu.diging.citesphere.core.authority.impl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class IsiscbEntry {

    private String id;
    private String name;
    private String authority_type;
    private String description;
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getAuthority_type() {
        return authority_type;
    }
    public void setAuthority_type(String authority_type) {
        this.authority_type = authority_type;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

}
