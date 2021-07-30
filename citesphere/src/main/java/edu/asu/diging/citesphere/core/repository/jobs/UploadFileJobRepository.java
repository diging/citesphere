package edu.asu.diging.citesphere.core.repository.jobs;

import org.springframework.data.repository.CrudRepository;

import edu.asu.diging.citesphere.core.model.jobs.impl.UploadFileJob;

public interface UploadFileJobRepository extends CrudRepository<UploadFileJob, String> {

}
