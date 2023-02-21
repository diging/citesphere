package edu.asu.diging.citesphere.core.repository.jobs;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import edu.asu.diging.citesphere.core.model.jobs.JobStatus;
import edu.asu.diging.citesphere.core.model.jobs.impl.GroupSyncJob;

public interface GroupSyncJobRepository extends PagingAndSortingRepository<GroupSyncJob, String> {

    public Optional<GroupSyncJob> findFirstByGroupIdOrderByCreatedOnDesc(String groupId);
    
    public List<GroupSyncJob> findByGroupIdIn(List<String> groupIds, Pageable page);
    
    public List<GroupSyncJob> findByGroupIdInAndStatus(List<String> groupIds, JobStatus status, Pageable page);
    
    public List<GroupSyncJob> findByGroupIdInAndId(List<String> groupIds, String id, Pageable page);
    
    public List<GroupSyncJob> findByGroupIdInAndStatusAndIdStartsWith(List<String> groupIds, JobStatus status, String id, Pageable page);
    
    public long countByGroupIdIn(List<String> groupIds);
}
