package edu.asu.diging.citesphere.core.export.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import edu.asu.diging.citesphere.core.exceptions.ExportFailedException;
import edu.asu.diging.citesphere.core.exceptions.ExportTypeNotSupportedException;
import edu.asu.diging.citesphere.core.exceptions.FileStorageException;
import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
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
import edu.asu.diging.citesphere.model.bib.impl.CitationResults;
import edu.asu.diging.citesphere.user.IUser;

@Component
@Transactional
public class ExportProcessor implements IExportProcessor {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final String SORT_BY_TITLE = "title";
    
    private Map<ExportType, Processor> processors;
    
    @Value("${_zotero_page_size}")
    private Integer zoteroPageSize;
    
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
    public void runExport(ExportType exportType, IUser user, String groupId, String collectionId, IExportTask task, ExportFinishedCallback callback) throws GroupDoesNotExistException, ExportTypeNotSupportedException, ExportFailedException, ZoteroHttpStatusException {
        Processor processor = processors.get(exportType);
        if (processor == null) {
            throw new ExportTypeNotSupportedException("Export type " + exportType + " is not supported.");
        }
        
        CitationResults initialPage = initializeTask(user, groupId, collectionId, task);
        List<ICitation> citations = collectCitations(initialPage, user, groupId, collectionId, task);
        
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
        
        // -- export finished
        task.setStatus(ExportStatus.DONE);
        task.setFinishedOn(OffsetDateTime.now());
        task.setFilename(filename);
        taskRepo.saveAndFlush((ExportTask) task);
        callback.exportFinished(task.getId());
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
    
    private CitationResults initializeTask(IUser user, String groupId, String collectionId, IExportTask task) throws GroupDoesNotExistException, ZoteroHttpStatusException {
        CitationResults results = citationManager.getGroupItems(user, groupId, collectionId, 1, SORT_BY_TITLE);
        
        long total = results.getTotalResults();
        task.setTotalRecords(total);
        task.setStatus(ExportStatus.INITIALIZING);
        taskRepo.saveAndFlush((ExportTask) task);
        
        return results;
    }
    
    private List<ICitation> collectCitations(CitationResults initialPage, IUser user, String groupId, String collectionId, IExportTask task) throws GroupDoesNotExistException, ZoteroHttpStatusException {
        List<ICitation> citations = new ArrayList<>();
        
        long total = initialPage.getTotalResults();
        long pageTotal = total/zoteroPageSize;
        if (total%zoteroPageSize > 0) {
            pageTotal += 1;
        }
        
        citations.addAll(initialPage.getCitations());
        task.setProgress(citations.size());
        task.setStatus(ExportStatus.STARTED);
        taskRepo.saveAndFlush((ExportTask) task);
        int page = 1;
        while(page < pageTotal) {
            page += 1;
            initialPage = citationManager.getGroupItems(user, groupId, collectionId, page, SORT_BY_TITLE);
            citations.addAll(initialPage.getCitations());
            task.setProgress(citations.size());
            taskRepo.save((ExportTask) task);
            try {
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException e) {
                logger.error("Exception during sleep.", e);
            }
        }
        
        return citations;
    }
}
