package edu.asu.diging.citesphere.core.model.jobs;

import java.time.OffsetDateTime;
import java.util.List;

import edu.asu.diging.citesphere.core.model.jobs.impl.JobPhase;

public interface IJob {

    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.jobs.impl.IUploadJob#getId()
     */
    String getId();
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.jobs.impl.IUploadJob#setId(java.lang.String)
     */

    void setId(String id);

    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.jobs.impl.IUploadJob#getUsername()
     */
    String getUsername();

    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.jobs.impl.IUploadJob#setUsername(java.lang.String)
     */
    void setUsername(String username);

    JobStatus getStatus();

    void setStatus(JobStatus status);

    List<JobPhase> getPhases();

    void setPhases(List<JobPhase> phases);

    void setCreatedOn(OffsetDateTime createdOn);

    OffsetDateTime getCreatedOn();
    
    void setThreadId(long threadId);
    
    long getThreadId();
}