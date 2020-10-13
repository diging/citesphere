package edu.asu.diging.citesphere.core.service.jobs;

import edu.asu.diging.citesphere.core.model.jobs.impl.GroupSyncJob;

public interface ISyncJobManager {

    void addJob(GroupSyncJob job);

    GroupSyncJob getMostRecentJob(String groupId);

}