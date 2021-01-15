package edu.asu.diging.citesphere.core.service.jobs.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import edu.asu.diging.citesphere.core.model.jobs.impl.GroupSyncJob;
import edu.asu.diging.citesphere.core.repository.jobs.GroupSyncJobRepository;
import edu.asu.diging.citesphere.core.service.ICitationManager;
import edu.asu.diging.citesphere.core.service.IGroupManager;
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
    public List<GroupSyncJob> getJobs(IUser user, Pageable page) {
        List<ICitationGroup> groups = citationManager.getGroups(user);
        return jobRepo.findByGroupIdIn(groups.stream().map(g -> g.getGroupId() + "").collect(Collectors.toList()), page);
    }
    
    @Override
    public long getJobsCount(IUser user) {
        List<ICitationGroup> groups = citationManager.getGroups(user);
        return jobRepo.countByGroupIdIn(groups.stream().map(g -> g.getGroupId() + "").collect(Collectors.toList()));
    }
}
