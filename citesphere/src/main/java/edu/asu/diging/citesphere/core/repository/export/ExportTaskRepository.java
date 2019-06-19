package edu.asu.diging.citesphere.core.repository.export;

import org.springframework.data.repository.PagingAndSortingRepository;

import edu.asu.diging.citesphere.core.model.export.impl.ExportTask;

public interface ExportTaskRepository extends PagingAndSortingRepository<ExportTask, String> {

}
