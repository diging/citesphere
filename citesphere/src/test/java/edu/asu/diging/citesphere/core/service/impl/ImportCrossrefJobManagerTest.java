package edu.asu.diging.citesphere.core.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.exceptions.MessageCreationException;
import edu.asu.diging.citesphere.core.kafka.IKafkaRequestProducer;
import edu.asu.diging.citesphere.core.model.jobs.IImportCrossrefJob;
import edu.asu.diging.citesphere.core.model.jobs.JobStatus;
import edu.asu.diging.citesphere.core.model.jobs.impl.ImportCrossrefJob;
import edu.asu.diging.citesphere.core.repository.jobs.ImportCrossrefJobRepository;
import edu.asu.diging.citesphere.core.service.IGroupManager;
import edu.asu.diging.citesphere.core.service.jobs.impl.ImportCrossrefJobManager;
import edu.asu.diging.citesphere.core.service.jwt.IJwtTokenService;
import edu.asu.diging.citesphere.messages.model.KafkaJobMessage;
import edu.asu.diging.citesphere.model.bib.ICitationGroup;
import edu.asu.diging.citesphere.user.IUser;

@RunWith(MockitoJUnitRunner.class)
public class ImportCrossrefJobManagerTest {

    @InjectMocks
    private ImportCrossrefJobManager importCrossrefJobManager;

    @Mock
    private IGroupManager groupManager;

    @Mock
    private ImportCrossrefJobRepository jobRepo;

    @Mock
    private IKafkaRequestProducer kafkaProducer;

    @Mock
    private IJwtTokenService tokenService;
    
    @Mock
    private IUser user;

    @Mock
    private ICitationGroup group;
    private List<String> dois;

    @Before
    public void setup() {
        ReflectionTestUtils.setField(importCrossrefJobManager, "jobPageSize", 10);
        doReturn("username").when(user).getUsername();
        dois = Arrays.asList("10.1000/xyz123", "10.1000/xyz456");
    }

    @Test
    public void test_createJob_success() throws Exception {
        String groupId = "groupId";
        List<String> dois = Arrays.asList("doi1", "doi2");
        doReturn(group).when(groupManager).getGroup(user, groupId);
        doReturn("jobToken").when(tokenService).generateJobApiToken(any(ImportCrossrefJob.class));

        IImportCrossrefJob job = importCrossrefJobManager.createJob(user, groupId, dois);

        assertNotNull(job);
        assertEquals(user.getUsername(), job.getUsername());
        assertEquals(dois, job.getDois());
        assertEquals(groupId, job.getCitationGroup());
        assertEquals(JobStatus.PREPARED, job.getStatus());
        verify(jobRepo, times(1)).save(any(ImportCrossrefJob.class));
        verify(kafkaProducer).sendRequest(any(KafkaJobMessage.class), anyString());
    }

    @Test
    public void test_createJob_groupDoesNotExist() {
        String groupId = "groupId";
        List<String> dois = Arrays.asList("doi1", "doi2");
        doReturn(null).when(groupManager).getGroup(user, groupId);

        GroupDoesNotExistException exception = assertThrows(GroupDoesNotExistException.class, () -> importCrossrefJobManager.createJob(user, groupId, dois));
        assertNotNull(exception);
    }

    @Test
    public void test_createJob_messageCreationException() throws Exception {
        String groupId = "groupId";
        when(groupManager.getGroup(user, groupId)).thenReturn(group);
        when(tokenService.generateJobApiToken(any(ImportCrossrefJob.class))).thenReturn("token");
        doThrow(new MessageCreationException("Error creating message")).when(kafkaProducer).sendRequest(any(KafkaJobMessage.class), anyString());

        IImportCrossrefJob job = importCrossrefJobManager.createJob(user, groupId, dois);

        assertNotNull(job);
        assertEquals(JobStatus.FAILURE, job.getStatus());
        assertEquals(1, job.getPhases().size());
        verify(jobRepo, times(2)).save(any(ImportCrossrefJob.class));
    }
}