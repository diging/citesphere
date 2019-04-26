package edu.asu.diging.citesphere.core.model.bib.impl;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import edu.asu.diging.citesphere.core.model.bib.ICitationConceptTag;

@Entity
public class CitationConceptTag implements ICitationConceptTag {

    @Id
    @GeneratedValue(generator = "concepttag_id_generator")
    @GenericGenerator(name = "concepttag_id_generator", parameters = @Parameter(name = "prefix", value = "CPTT"), strategy = "edu.asu.diging.citesphere.core.repository.IdGenerator")
    private String id;
    private String conceptName;
    private String conceptUri;
    private String localConceptId;
    private String typeName;
    private String typeUri;
    private String localConceptTypeId;


    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.ICitationConceptTag#getId()
     */
    @Override
    public String getId() {
        return id;
    }

    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.ICitationConceptTag#setId(java.lang.String)
     */
    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getConceptName() {
        return conceptName;
    }

    @Override
    public void setConceptName(String conceptName) {
        this.conceptName = conceptName;
    }

    @Override
    public String getConceptUri() {
        return conceptUri;
    }

    @Override
    public void setConceptUri(String conceptUri) {
        this.conceptUri = conceptUri;
    }

    @Override
    public String getLocalConceptId() {
        return localConceptId;
    }

    @Override
    public void setLocalConceptId(String localConceptId) {
        this.localConceptId = localConceptId;
    }

    @Override
    public String getTypeName() {
        return typeName;
    }

    @Override
    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    @Override
    public String getTypeUri() {
        return typeUri;
    }

    @Override
    public void setTypeUri(String typeUri) {
        this.typeUri = typeUri;
    }

    @Override
    public String getLocalConceptTypeId() {
        return localConceptTypeId;
    }

    @Override
    public void setLocalConceptTypeId(String localConceptTypeId) {
        this.localConceptTypeId = localConceptTypeId;
    }
    
}
