package edu.asu.diging.citesphere.core.export.impl;

import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.reflection.Whitebox;

import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroHttpStatusException;
import edu.asu.diging.citesphere.core.model.export.IExportTask;
import edu.asu.diging.citesphere.core.model.export.impl.ExportTask;
import edu.asu.diging.citesphere.core.repository.export.ExportTaskRepository;
import edu.asu.diging.citesphere.user.IUser;
import edu.asu.diging.citesphere.user.impl.User;

public class ExportTaskManagerTest {

    @Mock
    private ExportTaskRepository taskRepo;
    
    @InjectMocks
    private ExportTaskManager managerToTest;
    
    private ExportTask task1;
    private String TASK1_ID = "task1";
    
    private IUser user;
    private String USERNAME = "username";
    
    
    @Before
    public void setUp() throws GroupDoesNotExistException, ZoteroHttpStatusException {
        MockitoAnnotations.initMocks(this);
        
        user = new User();
        user.setUsername(USERNAME);
        
        task1 = new ExportTask();
        task1.setId(TASK1_ID);
        Mockito.when(taskRepo.findById(TASK1_ID)).thenReturn(Optional.of(task1));
        Mockito.when(taskRepo.saveAndFlush(Mockito.any(ExportTask.class))).thenReturn(task1);
        
        Whitebox.setInternalState(managerToTest, "tasksPageSize", 5);
    }
    
    @Test
    public void test_get_success() {
        IExportTask actual = managerToTest.get(TASK1_ID);
        Assert.assertEquals(TASK1_ID, actual.getId());
    }
    
    @Test
    public void test_get_noResult() {
        Mockito.when(taskRepo.findById("TASK2")).thenReturn(Optional.empty());
        Assert.assertNull(managerToTest.get("TASK2"));
    }
    
    @Test
    public void test_getTasksTotalPages_onePage() {
        Mockito.when(taskRepo.countByUsername(USERNAME)).thenReturn(5);
        Assert.assertEquals(1, managerToTest.getTasksTotalPages(user));
    }
    
    @Test
    public void test_getTasksTotalPages_moreThanOnePage() {
        Mockito.when(taskRepo.countByUsername(USERNAME)).thenReturn(24);
        Assert.assertEquals(5, managerToTest.getTasksTotalPages(user));
    }
}
