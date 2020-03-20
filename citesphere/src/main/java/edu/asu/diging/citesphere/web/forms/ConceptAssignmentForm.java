package edu.asu.diging.citesphere.web.forms;

public class ConceptAssignmentForm {

    private String id;
    private String conceptId;
    private String conceptTypeId;

    private String conceptUri;
    private String conceptName;
    private String conceptTypeName;
    private String conceptTypeUri;

    public String getConceptTypeUri() {
        return conceptTypeUri;
    }

    public void setConceptTypeUri(String conceptTypeUri) {
        this.conceptTypeUri = conceptTypeUri;
    }

    public String getConceptUri() {
        return conceptUri;
    }

    public void setConceptUri(String conceptUri) {
        this.conceptUri = conceptUri;
    }

    public String getConceptName() {
        return conceptName;
    }

    public void setConceptName(String conceptName) {
        this.conceptName = conceptName;
    }

    public String getConceptTypeName() {
        return conceptTypeName;
    }

    public void setConceptTypeName(String conceptTypeName) {
        this.conceptTypeName = conceptTypeName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getConceptId() {
        return conceptId;
    }

    public void setConceptId(String conceptId) {
        this.conceptId = conceptId;
    }

    public String getConceptTypeId() {
        return conceptTypeId;
    }

    public void setConceptTypeId(String conceptTypeId) {
        this.conceptTypeId = conceptTypeId;
    }

}
