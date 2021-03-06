package edu.asu.diging.citesphere.web.forms;

import java.util.List;

public class PersonForm {

    private String id;
    private String firstName;
    private String lastName;
    private String uri;
    private String localAuthorityId;
    private int position;
    private String role;
    
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
    
    private List<AffiliationForm> affiliations;
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public String getUri() {
        return uri;
    }
    public void setUri(String uri) {
        this.uri = uri;
    }
    public String getLocalAuthorityId() {
        return localAuthorityId;
    }
    public void setLocalAuthorityId(String localAuthorityId) {
        this.localAuthorityId = localAuthorityId;
    }
    public int getPosition() {
        return position;
    }
    public void setPosition(int position) {
        this.position = position;
    }
    public List<AffiliationForm> getAffiliations() {
        return affiliations;
    }
    public void setAffiliations(List<AffiliationForm> affiliations) {
        this.affiliations = affiliations;
    }    
}
