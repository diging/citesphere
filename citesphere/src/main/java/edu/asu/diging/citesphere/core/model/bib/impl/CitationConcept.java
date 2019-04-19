package edu.asu.diging.citesphere.core.model.bib.impl;

import java.time.OffsetDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import edu.asu.diging.citesphere.core.model.IUser;
import edu.asu.diging.citesphere.core.model.bib.ICitationConcept;
import edu.asu.diging.citesphere.core.model.bib.IConceptType;
import edu.asu.diging.citesphere.core.model.impl.User;

@Entity
public class CitationConcept implements ICitationConcept {

    @Id
    @GeneratedValue(generator = "concept_id_generator")
    @GenericGenerator(name = "concept_id_generator", parameters = @Parameter(name = "prefix", value = "CON"), strategy = "edu.asu.diging.citesphere.core.repository.IdGenerator")
    private String id;
    private String name;
    private String description;
    private String uri;

    @OneToOne(targetEntity = ConceptType.class)
    private IConceptType type;
    
    @ManyToOne(targetEntity=User.class)
    private IUser owner;
    
    private OffsetDateTime createdOn;
    

    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.IConcept#getId()
     */
    @Override
    public String getId() {
        return id;
    }

    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.IConcept#setId(java.lang.String)
     */
    @Override
    public void setId(String id) {
        this.id = id;
    }

    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.IConcept#getName()
     */
    @Override
    public String getName() {
        return name;
    }

    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.IConcept#setName(java.lang.String)
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.IConcept#getDescription()
     */
    @Override
    public String getDescription() {
        return description;
    }

    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.IConcept#setDescription(java.lang.String)
     */
    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.IConcept#getUri()
     */
    @Override
    public String getUri() {
        return uri;
    }

    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.IConcept#setUri(java.lang.String)
     */
    @Override
    public void setUri(String uri) {
        this.uri = uri;
    }

    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.IConcept#getType()
     */
    @Override
    public IConceptType getType() {
        return type;
    }

    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.IConcept#setType(edu.asu.diging.citesphere.core.model.bib.impl.IConceptType)
     */
    @Override
    public void setType(IConceptType type) {
        this.type = type;
    }

    @Override
    public IUser getOwner() {
        return owner;
    }

    @Override
    public void setOwner(IUser owner) {
        this.owner = owner;
    }

    @Override
    public OffsetDateTime getCreatedOn() {
        return createdOn;
    }

    @Override
    public void setCreatedOn(OffsetDateTime createdOn) {
        this.createdOn = createdOn;
    }
}
