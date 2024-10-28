package edu.asu.diging.citesphere.api.v1.user;

import java.util.HashMap;
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

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
    
    private Map<Class<?>, BiConsumer<ObjectNode, Object>> jobHandlers = new HashMap<>();

    public JobInfoController() {
        // Map each job class to its respective handling method
        jobHandlers.put(UploadJob.class, (node, job) -> handleUploadJob(node, (IUploadJob) job));
        jobHandlers.put(ExportJob.class, (node, job) -> handleExportJob(node, (IExportJob) job));
        jobHandlers.put(ImportCrossrefJob.class, (node, job) -> handleImportCrossrefJob(node, (IImportCrossrefJob) job));
    }
    
    @RequestMapping(value="/job/info")
    public ResponseEntity<String> getProfile(@RequestHeader HttpHeaders headers) throws JsonProcessingException {
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

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("zotero", zoteroToken.getToken());
        node.put("zoteroId", zoteroToken.getUserId());
        node.put("username", job.getUsername());
        BiConsumer<ObjectNode, Object> handler = jobHandlers.get(job.getClass());
        if (handler != null) {
            handler.accept(node, job);
        } else {
            logger.error("No handler found for job type: " + job.getClass());
            return new ResponseEntity<>("No handler found for job type: " + job.getClass(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(node.toString(), HttpStatus.OK);
    }

    private void handleUploadJob(ObjectNode node, IUploadJob job) {
        node.put("groupId", job.getCitationGroup());
    }

    private void handleExportJob(ObjectNode node, IExportJob job) {
        IExportTask exportTask = exportTaskManager.get(job.getTaskId());
        node.put("groupId", exportTask.getGroupId());
        node.put("collectionId", exportTask.getCollectionId());
        node.put("exportType", exportTask.getExportType().name());
        node.put("taskId", exportTask.getId());
    }

    @SuppressWarnings("deprecation")
    private void handleImportCrossrefJob(ObjectNode node, IImportCrossrefJob job) {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode arrayNode = mapper.valueToTree(job.getDois());
        node.put("dois", arrayNode);
        node.put("groupId", job.getCitationGroup());
    }
}
