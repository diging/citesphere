package edu.asu.diging.citesphere.core.repository.jobs;

import org.springframework.data.repository.CrudRepository;

import edu.asu.diging.citesphere.core.model.jobs.impl.UploadCollectionJob;

public interface UploadCollectionJobRepository extends CrudRepository<UploadCollectionJob, String> {

}
