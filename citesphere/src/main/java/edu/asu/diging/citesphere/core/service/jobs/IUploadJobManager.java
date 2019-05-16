package edu.asu.diging.citesphere.core.service.jobs;

import edu.asu.diging.citesphere.core.model.jobs.IUploadJob;

public interface IUploadJobManager {

    IUploadJob findUploadJob(String id);

}