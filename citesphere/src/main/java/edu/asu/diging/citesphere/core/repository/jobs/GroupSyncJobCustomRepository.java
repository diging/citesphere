package edu.asu.diging.citesphere.core.repository.jobs;

import edu.asu.diging.citesphere.core.model.jobs.impl.GroupSyncJob;

public interface GroupSyncJobCustomRepository {

    void refresh(GroupSyncJob job);

}