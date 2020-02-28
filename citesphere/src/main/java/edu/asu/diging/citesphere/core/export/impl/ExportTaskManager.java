package edu.asu.diging.citesphere.core.export.impl;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import edu.asu.diging.citesphere.core.export.ExportType;
import edu.asu.diging.citesphere.core.export.IExportTaskManager;
import edu.asu.diging.citesphere.core.model.export.ExportStatus;
import edu.asu.diging.citesphere.core.model.export.IExportTask;
import edu.asu.diging.citesphere.core.model.export.impl.ExportTask;
import edu.asu.diging.citesphere.core.model.jobs.IExportJob;
import edu.asu.diging.citesphere.core.repository.export.ExportTaskRepository;
import edu.asu.diging.citesphere.user.IUser;

@Service
@PropertySource("classpath:config.properties")
public class ExportTaskManager implements IExportTaskManager {
    
    @Value("${_tasks_page_size}")
    private Integer tasksPageSize;
    
    @Autowired
    private ExportTaskRepository taskRepo;

    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.export.impl.IExportTaskManager#createExportTask(edu.asu.diging.citesphere.core.export.ExportType, java.lang.String, edu.asu.diging.citesphere.core.model.export.ExportStatus)
     */
    @Override
    public IExportTask createExportTask(ExportType exportType, String username, ExportStatus status) {
        IExportTask task = new ExportTask();
        task.setExportType(exportType);
        task.setUsername(username);
        task.setStatus(status);
        task.setCreatedOn(OffsetDateTime.now());
        return task;
    }
    
    @Override
    public IExportTask saveAndFlush(IExportTask task) {
        return taskRepo.saveAndFlush((ExportTask) task);
    }
    
    @Override
    public IExportTask save(IExportTask task) {
        return taskRepo.save((ExportTask)task);
    }
    
    @Override
    public IExportTask get(String id) {
        Optional<ExportTask> optional = taskRepo.findById(id);
        if (optional.isPresent()) {
            return optional.get();
        }
        return null;
    }
    
    @Override
    public List<IExportTask> getTasks(IUser user, int page) {
        List<IExportTask> tasks = new ArrayList<>();
        taskRepo.findByUsername(user.getUsername(),
                PageRequest.of(page, tasksPageSize, Sort.by(Direction.DESC, "createdOn", "id")))
                .forEach(t -> tasks.add(t));
        return tasks;
    }
    
    @Override
    public int getTasksTotal(IUser user) {
        return taskRepo.countByUsername(user.getUsername());
    }

    @Override
    public int getTasksTotalPages(IUser user) {
        int totalTasks = getTasksTotal(user);
        int pagesTotal = totalTasks / tasksPageSize;
        if (totalTasks % tasksPageSize > 0) {
            pagesTotal += 1;
        }
        return pagesTotal;
    }
    
    @Override
    public void updateTask(IExportJob job) {
        IExportTask task = get(job.getTaskId());
        switch (job.getStatus()) {
        case DONE:
            task.setStatus(ExportStatus.DONE);
            break;
        case FAILURE:
            task.setStatus(ExportStatus.FAILED);
            break;
        case PREPARED:
            task.setStatus(ExportStatus.PENDING);
            break;
        case STARTED:
            task.setStatus(ExportStatus.STARTED);
        }
        taskRepo.save((ExportTask)task);
    }
}
