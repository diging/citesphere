package edu.asu.diging.citesphere.api.v1.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.asu.diging.citesphere.core.export.IExportTaskManager;
import edu.asu.diging.citesphere.core.model.IZoteroToken;
import edu.asu.diging.citesphere.core.model.export.IExportTask;
import edu.asu.diging.citesphere.core.model.jobs.IExportJob;
import edu.asu.diging.citesphere.core.model.jobs.IJob;
import edu.asu.diging.citesphere.core.model.jobs.IUploadJob;
import edu.asu.diging.citesphere.core.service.jobs.IUploadJobManager;
import edu.asu.diging.citesphere.core.service.jwt.IJobApiTokenContents;
import edu.asu.diging.citesphere.core.user.IUserManager;
import edu.asu.diging.citesphere.core.zotero.impl.ZoteroTokenManager;

@Controller
public class JobInfoController extends BaseJobInfoController {
    
    @Autowired
    private IUserManager userManager;
    
    @Autowired
    private ZoteroTokenManager tokenManager;
    
    @Autowired
    private IUploadJobManager jobManager;
    
    @Autowired
    private IExportTaskManager exportTaskManager;
    

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
        
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("zotero", zoteroToken.getToken());
        node.put("zoteroId", zoteroToken.getUserId());
        node.put("username", job.getUsername());
        // FIXME: ugly, needs better solution
        if (job instanceof IUploadJob) {
            node.put("groupId", ((IUploadJob)job).getCitationGroup());
        }
        if (job instanceof IExportJob) {
            IExportTask exportTask = exportTaskManager.get(((IExportJob)job).getTaskId());
            node.put("groupId", exportTask.getGroupId());
            node.put("collectionId", exportTask.getCollectionId());
            node.put("exportType", exportTask.getExportType().name());
            node.put("taskId", exportTask.getId());
        }
        
        
        return new ResponseEntity<>(node.toString(), HttpStatus.OK);
    }
}
