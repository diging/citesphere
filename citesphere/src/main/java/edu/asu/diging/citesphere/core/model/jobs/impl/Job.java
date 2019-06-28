package edu.asu.diging.citesphere.core.model.jobs.impl;

import java.time.OffsetDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.fasterxml.jackson.annotation.JsonIgnore;

import edu.asu.diging.citesphere.core.model.jobs.IJob;
import edu.asu.diging.citesphere.core.model.jobs.JobStatus;

@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class Job implements IJob {

    @Id
    @GeneratedValue(generator = "job_id_generator")
    @GenericGenerator(name = "job_id_generator",    
                    parameters = @Parameter(name = "prefix", value = "JOB"), 
                    strategy = "edu.asu.diging.citesphere.core.repository.IdGenerator"
            )
    private String id;
    
    private String username;
    
    @Enumerated(EnumType.STRING)
    private JobStatus status;
    
    private OffsetDateTime createdOn;
    
    @JsonIgnore
    @OneToMany(cascade=CascadeType.ALL)
    private List<JobPhase> phases;
    

    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.jobs.impl.IUploadJob#getId()
     */
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.jobs.impl.IJob#getId()
     */
    @Override
    public String getId() {
        return id;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.jobs.impl.IUploadJob#setId(java.lang.String)
     */
    
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.jobs.impl.IJob#setId(java.lang.String)
     */
    @Override
    public void setId(String id) {
        this.id = id;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.jobs.impl.IUploadJob#getUsername()
     */
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.jobs.impl.IJob#getUsername()
     */
    @Override
    public String getUsername() {
        return username;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.jobs.impl.IUploadJob#setUsername(java.lang.String)
     */
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.jobs.impl.IJob#setUsername(java.lang.String)
     */
    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.jobs.impl.IJob#getStatus()
     */
    @Override
    public JobStatus getStatus() {
        return status;
    }

    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.jobs.impl.IJob#setStatus(edu.asu.diging.citesphere.core.model.jobs.JobStatus)
     */
    @Override
    public void setStatus(JobStatus status) {
        this.status = status;
    }

    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.jobs.impl.IJob#getPhases()
     */
    @Override
    public List<JobPhase> getPhases() {
        return phases;
    }

    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.jobs.impl.IJob#setPhases(java.util.List)
     */
    @Override
    public void setPhases(List<JobPhase> phases) {
        this.phases = phases;
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
