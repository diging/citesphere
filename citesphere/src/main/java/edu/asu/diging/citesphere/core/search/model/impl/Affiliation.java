package edu.asu.diging.citesphere.core.search.model.impl;

import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

public class Affiliation {

    @Field(type=FieldType.Text)
    private String name;
    @Field(type=FieldType.Keyword)
    private String uri;
    @Field(type=FieldType.Keyword)
    private String localAuthorityId;
    
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
    
}
