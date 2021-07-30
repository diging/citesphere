package edu.asu.diging.citesphere.web.forms;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

public class CreateAuthorityForm {
    private String description;
    
    @NotEmpty(message = "Name cannot be empty.")
    private String name;

    @Pattern(regexp="[0-9]*", message="Group Id can only contain numeric value.")
    private String groupId;
    
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
}
