package edu.asu.diging.citesphere.core.repository.jobs;

import org.springframework.data.repository.CrudRepository;

import edu.asu.diging.citesphere.core.model.jobs.impl.ImportCrossrefJob;

public interface ImportCrossrefJobRepository extends CrudRepository<ImportCrossrefJob, String> {

}
