package edu.asu.diging.citesphere.core.search.model.impl;

import java.util.Set;

import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

public class Person {

    @Field(type=FieldType.Text)
    private String name;
    @Field(type=FieldType.Keyword)
    private String uri;
    @Field(type=FieldType.Keyword)
    private String localAuthorityId;
    @Field(type=FieldType.Keyword)
    private String firstName;
    @Field(type=FieldType.Keyword)
    private String lastName;
    @Field(type=FieldType.Keyword)
    private String role;
    private int positionInList;
    
    private Set<Affiliation> affiliations;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Set<Affiliation> getAffiliations() {
        return affiliations;
    }

    public void setAffiliations(Set<Affiliation> affiliations) {
        this.affiliations = affiliations;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getPositionInList() {
        return positionInList;
    }

    public void setPositionInList(int positionInList) {
        this.positionInList = positionInList;
    }
    
}
