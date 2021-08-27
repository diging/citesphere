package edu.asu.diging.citesphere.web.forms;

import javax.validation.constraints.NotEmpty;

public class CreateAuthorityForm {
    
    private String description;
    
    @NotEmpty(message = "Name cannot be empty.")
    private String name;

    private String groupId;
    
    private String uri;

    private String importerId;
    
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getImporterId() {
        return importerId;
    }

    public void setImporterId(String importerId) {
        this.importerId = importerId;
    }
}
