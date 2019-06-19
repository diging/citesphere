package edu.asu.diging.citesphere.core.export.impl;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.asu.diging.citesphere.core.exceptions.ExportFailedException;
import edu.asu.diging.citesphere.core.exceptions.ExportTypeNotSupportedException;
import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.export.ExportFinishedCallback;
import edu.asu.diging.citesphere.core.export.ExportType;
import edu.asu.diging.citesphere.core.export.IExportManager;
import edu.asu.diging.citesphere.core.export.IExportProcessor;
import edu.asu.diging.citesphere.core.model.IUser;
import edu.asu.diging.citesphere.core.model.export.ExportStatus;
import edu.asu.diging.citesphere.core.model.export.IExportTask;
import edu.asu.diging.citesphere.core.model.export.impl.ExportTask;
import edu.asu.diging.citesphere.core.repository.export.ExportTaskRepository;

@Service
@Transactional
public class ExportManager implements IExportManager, ExportFinishedCallback {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    private ConcurrentHashMap<String, IExportTask> runningTasks; 
    
    @Autowired
    private ExportTaskRepository taskRepo;
    
    @Autowired
    private IExportProcessor processor;
    
    @PostConstruct
    public void init() {
        runningTasks = new ConcurrentHashMap<>();
    }

    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.export.impl.IExportManager#runExport(edu.asu.diging.citesphere.core.export.ExportType, edu.asu.diging.citesphere.core.model.IUser, java.lang.String)
     */
    @Override
    public void export(ExportType exportType, IUser user, String groupId) throws GroupDoesNotExistException, ExportTypeNotSupportedException, ExportFailedException {
        IExportTask task = new ExportTask();
        task.setExportType(exportType);
        task.setUsername(user.getUsername());
        task.setStatus(ExportStatus.PENDING);;
        task = taskRepo.save((ExportTask) task);
        runningTasks.put(task.getId(), task);
        
        processor.runExport(exportType, user, groupId, task, this);
    }

    @Override
    public void exportFinished(String taskId) {
        runningTasks.remove(taskId);
    }
    
    @PreDestroy
    public void shutdown() {
        logger.info("Failing " + runningTasks.size() + " unfinished tasks (number of actually failed tasks might be lower).");
        for (String taskId : runningTasks.keySet()) {
            Optional<ExportTask> taskOptional = taskRepo.findById(taskId);
            if (taskOptional.isPresent()) {
                ExportTask task = taskOptional.get();
                if (Arrays.asList(ExportStatus.PENDING, ExportStatus.INITIALIZING, ExportStatus.STARTED).contains(task.getStatus())) {
                    task.setStatus(ExportStatus.FAILED);
                    taskRepo.save((ExportTask) task);
                }
            }
        }
    }
}
