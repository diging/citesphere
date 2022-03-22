package edu.asu.diging.citesphere.web.forms;

import javax.validation.constraints.NotEmpty;

public class ListAuthoritiesByName {
    private String description;
    @NotEmpty(message="Name cannot be empty.")
    private String name;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description= description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
}
}
