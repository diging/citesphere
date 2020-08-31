package edu.asu.diging.citesphere.web.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.asu.diging.citesphere.core.model.jobs.impl.GroupSyncJob;
import edu.asu.diging.citesphere.core.service.jobs.ISyncJobManager;

@RestController
public class SyncInfoController {
    
    @Autowired
    private ISyncJobManager jobManager;
    
    @RequestMapping("/auth/group/{zoteroGroupId}/sync/info")
    public SyncInfo getSyncInfo(@PathVariable("zoteroGroupId") String groupId) {
       GroupSyncJob job = jobManager.getMostRecentJob(groupId);
        
        SyncInfo info = new SyncInfo();
        
        if(job != null) {
            info.createdOn = job.getCreatedOn().toString();
            info.total = job.getTotal();
            info.current = job.getCurrent();
            info.status = job.getStatus() != null ? job.getStatus().name() : "";
        }
        
        return info;
    }

    class SyncInfo {
        public String createdOn;
        public long total;
        public long current;
        public String status;
    }
}
