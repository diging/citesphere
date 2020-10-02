package edu.asu.diging.citesphere.core.export.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.data.util.CloseableIterator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import edu.asu.diging.citesphere.core.exceptions.AccessForbiddenException;
import edu.asu.diging.citesphere.core.exceptions.ExportFailedException;
import edu.asu.diging.citesphere.core.exceptions.ExportTypeNotSupportedException;
import edu.asu.diging.citesphere.core.exceptions.FileStorageException;
import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.exceptions.SyncInProgressException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroHttpStatusException;
import edu.asu.diging.citesphere.core.export.ExportFinishedCallback;
import edu.asu.diging.citesphere.core.export.ExportType;
import edu.asu.diging.citesphere.core.export.IExportProcessor;
import edu.asu.diging.citesphere.core.export.proc.Processor;
import edu.asu.diging.citesphere.core.model.export.ExportStatus;
import edu.asu.diging.citesphere.core.model.export.IExportTask;
import edu.asu.diging.citesphere.core.model.export.impl.ExportTask;
import edu.asu.diging.citesphere.core.repository.export.ExportTaskRepository;
import edu.asu.diging.citesphere.core.service.ICitationManager;
import edu.asu.diging.citesphere.core.service.upload.IFileStorageManager;
import edu.asu.diging.citesphere.model.bib.ICitation;
import edu.asu.diging.citesphere.user.IUser;

@Component
@Transactional
public class ExportProcessor implements IExportProcessor {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final String SORT_BY_TITLE = "title";
    
    private Map<ExportType, Processor> processors;
    
    @Value("${_zotero_page_size}")
    private Integer zoteroPageSize;
    
    @Value("${_max_export_tries}")
    private Integer maxExportTries;
    
    @Autowired
    private ICitationManager citationManager;
    
    @Autowired
    private ExportTaskRepository taskRepo;
    
    @Autowired
    private ApplicationContext ctx;
    
    @Autowired
    private IFileStorageManager storageManager;
    
    @PostConstruct
    public void init() {
        processors = new HashMap<>();
        Map<String, Processor> beans = ctx.getBeansOfType(Processor.class);
        for (Processor bean : beans.values()) {
            processors.put(bean.getSupportedType(), bean);
        }
    }
    

    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.export.impl.IExportProcessor#runExport(edu.asu.diging.citesphere.core.export.ExportType, edu.asu.diging.citesphere.core.model.IUser, java.lang.String, edu.asu.diging.citesphere.core.model.export.IExportTask)
     */
    @Override
    @Async
    public void runExport(ExportType exportType, IUser user, String groupId, String collectionId, IExportTask task, ExportFinishedCallback callback) throws ExportTypeNotSupportedException, ExportFailedException {
        Processor processor = processors.get(exportType);
        if (processor == null) {
            throw new ExportTypeNotSupportedException("Export type " + exportType + " is not supported.");
        }
        
        CloseableIterator<ICitation> citations = null;
        try {
            int counter = 0;
            while(true) {
                try {
                    citations = citationManager.getAllGroupItems(user, groupId, collectionId);
                    // done syncing
                    break;
                } catch (SyncInProgressException e) {
                    setTaskStatus(task, ExportStatus.SYNCING);
                    // still syncing, let's sleep and try again
                    try {
                        TimeUnit.SECONDS.sleep(5);
                        counter++;
                    } catch (InterruptedException e1) {
                        logger.error("Could not sleep.");
                    }
                }
                // if we waiting long enough, let's cancel the export
                if (counter >= maxExportTries) {
                    logger.warn("Syncing took too long. Cancelling export " + task.getId());
                    break;
                }
            }
        } catch (AccessForbiddenException e1) {
            logger.error("Unauthorized access of " + user + " on group " + groupId, e1);
            setTaskStatus(task, ExportStatus.FAILED);
            return;
        } catch (ZoteroHttpStatusException e) {
            logger.error("There was a problem when connecting to Zotero.", e);
            setTaskStatus(task, ExportStatus.FAILED);
            return;
        } catch (GroupDoesNotExistException e) {
            logger.error("Group " + groupId + " does not exist.", e);
            setTaskStatus(task, ExportStatus.FAILED);
            return;
        } 
        
        if (citations == null) {
            setTaskStatus(task, ExportStatus.FAILED);
            return;
        }
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd--HH-mm-ss");
        String time = LocalDateTime.now().format(formatter);
        String filename = "export-" + time + "." + processor.getFileExtension();
       
        BufferedWriter writer;
        try {
            writer = createWriter(user, task, processor, filename);
        } catch (IOException e) {
            throw new ExportFailedException("Could not create file.", e);
        }
        processor.write(citations, writer);
        citations.close();
        
        // -- export finished
        task.setStatus(ExportStatus.DONE);
        task.setFinishedOn(OffsetDateTime.now());
        task.setFilename(filename);
        taskRepo.saveAndFlush((ExportTask) task);
        callback.exportFinished(task.getId());
    }


    private void setTaskStatus(IExportTask task, ExportStatus status) {
        task.setStatus(status);
        task.setFinishedOn(OffsetDateTime.now());
        taskRepo.saveAndFlush((ExportTask) task);
    }


    private BufferedWriter createWriter(IUser user, IExportTask task, Processor processor, String filename)
            throws ExportFailedException, IOException {
         try {
            storageManager.saveFile(user.getUsername(), task.getId(), filename, new byte[0]);
        } catch (FileStorageException e) {
            throw new ExportFailedException("Could not create export file.", e);
        }
        
        String filePath = storageManager.getFolderPath(user.getUsername(), task.getId());
        filePath += File.separator + filename;
        
        BufferedWriter writer = Files.newBufferedWriter(Paths.get(filePath));
        return writer;
    }
    

}
