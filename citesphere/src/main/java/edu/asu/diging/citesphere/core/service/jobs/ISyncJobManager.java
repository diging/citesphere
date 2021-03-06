package edu.asu.diging.citesphere.core.service.jobs;

import java.util.List;

import org.springframework.data.domain.Pageable;

import edu.asu.diging.citesphere.core.model.jobs.impl.GroupSyncJob;
import edu.asu.diging.citesphere.user.IUser;

public interface ISyncJobManager {

    void addJob(GroupSyncJob job);

    GroupSyncJob getMostRecentJob(String groupId);

    List<GroupSyncJob> getJobs(IUser user, Pageable page);

    long getJobsCount(IUser user);

    void cancelJob(String jobId);

}