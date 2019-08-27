package edu.asu.diging.citesphere.core.repository.jobs;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import edu.asu.diging.citesphere.core.model.jobs.impl.UploadJob;

public interface UploadJobRepository extends CrudRepository<UploadJob, String> {

    List<UploadJob> findByUsername(String username, Pageable pagable);
}
