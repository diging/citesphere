package edu.asu.diging.citesphere.core.repository.export;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import edu.asu.diging.citesphere.core.model.export.impl.ExportTask;

public interface ExportTaskRepository extends PagingAndSortingRepository<ExportTask, String> {

    public List<ExportTask> findByUsername(String username, Pageable pagable);
}
