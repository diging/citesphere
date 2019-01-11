package edu.asu.diging.citesphere.core.model.bib.impl;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.fasterxml.jackson.annotation.JsonIgnore;

import edu.asu.diging.citesphere.core.model.bib.IAffiliation;

@Entity
public class Affiliation implements IAffiliation {

    @Id
    @GeneratedValue(generator = "affiliation_id_generator")
    @GenericGenerator(name = "affiliation_id_generator",    
                    parameters = @Parameter(name = "prefix", value = "AF"), 
                    strategy = "edu.asu.diging.citesphere.core.repository.IdGenerator"
            )
    @JsonIgnore
    private String id;
    private String name;
    private String uri;
     
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.IAffiliation#getId()
     */
    @Override
    public String getId() {
        return id;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.IAffiliation#setId(java.lang.String)
     */
    @Override
    public void setId(String id) {
        this.id = id;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.IAffiliation#getName()
     */
    @Override
    public String getName() {
        return name;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.IAffiliation#setName(java.lang.String)
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.IAffiliation#getUri()
     */
    @Override
    public String getUri() {
        return uri;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.IAffiliation#setUri(java.lang.String)
     */
    @Override
    public void setUri(String uri) {
        this.uri = uri;
    }
}
