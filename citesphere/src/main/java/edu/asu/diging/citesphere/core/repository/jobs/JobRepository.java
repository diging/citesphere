package edu.asu.diging.citesphere.core.repository.jobs;

import org.springframework.data.repository.CrudRepository;

import edu.asu.diging.citesphere.core.model.jobs.impl.Job;

public interface JobRepository extends CrudRepository<Job, String> {

}
