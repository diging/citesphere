package edu.asu.diging.citesphere.api.v1.user;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.asu.diging.citesphere.core.model.IZoteroToken;
import edu.asu.diging.citesphere.core.model.export.IExportTask;
import edu.asu.diging.citesphere.core.model.export.impl.ExportTask;
import edu.asu.diging.citesphere.core.model.jobs.IExportJob;
import edu.asu.diging.citesphere.core.model.jobs.impl.ExportJob;
import edu.asu.diging.citesphere.core.repository.export.ExportTaskRepository;
import edu.asu.diging.citesphere.core.repository.jobs.ExportJobRepository;
import edu.asu.diging.citesphere.core.service.jwt.IJobApiTokenContents;
import edu.asu.diging.citesphere.core.user.IUserManager;
import edu.asu.diging.citesphere.core.zotero.impl.ZoteroTokenManager;

public class ExportJobInfoController extends BaseJobInfoController {
    
    @Autowired
    private IUserManager userManager;
    
    @Autowired
    private ZoteroTokenManager tokenManager;
    
    @Autowired
    private ExportJobRepository exportJobRepository;
    
    @Autowired
    private ExportTaskRepository taskRepo;

    @RequestMapping(value="/job/export/info")
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
                
        Optional<ExportJob> jobOptional = exportJobRepository.findById(tokenContents.getJobId());
        if (!jobOptional.isPresent()) {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode node = mapper.createObjectNode();
            node.put("message", "Job id is invalid.");
            return new ResponseEntity<String>(node.toString(), HttpStatus.NOT_FOUND);
        }
        
        IExportJob job = jobOptional.get();
        IZoteroToken zoteroToken = tokenManager.getToken(userManager.findByUsername(job.getUsername()));
        
        Optional<ExportTask> taskOptional = taskRepo.findById(job.getTaskId());
        if (!taskOptional.isPresent()) {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode node = mapper.createObjectNode();
            node.put("message", "Task id is invalid.");
            return new ResponseEntity<String>(node.toString(), HttpStatus.NOT_FOUND);
        }
        IExportTask task = taskOptional.get();
        
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("zotero", zoteroToken.getToken());
        node.put("zoteroId", zoteroToken.getUserId());
        node.put("groupId", task.getGroupId());
        node.put("collectionId", task.getCollectionId());
        
        return new ResponseEntity<>(node.toString(), HttpStatus.OK);
    }
}
