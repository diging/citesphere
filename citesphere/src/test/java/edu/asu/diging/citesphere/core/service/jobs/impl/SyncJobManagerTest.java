package edu.asu.diging.citesphere.core.service.jobs.impl;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;

import edu.asu.diging.citesphere.core.model.jobs.impl.GroupSyncJob;
import edu.asu.diging.citesphere.core.repository.jobs.GroupSyncJobRepository;
import edu.asu.diging.citesphere.core.service.ICitationManager;
import edu.asu.diging.citesphere.model.bib.ICitationGroup;
import edu.asu.diging.citesphere.model.bib.impl.CitationGroup;
import edu.asu.diging.citesphere.user.impl.User;

public class SyncJobManagerTest {

    @InjectMocks
    private SyncJobManager managerToTest;

    @Mock
    private GroupSyncJobRepository jobRepo;

    @Mock
    private ICitationManager citationManager;

    private int page;
    private int pageSize;
    private List<ICitationGroup> citationGroupList;
    private ICitationGroup citationGroup1;
    private ICitationGroup citationGroup2;
    private List<GroupSyncJob> groupSyncJobList;
    private GroupSyncJob groupSyncJob1;
    private GroupSyncJob groupSyncJob2;
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        managerToTest.init();
        new User();
        page = 1;
        pageSize = 10;
        PageRequest.of(page, pageSize);

        citationGroup1 = new CitationGroup();
        citationGroup1.setGroupId(1L);
        citationGroup2 = new CitationGroup();
        groupSyncJob1 = new GroupSyncJob();
        groupSyncJob2 = new GroupSyncJob();

        citationGroupList = new ArrayList<>();
        citationGroupList.add(citationGroup1);
        citationGroupList.add(citationGroup2);

        groupSyncJobList = new ArrayList<>();
        groupSyncJobList.add(groupSyncJob1);
        groupSyncJobList.add(groupSyncJob2);

        new ArrayList<>();

        new ArrayList<>();
    }
    
    @Test
    public void test_deleteJob_success() {
        groupSyncJob1.setId("job1");
        groupSyncJob1.setGroupId(String.valueOf(citationGroup1.getGroupId()));
        
        when(jobRepo.findById("job1")).thenReturn(Optional.of(groupSyncJob1));

        managerToTest.addJob(groupSyncJob1);

        managerToTest.deleteJob("job1");

        verify(jobRepo, times(1)).delete(groupSyncJob1);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void test_deleteJob_failed() {
        groupSyncJob1.setId("job1");
        groupSyncJob1.setGroupId(String.valueOf(citationGroup1.getGroupId()));
        
        when(jobRepo.findById("job1")).thenReturn(Optional.of(groupSyncJob1));

        doThrow(new IllegalArgumentException("entity is null"))
            .when(jobRepo).delete(Mockito.any(GroupSyncJob.class));

        managerToTest.deleteJob("job1");
    }
    
    
}
