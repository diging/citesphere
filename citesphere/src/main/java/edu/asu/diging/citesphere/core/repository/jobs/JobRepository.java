package edu.asu.diging.citesphere.core.repository.jobs;

import org.springframework.data.repository.PagingAndSortingRepository;

import edu.asu.diging.citesphere.core.model.jobs.impl.Job;

public interface JobRepository extends PagingAndSortingRepository<Job, String> {

}
