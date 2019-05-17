package edu.asu.diging.citesphere.core.model.jobs;

import java.util.List;

import edu.asu.diging.citesphere.core.model.jobs.impl.JobPhase;

public interface IUploadJob {

    String getId();

    void setId(String id);

    String getUsername();

    void setUsername(String username);

    String getFilename();

    void setFilename(String filename);
    
    public JobStatus getStatus();

    public void setStatus(JobStatus status);
    
    public List<JobPhase> getPhases();

    public void setPhases(List<JobPhase> phases);

}