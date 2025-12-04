package edu.asu.diging.citesphere.core.service.jobs.impl;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import edu.asu.diging.citesphere.core.model.jobs.JobStatus;
import edu.asu.diging.citesphere.core.model.jobs.impl.GroupSyncJob;
import edu.asu.diging.citesphere.core.repository.jobs.GroupSyncJobRepository;
import edu.asu.diging.citesphere.core.service.ICitationManager;
import edu.asu.diging.citesphere.core.service.jobs.ISyncJobManager;
import edu.asu.diging.citesphere.model.bib.ICitationGroup;
import edu.asu.diging.citesphere.user.IUser;

@Service
public class SyncJobManager implements ISyncJobManager {

    private Map<String, GroupSyncJob> currentJobs;
    
    @Autowired
    private GroupSyncJobRepository jobRepo;
    
    @Autowired
    private ICitationManager citationManager;
    
    @PostConstruct
    public void init() {
        currentJobs = new ConcurrentHashMap<>();
    }
    
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.service.jobs.impl.ISyncJobManager#addJobId(edu.asu.diging.citesphere.core.model.jobs.impl.GroupSyncJob)
     */
    @Override
    public void addJob(GroupSyncJob job) {
        currentJobs.put(job.getGroupId(), job);
    }
    
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.service.jobs.impl.ISyncJobManager#getMostRecentJob(java.lang.String)
     */
    @Override
    public GroupSyncJob getMostRecentJob(String groupId) {
        GroupSyncJob job = currentJobs.get(groupId);
        if (job == null) {
            Optional<GroupSyncJob> jobOptional = jobRepo.findFirstByGroupIdOrderByCreatedOnDesc(groupId);
            if (jobOptional.isPresent()) {
                job = jobOptional.get();
            }
        }
        return job;
    }
    
    @Override
    public List<GroupSyncJob> getJobs(IUser user, Pageable page, String groupId, String status) {
        List<ICitationGroup> groups = citationManager.getGroups(user);
        if (groups == null || groups.isEmpty()) {
            return Collections.emptyList();
        }
        Map<String, String> groupNames = groups.stream().collect(
                Collectors.toMap(g -> g.getGroupId() + "", ICitationGroup::getName, (existing, replacement) -> existing));
        List<String> groupIds = groups.stream().map(g -> g.getGroupId() + "").collect(Collectors.toList());
        if (StringUtils.hasText(groupId)) {
            if (!groupIds.contains(groupId)) {
                return Collections.emptyList();
            }
            groupIds = Collections.singletonList(groupId);
        }
        List<GroupSyncJob> jobs;
        if (StringUtils.hasText(status)) {
            try {
                JobStatus jobStatus = JobStatus.valueOf(status.toUpperCase());
                jobs = jobRepo.findByGroupIdInAndStatus(groupIds, jobStatus, page);
            } catch (IllegalArgumentException e) {
                return Collections.emptyList();
            }
        } else {
            jobs = jobRepo.findByGroupIdIn(groupIds, page);
        }
        jobs.forEach(job -> job.setGroupName(groupNames.get(job.getGroupId())));
        return jobs;
    }
    
    @Override
    public long getJobsCount(IUser user, String groupId, String status) {
        List<ICitationGroup> groups = citationManager.getGroups(user);
        if (groups == null) {
            return -1;
        }
        List<String> groupIds = groups.stream().map(g -> g.getGroupId() + "").collect(Collectors.toList());
        if (StringUtils.hasText(groupId)) {
            if (!groupIds.contains(groupId)) {
                return 0;
            }
            groupIds = Collections.singletonList(groupId);
        }
        if (StringUtils.hasText(status)) {
            try {
                JobStatus jobStatus = JobStatus.valueOf(status.toUpperCase());
                return jobRepo.countByGroupIdInAndStatus(groupIds, jobStatus);
            } catch (IllegalArgumentException e) {
                return 0;
            }
        }
        return jobRepo.countByGroupIdIn(groupIds);
    }
    
    @Override
    public void cancelJob(String jobId) {
        Optional<GroupSyncJob> jobOptional = jobRepo.findById(jobId);
        if (jobOptional.isPresent()) {
            GroupSyncJob job = currentJobs.get(jobOptional.get().getGroupId());
            if (job == null) {
                job = jobOptional.get();
            }
            job.setStatus(JobStatus.CANCELED);
            job.setFinishedOn(OffsetDateTime.now());
            jobRepo.save(job);
        }
    }
}
