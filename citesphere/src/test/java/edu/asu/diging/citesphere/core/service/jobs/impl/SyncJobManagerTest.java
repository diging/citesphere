package edu.asu.diging.citesphere.core.service.jobs.impl;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import edu.asu.diging.citesphere.core.model.jobs.JobStatus;
import edu.asu.diging.citesphere.core.model.jobs.impl.GroupSyncJob;
import edu.asu.diging.citesphere.core.repository.jobs.GroupSyncJobRepository;
import edu.asu.diging.citesphere.core.service.ICitationManager;
import edu.asu.diging.citesphere.model.bib.ICitationGroup;
import edu.asu.diging.citesphere.model.bib.impl.CitationGroup;
import edu.asu.diging.citesphere.user.IUser;
import edu.asu.diging.citesphere.user.impl.User;

public class SyncJobManagerTest {

    @InjectMocks
    private SyncJobManager managerToTest;

    @Mock
    private GroupSyncJobRepository jobRepo;

    @Mock
    private ICitationManager citationManager;

    private IUser user;
    private int page;
    private int pageSize;
    private Pageable paging;
    private List<ICitationGroup> citationGroupList;
    private ICitationGroup citationGroup1;
    private ICitationGroup citationGroup2;
    private List<GroupSyncJob> groupSyncJobList;
    private List<GroupSyncJob> groupSyncJobs;
    private GroupSyncJob groupSyncJob1;
    private GroupSyncJob groupSyncJob2;
    private List<String> groupIds;
    private JobStatus status;
    private String groupId;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        user = new User();
        page = 1;
        pageSize = 10;
        paging = PageRequest.of(page, pageSize);

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

        groupIds = new ArrayList<>();

        groupSyncJobs = new ArrayList<>();
        status = JobStatus.DONE;
        groupId = "group1";
    }

    @Test
    public void test_getJobs() {
        Mockito.when(jobRepo.findByGroupIdIn(groupIds, paging)).thenReturn(groupSyncJobList);
        List<GroupSyncJob> actualResponse = managerToTest.getJobs(user, paging);
        Assert.assertEquals(groupSyncJobList.size(), actualResponse.size());
    }

    @Test
    public void test_getJobs_resultsNotFound() {
        Mockito.when(jobRepo.findByGroupIdIn(groupIds, paging)).thenReturn(groupSyncJobs);
        List<GroupSyncJob> actualResponse = managerToTest.getJobs(user, paging);
        Assert.assertTrue(actualResponse.isEmpty());
    }

    @Test
    public void test_getJobs_byJobStatus() {
        Mockito.when(jobRepo.findByGroupIdInAndStatus(groupIds, status, paging)).thenReturn(groupSyncJobList);
        List<GroupSyncJob> actualResponse = managerToTest.getJobs(user, status, paging);
        Assert.assertEquals(groupSyncJobList.size(), actualResponse.size());
    }

    @Test
    public void test_getJobs_byJobStatus_resultsNotFound() {
        Mockito.when(jobRepo.findByGroupIdInAndStatus(groupIds, status, paging)).thenReturn(groupSyncJobs);
        List<GroupSyncJob> actualResponse = managerToTest.getJobs(user, status, paging);
        Assert.assertTrue(actualResponse.isEmpty());
    }

    @Test
    public void test_getJobs_byGroupId() {
        Mockito.when(jobRepo.findByGroupId(groupId, paging)).thenReturn(groupSyncJobList);
        List<GroupSyncJob> actualResponse = managerToTest.getJobs(groupId, paging);
        Assert.assertEquals(groupSyncJobList.size(), actualResponse.size());
    }

    @Test
    public void test_getJobs_byGroupId_resultsNotFound() {
        Mockito.when(jobRepo.findByGroupId(groupId, paging)).thenReturn(groupSyncJobs);
        List<GroupSyncJob> actualResponse = managerToTest.getJobs(groupId, paging);
        Assert.assertTrue(actualResponse.isEmpty());
    }

    @Test
    public void test_getJobs_byJobStatusAndGroupId() {
        Mockito.when(jobRepo.findByGroupIdAndStatus(groupId, status, paging))
                .thenReturn(groupSyncJobList);
        List<GroupSyncJob> actualResponse = managerToTest.getJobs(groupId, status, paging);
        Assert.assertEquals(groupSyncJobList.size(), actualResponse.size());
    }

    @Test
    public void test_getJobs_byJobStatusAndGroupId_resultsNotFound() {
        Mockito.when(jobRepo.findByGroupIdAndStatus(groupId, status, paging))
                .thenReturn(groupSyncJobs);
        List<GroupSyncJob> actualResponse = managerToTest.getJobs(groupId, status, paging);
        Assert.assertTrue(actualResponse.isEmpty());
    }
}
