package edu.asu.diging.citesphere.core.model.jobs.impl;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import edu.asu.diging.citesphere.core.model.jobs.JobStatus;

@Entity
public class JobPhase {

    @Id
    @GeneratedValue(generator = "phase_id_generator")
    @GenericGenerator(name = "phase_id_generator",    
                    parameters = @Parameter(name = "prefix", value = "PH"), 
                    strategy = "edu.asu.diging.citesphere.core.repository.IdGenerator"
            )
    private String id;
   
    @Enumerated(EnumType.STRING)
    private JobStatus status;
   
    @Lob
    private String message;
    
    public JobPhase() {}

    public JobPhase(JobStatus status, String message) {
        super();
        this.status = status;
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public JobStatus getStatus() {
        return status;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    
}
