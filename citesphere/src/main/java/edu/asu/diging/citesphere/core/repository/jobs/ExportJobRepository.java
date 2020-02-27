package edu.asu.diging.citesphere.core.repository.jobs;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import edu.asu.diging.citesphere.core.model.jobs.impl.ExportJob;

public interface ExportJobRepository extends CrudRepository<ExportJob, String> {

    List<ExportJob> findByUsername(String username, Pageable pagable);
    
    List<ExportJob> findByTaskId(String taskId);
}
