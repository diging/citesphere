package edu.asu.diging.citesphere.api.v1.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        
        Map<String, Object> response = new HashMap<>();
        response.put("zotero", zoteroToken.getToken());
        response.put("zoteroId", zoteroToken.getUserId());
        response.put("username", job.getUsername());
        // FIXME: ugly, needs better solution
        if (job instanceof IUploadJob) {
            handleUploadJob(response, (IUploadJob) job);
        } else if (job instanceof IExportJob) {
            handleExportJob(response, (IExportJob) job);
        } else if (job instanceof IImportCrossrefJob) {
            handleImportCrossrefJob(response, (IImportCrossrefJob) job);
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
