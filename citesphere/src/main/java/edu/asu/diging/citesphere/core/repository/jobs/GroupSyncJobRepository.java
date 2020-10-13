package edu.asu.diging.citesphere.core.repository.jobs;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import edu.asu.diging.citesphere.core.model.jobs.impl.GroupSyncJob;

public interface GroupSyncJobRepository extends CrudRepository<GroupSyncJob, String> {

    public Optional<GroupSyncJob> findFirstByGroupIdOrderByCreatedOnDesc(String groupId);
}
