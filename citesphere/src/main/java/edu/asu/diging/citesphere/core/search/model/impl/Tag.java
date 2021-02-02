package edu.asu.diging.citesphere.core.search.model.impl;

import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

public class Tag {

    @Field(type=FieldType.Text)
    private String conceptName;
    @Field(type=FieldType.Keyword)
    private String conceptUri;
    @Field(type=FieldType.Keyword)
    private String localConceptId;
    @Field(type=FieldType.Text)
    private String typeName;
    @Field(type=FieldType.Keyword)
    private String typeUri;
    @Field(type=FieldType.Keyword)
    private String localConceptTypeId;
    
    public String getConceptName() {
        return conceptName;
    }
    public void setConceptName(String conceptName) {
        this.conceptName = conceptName;
    }
    public String getConceptUri() {
        return conceptUri;
    }
    public void setConceptUri(String conceptUri) {
        this.conceptUri = conceptUri;
    }
    public String getLocalConceptId() {
        return localConceptId;
    }
    public void setLocalConceptId(String localConceptId) {
        this.localConceptId = localConceptId;
    }
    public String getTypeName() {
        return typeName;
    }
    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
    public String getTypeUri() {
        return typeUri;
    }
    public void setTypeUri(String typeUri) {
        this.typeUri = typeUri;
    }
    public String getLocalConceptTypeId() {
        return localConceptTypeId;
    }
    public void setLocalConceptTypeId(String localConceptTypeId) {
        this.localConceptTypeId = localConceptTypeId;
    }
    
}
