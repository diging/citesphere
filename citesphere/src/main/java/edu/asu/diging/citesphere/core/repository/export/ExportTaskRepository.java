package edu.asu.diging.citesphere.core.repository.export;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import edu.asu.diging.citesphere.core.model.export.impl.ExportTask;

public interface ExportTaskRepository extends JpaRepository<ExportTask, String> {

    List<ExportTask> findByUsername(String username, Pageable pagable);
    
    int countByUsername(String username);
}
