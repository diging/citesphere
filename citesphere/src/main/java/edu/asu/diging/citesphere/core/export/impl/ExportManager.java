package edu.asu.diging.citesphere.core.export.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import edu.asu.diging.citesphere.core.exceptions.AccessForbiddenException;
import edu.asu.diging.citesphere.core.exceptions.DownloadExportException;
import edu.asu.diging.citesphere.core.exceptions.ExportFailedException;
import edu.asu.diging.citesphere.core.exceptions.ExportTooBigException;
import edu.asu.diging.citesphere.core.exceptions.ExportTypeNotSupportedException;
import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.exceptions.MessageCreationException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroHttpStatusException;
import edu.asu.diging.citesphere.core.export.ExportFinishedCallback;
import edu.asu.diging.citesphere.core.export.ExportType;
import edu.asu.diging.citesphere.core.export.IExportJobManager;
import edu.asu.diging.citesphere.core.export.IExportManager;
import edu.asu.diging.citesphere.core.export.IExportProcessor;
import edu.asu.diging.citesphere.core.export.IExportTaskManager;
import edu.asu.diging.citesphere.core.kafka.IKafkaRequestProducer;
import edu.asu.diging.citesphere.core.model.export.ExportStatus;
import edu.asu.diging.citesphere.core.model.export.IExportTask;
import edu.asu.diging.citesphere.core.model.export.impl.ExportTask;
import edu.asu.diging.citesphere.core.model.jobs.IExportJob;
import edu.asu.diging.citesphere.core.model.jobs.JobStatus;
import edu.asu.diging.citesphere.core.model.jobs.impl.JobPhase;
import edu.asu.diging.citesphere.core.service.ICitationCollectionManager;
import edu.asu.diging.citesphere.core.service.ICitationManager;
import edu.asu.diging.citesphere.core.service.IGroupManager;
import edu.asu.diging.citesphere.core.service.jwt.IJwtTokenService;
import edu.asu.diging.citesphere.messages.KafkaTopics;
import edu.asu.diging.citesphere.messages.model.KafkaJobMessage;
import edu.asu.diging.citesphere.model.bib.ICitationCollection;
import edu.asu.diging.citesphere.model.bib.ICitationGroup;
import edu.asu.diging.citesphere.model.bib.impl.CitationResults;
import edu.asu.diging.citesphere.user.IUser;

@Service
@Transactional
@PropertySource("classpath:config.properties")
public class ExportManager implements IExportManager, ExportFinishedCallback {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final String SORT_BY_TITLE = "title";

    private ConcurrentHashMap<String, IExportTask> runningTasks;

    @Value("${_max_export_size}")
    private Integer maxExportSize;

    @Value("${_citesphere_exporter_base_url}")
    private String exporterBaseUrl;

    @Value("${_citesphere_exporter_download_path}")
    private String exporterDownloadPath;

    @Value("${_citesphere_exporter_api_token}")
    private String citesphereExporterToken;

    @Value("${_distributed_exporter_download_path}")
    private String exportDownloadFolder;

    @Autowired
    private IExportTaskManager taskManager;

    @Autowired
    private IExportProcessor processor;

    @Autowired
    private ICitationManager citationManager;

    @Autowired
    private IGroupManager groupManager;

    @Autowired
    private ICitationCollectionManager collectionManager;

    @Autowired
    private IExportJobManager jobManager;

    @Autowired
    private IKafkaRequestProducer kafkaProducer;

    @Autowired
    private IJwtTokenService tokenService;

    @PostConstruct
    public void init() {
        runningTasks = new ConcurrentHashMap<>();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.diging.citesphere.core.export.impl.IExportManager#runExport(edu.asu.
     * diging.citesphere.core.export.ExportType,
     * edu.asu.diging.citesphere.core.model.IUser, java.lang.String)
     */
    @Override
    public void export(ExportType exportType, IUser user, String groupId, String collectionId)
            throws GroupDoesNotExistException, ExportTypeNotSupportedException, ExportFailedException,
            ExportTooBigException, ZoteroHttpStatusException, AccessForbiddenException {

        ICitationGroup group = groupManager.getGroup(user, groupId);
        if (group == null) {
            throw new GroupDoesNotExistException("Group does not exist.");
        }

        // FIXME: sort field should not be hard coded!
        CitationResults results = citationManager.getGroupItems(user, groupId, collectionId, 1, "title");
        if (results.getTotalResults() > maxExportSize) {
            throw new ExportTooBigException("Can't export " + results.getTotalResults() + " records.");
        }

        ICitationCollection collection = null;
        if (collectionId != null && !collectionId.trim().isEmpty()) {
            collection = collectionManager.getCollection(user, groupId, collectionId);
        }

        IExportTask task = taskManager.createExportTask(exportType, user.getUsername(), ExportStatus.PENDING);
        task.setGroupId(groupId);
        task.setGroupName(group.getName());
        if (collection != null) {
            task.setCollectionId(collectionId);
            task.setCollectionName(collection.getName());
        }

        task = taskManager.saveAndFlush(task);
        runningTasks.put(task.getId(), task);

        processor.runExport(exportType, user, groupId, collectionId, task, this);
    }

    @Override
    public void distributedExport(ExportType exportType, IUser user, String groupId, String collectionId)
            throws GroupDoesNotExistException, ExportTypeNotSupportedException, ExportFailedException,
            ExportTooBigException, ZoteroHttpStatusException, AccessForbiddenException {

        ICitationGroup group = groupManager.getGroup(user, groupId);
        if (group == null) {
            throw new GroupDoesNotExistException("Group does not exist.");
        }

        ICitationCollection collection = null;
        if (collectionId != null && !collectionId.trim().isEmpty()) {
            collection = collectionManager.getCollection(user, groupId, collectionId);
        }

        IExportTask task = new ExportTask();
        task.setExportType(exportType);
        task.setUsername(user.getUsername());
        task.setStatus(ExportStatus.PENDING);
        task.setCreatedOn(OffsetDateTime.now());
        task.setGroupId(groupId);
        task.setGroupName(group.getName());
        if (collection != null) {
            task.setCollectionId(collectionId);
            task.setCollectionName(collection.getName());
        }

        CitationResults results = citationManager.getGroupItems(user, groupId, collectionId, 1, SORT_BY_TITLE);
        task.setTotalRecords(results.getTotalResults());

        task = taskManager.saveAndFlush(task);
        runningTasks.put(task.getId(), task);

        IExportJob job = jobManager.createJob(JobStatus.PREPARED, task.getId(), user.getUsername());
        job = jobManager.save(job);

        sendJobMessage(job);
    }

    @Override
    public void retryExport(IExportJob job) {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            logger.error("Could not sleep.", e);
        }
        sendJobMessage(job);
    }

    /**
     * 
     * @param task
     * @return
     * @throws DownloadExportException
     */
    /*
     * Adapted from:
     * https://www.codejava.net/java-se/networking/use-httpurlconnection-to-download
     * -file-from-an-http-url
     */
    @Override
    public String getDistributedExportResult(IExportTask task) throws DownloadExportException {
        String downloadUrl = exporterBaseUrl + exporterDownloadPath.replace("{0}", task.getId());
        URL url;
        try {
            url = new URL(downloadUrl);
        } catch (MalformedURLException e1) {
            throw new DownloadExportException(e1);
        }
        HttpURLConnection httpConn;
        int responseCode;
        try {
            httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setRequestProperty("Authorization", citesphereExporterToken);
            responseCode = httpConn.getResponseCode();
        } catch (IOException e1) {
            throw new DownloadExportException(e1);
        }

        if (responseCode == HttpURLConnection.HTTP_OK) {
            String fileName = "";
            String disposition = httpConn.getHeaderField("Content-Disposition");

            if (disposition != null) {
                // extracts file name from header field
                int index = disposition.indexOf("filename=");
                if (index > -1) {
                    fileName = disposition.substring(index + 10, disposition.length() - 1);
                }
            }

            if (fileName.trim().isEmpty()) {
                fileName = task.getId() + "_" + task.getCreatedOn() + "." + task.getExportType().getFileExtension();
            }

            InputStream inputStream;
            try {
                inputStream = httpConn.getInputStream();
            } catch (IOException e) {
                throw new DownloadExportException(e);
            }
            String saveFilePath = exportDownloadFolder + File.separator + fileName;
            FileOutputStream outputStream;
            try {
                outputStream = new FileOutputStream(saveFilePath);
            } catch (FileNotFoundException e) {
                throw new DownloadExportException(e);
            }

            int bytesRead = -1;
            byte[] buffer = new byte[1024];
            try {
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            } catch (IOException e) {
                throw new DownloadExportException(e);
            } finally {
                try {
                    outputStream.close();
                    inputStream.close();
                } catch (IOException e) {
                    // we assume that everything else worked and we have the file
                    logger.error("Something went wrong closing streams.", e);
                }
            }
            httpConn.disconnect();
            return saveFilePath;
        } else {
            httpConn.disconnect();
            throw new DownloadExportException("Could not download file. HTTP response code: " + responseCode);
        }
    }

    @Override
    public void exportFinished(String taskId) {
        runningTasks.remove(taskId);
    }

    @PreDestroy
    public void shutdown() {
        logger.info("Failing " + runningTasks.size()
                + " unfinished tasks (number of actually failed tasks might be lower).");
        for (String taskId : runningTasks.keySet()) {
            IExportTask task = taskManager.get(taskId);
            if (task != null) {
                if (Arrays.asList(ExportStatus.PENDING, ExportStatus.INITIALIZING, ExportStatus.STARTED)
                        .contains(task.getStatus())) {
                    task.setStatus(ExportStatus.FAILED);
                    taskManager.save(task);
                }
            }
        }
    }

    private void sendJobMessage(IExportJob job) {
        String token = tokenService.generateJobApiToken(job);
        try {
            kafkaProducer.sendRequest(new KafkaJobMessage(token), KafkaTopics.REFERENCES_EXPORT_TOPIC);
        } catch (MessageCreationException e) {
            logger.error("Could not send Kafka message.", e);
            job.setStatus(JobStatus.FAILURE);
            job.getPhases().add(new JobPhase(JobStatus.FAILURE, e.getMessage()));
            jobManager.save(job);
        }
    }

}
