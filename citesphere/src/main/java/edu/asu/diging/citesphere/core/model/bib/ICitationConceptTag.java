package edu.asu.diging.citesphere.core.model.bib;

public interface ICitationConceptTag {
    void setLocalConceptTypeId(String localConceptTypeId);

    String getLocalConceptTypeId();

    void setTypeUri(String typeUri);

    String getTypeUri();

    void setTypeName(String typeName);

    String getTypeName();

    void setLocalConceptId(String localConceptId);

    String getLocalConceptId();

    void setConceptUri(String conceptUri);

    String getConceptUri();

    void setConceptName(String conceptName);

    String getConceptName();

    String getId();

    void setId(String id);

}