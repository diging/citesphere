package edu.asu.diging.citesphere.core.model.authority.impl;

import java.time.OffsetDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import edu.asu.diging.citesphere.core.model.authority.IAuthorityEntry;

@Entity
public class AuthorityEntry implements IAuthorityEntry {

    @Id
    @GeneratedValue(generator = "authority_id_generator")
    @GenericGenerator(name = "authority_id_generator",    
                    parameters = @Parameter(name = "prefix", value = "AU"), 
                    strategy = "edu.asu.diging.citesphere.core.repository.IdGenerator"
            )
    private String id;
    private String name;
    private String uri;
    private String importerId;
    private String username;
    private OffsetDateTime createdOn;
    
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.authority.impl.IAuthorityEntry#getId()
     */
    @Override
    public String getId() {
        return id;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.authority.impl.IAuthorityEntry#setId(java.lang.String)
     */
    @Override
    public void setId(String id) {
        this.id = id;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.authority.impl.IAuthorityEntry#getName()
     */
    @Override
    public String getName() {
        return name;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.authority.impl.IAuthorityEntry#setName(java.lang.String)
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.authority.impl.IAuthorityEntry#getUri()
     */
    @Override
    public String getUri() {
        return uri;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.authority.impl.IAuthorityEntry#setUri(java.lang.String)
     */
    @Override
    public void setUri(String uri) {
        this.uri = uri;
    }
    @Override
    public String getImporterId() {
        return importerId;
    }
    @Override
    public void setImporterId(String importerId) {
        this.importerId = importerId;
    }
    @Override
    public String getUsername() {
        return username;
    }
    @Override
    public void setUsername(String username) {
        this.username = username;
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
