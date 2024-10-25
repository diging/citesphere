package edu.asu.diging.citesphere.api.v1.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.asu.diging.citesphere.core.export.IExportTaskManager;
import edu.asu.diging.citesphere.core.model.IZoteroToken;
import edu.asu.diging.citesphere.core.model.export.IExportTask;
import edu.asu.diging.citesphere.core.model.jobs.IExportJob;
import edu.asu.diging.citesphere.core.model.jobs.IImportCrossrefJob;
import edu.asu.diging.citesphere.core.model.jobs.IJob;
import edu.asu.diging.citesphere.core.model.jobs.IUploadJob;
import edu.asu.diging.citesphere.core.model.jobs.impl.ExportJob;
import edu.asu.diging.citesphere.core.model.jobs.impl.ImportCrossrefJob;
import edu.asu.diging.citesphere.core.model.jobs.impl.UploadJob;
import edu.asu.diging.citesphere.core.service.jobs.IUploadJobManager;
import edu.asu.diging.citesphere.core.service.jwt.IJobApiTokenContents;
import edu.asu.diging.citesphere.core.user.IUserManager;
import edu.asu.diging.citesphere.core.zotero.impl.ZoteroTokenManager;

@Controller
public class JobInfoController extends BaseJobInfoController {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private IUserManager userManager;
    
    @Autowired
    private ZoteroTokenManager tokenManager;
    
    @Autowired
    private IUploadJobManager jobManager;
    
    @Autowired
    private IExportTaskManager exportTaskManager; 
    
    private Map<Class<?>, BiConsumer<Map<String, Object>, Object>> jobHandlers = new HashMap<>();

    public JobInfoController() {
        // Map each job class to its respective handling method
        jobHandlers.put(UploadJob.class, (response, job) -> handleUploadJob(response, (IUploadJob) job));
        jobHandlers.put(ExportJob.class, (response, job) -> handleExportJob(response, (IExportJob) job));
        jobHandlers.put(ImportCrossrefJob.class, (response, job) -> handleImportCrossrefJob(response, (IImportCrossrefJob) job));
    }
    
    @RequestMapping(value="/job/info")
    public ResponseEntity<String> getProfile(@RequestHeader HttpHeaders headers) {
        ResponseEntity<String> entity = checkForToken(headers);
        if (entity != null) {
            return entity;
        }
        
        IJobApiTokenContents tokenContents = getTokenContents(headers);
        entity = checkTokenValidity(tokenContents);
        if (entity != null) {
            return entity;
        }
                
        IJob job = jobManager.findJob(tokenContents.getJobId());
        IZoteroToken zoteroToken = tokenManager.getToken(userManager.findByUsername(job.getUsername()));
        
        Map<String, Object> response = new HashMap<>();
        response.put("zotero", zoteroToken.getToken());
        response.put("zoteroId", zoteroToken.getUserId());
        response.put("username", job.getUsername());
        BiConsumer<Map<String, Object>, Object> handler = jobHandlers.get(job.getClass());
        if (handler != null) {
            handler.accept(response, job);
        } else {
            logger.error("No handler found for job type: " + job.getClass());
            return new ResponseEntity<>("No handler found for job type: " + job.getClass(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
        return new ResponseEntity<>(response.toString(), HttpStatus.OK);
    }
    
    private void handleUploadJob(Map<String, Object> response, IUploadJob job) {
        response.put("groupId", job.getCitationGroup());
    }

    private void handleExportJob(Map<String, Object> response, IExportJob job) {
        IExportTask exportTask = exportTaskManager.get(job.getTaskId());
        response.put("groupId", exportTask.getGroupId());
        response.put("collectionId", exportTask.getCollectionId());
        response.put("exportType", exportTask.getExportType().name());
        response.put("taskId", exportTask.getId());
    }

    private void handleImportCrossrefJob(Map<String, Object> response, IImportCrossrefJob job) {
        List<String> doisList = new ArrayList<>(job.getDois());
        response.put("dois", doisList);
        response.put("groupId", job.getCitationGroup());
    }
}
