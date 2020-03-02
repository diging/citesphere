package edu.asu.diging.citesphere.core.export;

import edu.asu.diging.citesphere.core.model.jobs.IExportJob;
import edu.asu.diging.citesphere.core.model.jobs.JobStatus;

public interface IExportJobManager {

    IExportJob save(IExportJob job);

    IExportJob createJob(JobStatus status, String taskId, String username);

    IExportJob getExportJob(String id);

    IExportJob findByTaskId(String taskId);

}