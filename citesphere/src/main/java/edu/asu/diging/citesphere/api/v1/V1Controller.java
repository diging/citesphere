package edu.asu.diging.citesphere.api.v1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.asu.diging.citesphere.api.v1.model.impl.SyncInfo;
import edu.asu.diging.citesphere.core.model.jobs.impl.GroupSyncJob;
import edu.asu.diging.citesphere.core.service.jobs.ISyncJobManager;
import edu.asu.diging.citesphere.model.bib.ICitationGroup;

@RequestMapping("/api/v1")
public class V1Controller {

    @Autowired
    private ISyncJobManager jobManager;

    protected SyncInfo getSyncInfo(ICitationGroup group) {
        GroupSyncJob job = jobManager.getMostRecentJob(group.getGroupId() + "");
        SyncInfo info = new SyncInfo();
        if (job != null) {
            info.createdOn = job.getCreatedOn().toString();
            info.status = job.getStatus() != null ? job.getStatus().name() : "";
        }
        return info;
    }
}
