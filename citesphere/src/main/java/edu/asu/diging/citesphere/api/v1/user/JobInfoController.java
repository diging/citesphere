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
        
        Map<String, Object> reponse = new HashMap<>();
        reponse.put("zotero", zoteroToken.getToken());
        reponse.put("zoteroId", zoteroToken.getUserId());
        reponse.put("username", job.getUsername());
        // FIXME: ugly, needs better solution
        if (job instanceof IUploadJob) {
            reponse.put("groupId", ((IUploadJob)job).getCitationGroup());
        } else if (job instanceof IExportJob) {
            IExportTask exportTask = exportTaskManager.get(((IExportJob)job).getTaskId());
            reponse.put("groupId", exportTask.getGroupId());
            reponse.put("collectionId", exportTask.getCollectionId());
            reponse.put("exportType", exportTask.getExportType().name());
            reponse.put("taskId", exportTask.getId());
        } else if (job instanceof IImportCrossrefJob) {
            List<String> doisList = new ArrayList<>();
            ((IImportCrossrefJob)job).getDois().forEach(d -> doisList.add(d));
            reponse.put("dois", doisList);
            reponse.put("groupId", ((IImportCrossrefJob)job).getCitationGroup());
        }
        
        return new ResponseEntity<>(reponse.toString(), HttpStatus.OK);
    }
}
