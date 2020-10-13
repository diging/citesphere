package edu.asu.diging.citesphere.core.repository.jobs.impl;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import edu.asu.diging.citesphere.core.model.jobs.impl.GroupSyncJob;
import edu.asu.diging.citesphere.core.repository.jobs.GroupSyncJobCustomRepository;

@Component
public class GroupSyncJobCustomRepositoryImpl implements GroupSyncJobCustomRepository {

    @Autowired
    private EntityManager entityManager;
    
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.repository.jobs.impl.GroupSyncJobCustomRepository#refresh(edu.asu.diging.citesphere.core.model.jobs.impl.GroupSyncJob)
     */
    @Override
    public void refresh(GroupSyncJob job) {
        entityManager.refresh(job);
    }
}
