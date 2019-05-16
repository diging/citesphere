package edu.asu.diging.citesphere.core.model.jobs.impl;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class Job {

    @Id
    @GeneratedValue(generator = "job_id_generator")
    @GenericGenerator(name = "job_id_generator",    
                    parameters = @Parameter(name = "prefix", value = "JOB"), 
                    strategy = "edu.asu.diging.citesphere.core.repository.IdGenerator"
            )
    private String id;
    
    private String username;
    

    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.jobs.impl.IUploadJob#getId()
     */
    public String getId() {
        return id;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.jobs.impl.IUploadJob#setId(java.lang.String)
     */
    
    public void setId(String id) {
        this.id = id;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.jobs.impl.IUploadJob#getUsername()
     */
    public String getUsername() {
        return username;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.jobs.impl.IUploadJob#setUsername(java.lang.String)
     */
    public void setUsername(String username) {
        this.username = username;
    }
}
