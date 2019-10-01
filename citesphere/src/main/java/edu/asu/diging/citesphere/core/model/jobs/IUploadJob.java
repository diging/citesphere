package edu.asu.diging.citesphere.core.model.jobs;

import java.util.List;

import edu.asu.diging.citesphere.core.model.jobs.impl.JobPhase;

public interface IUploadJob extends IJob {

    String getFilename();

    void setFilename(String filename);
    
    public JobStatus getStatus();

    public void setStatus(JobStatus status);
    
    public List<JobPhase> getPhases();

    public void setPhases(List<JobPhase> phases);

    void setContentType(String contentType);

    String getContentType();

    void setFileSize(long fileSize);

    long getFileSize();

    void setCitationGroup(String citationGroup);

    String getCitationGroup();

	String getCitationGroupName();

	void setCitationGroupName(String citationGroupName);

}