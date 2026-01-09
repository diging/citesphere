package edu.asu.diging.citesphere.core.service.jobs.impl;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import edu.asu.diging.citesphere.core.model.jobs.JobStatus;
import edu.asu.diging.citesphere.core.model.jobs.impl.GroupSyncJob;
import edu.asu.diging.citesphere.core.repository.jobs.GroupSyncJobRepository;
import edu.asu.diging.citesphere.core.service.ICitationManager;
import edu.asu.diging.citesphere.core.service.jobs.ISyncJobManager;
import edu.asu.diging.citesphere.model.bib.ICitationGroup;
import edu.asu.diging.citesphere.user.IUser;

@Service
public class SyncJobManager implements ISyncJobManager {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private Map<String, GroupSyncJob> currentJobs;

    @Autowired
    private GroupSyncJobRepository jobRepo;

    @Autowired
    private ICitationManager citationManager;

    @Autowired
    private JobStatusChecker jobStatusChecker;
    
    @PostConstruct
    public void init() {
        currentJobs = new ConcurrentHashMap<>();
        cleanupStalledJobs();
    }

    /**
     * Marks stalled jobs (older than 1 hour) as FAILURE on application startup.
     */
    private void cleanupStalledJobs() {
        OffsetDateTime threshold = OffsetDateTime.now().minusHours(1);
        List<GroupSyncJob> stalledJobs = jobRepo.findByStatusInAndCreatedOnBefore(
            Arrays.asList(JobStatus.STARTED, JobStatus.PREPARED, JobStatus.SYNCING),
            threshold
        );

        for (GroupSyncJob job : stalledJobs) {
            logger.warn("Found stalled job {} from {}, marking as FAILURE",
                job.getId(), job.getCreatedOn());
            job.setStatus(JobStatus.FAILURE);
            job.setFinishedOn(OffsetDateTime.now());
            jobRepo.save(job);
        }

        if (!stalledJobs.isEmpty()) {
            logger.info("Cleaned up {} stalled jobs", stalledJobs.size());
        }
    }

    @Override
    public void addJob(GroupSyncJob job) {
        currentJobs.put(job.getGroupId(), job);
    }
    
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
    public List<GroupSyncJob> getJobs(IUser user, Pageable page) {
        List<ICitationGroup> groups = citationManager.getGroups(user);
        return jobRepo.findByGroupIdIn(groups.stream().map(g -> g.getGroupId() + "").collect(Collectors.toList()), page);
    }
    
    @Override
    public long getJobsCount(IUser user) {
        List<ICitationGroup> groups = citationManager.getGroups(user);
        if (groups == null) {
            return -1;
        }
        return jobRepo.countByGroupIdIn(groups.stream().map(g -> g.getGroupId() + "").collect(Collectors.toList()));
    }
    
    @Override
    public void cancelJob(String jobId) {
        Optional<GroupSyncJob> jobOptional = jobRepo.findById(jobId);
        if (!jobOptional.isPresent()) {
            logger.warn("Cannot cancel job {}: job not found", jobId);
            return;
        }

        GroupSyncJob job = jobOptional.get();

        if (job.getStatus() == JobStatus.DONE ||
            job.getStatus() == JobStatus.CANCELED ||
            job.getStatus() == JobStatus.FAILURE) {
            logger.info("Job {} already in terminal state: {}", jobId, job.getStatus());
            return;
        }

        // Update database first, then invalidate cache, then interrupt thread
        job.setStatus(JobStatus.CANCELED);
        job.setFinishedOn(OffsetDateTime.now());
        jobRepo.save(job);
        logger.info("Marked job {} as CANCELED in database", jobId);

        jobStatusChecker.invalidateCache(jobId);

        boolean interrupted = citationManager.cancel(job.getGroupId());
        if (interrupted) {
            logger.info("Successfully interrupted thread for job {}", jobId);
        } else {
            logger.warn("Could not interrupt thread for job {} - will be detected on next check", jobId);
        }

        currentJobs.remove(job.getGroupId());
    }
}
