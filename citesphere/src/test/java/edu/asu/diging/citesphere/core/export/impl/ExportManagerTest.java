package edu.asu.diging.citesphere.core.export.impl;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.reflection.Whitebox;

import edu.asu.diging.citesphere.core.exceptions.ExportFailedException;
import edu.asu.diging.citesphere.core.exceptions.ExportTooBigException;
import edu.asu.diging.citesphere.core.exceptions.ExportTypeNotSupportedException;
import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroHttpStatusException;
import edu.asu.diging.citesphere.core.export.ExportType;
import edu.asu.diging.citesphere.core.export.IExportProcessor;
import edu.asu.diging.citesphere.core.model.export.ExportStatus;
import edu.asu.diging.citesphere.core.model.export.IExportTask;
import edu.asu.diging.citesphere.core.model.export.impl.ExportTask;
import edu.asu.diging.citesphere.core.repository.export.ExportTaskRepository;
import edu.asu.diging.citesphere.core.service.ICitationCollectionManager;
import edu.asu.diging.citesphere.core.service.ICitationManager;
import edu.asu.diging.citesphere.core.service.IGroupManager;
import edu.asu.diging.citesphere.model.bib.ICitationCollection;
import edu.asu.diging.citesphere.model.bib.ICitationGroup;
import edu.asu.diging.citesphere.model.bib.impl.CitationCollection;
import edu.asu.diging.citesphere.model.bib.impl.CitationGroup;
import edu.asu.diging.citesphere.model.bib.impl.CitationResults;
import edu.asu.diging.citesphere.user.IUser;
import edu.asu.diging.citesphere.user.impl.User;

public class ExportManagerTest {
    
    @Mock
    private ExportTaskRepository taskRepo;
    
    @Mock
    private IExportProcessor processor;
    
    @Mock
    private ICitationManager citationManager;
    
    @Mock
    private IGroupManager groupManager;
    
    @Mock
    private ICitationCollectionManager collectionManager;

    @InjectMocks
    private ExportManager managerToTest;
    
    private CitationResults results1;
    private IUser user;
    private String USERNAME = "username";
    
    private ICitationGroup group;
    private Long groupId = new Long(1);
    private String GROUP_NAME = "group";
    
    private ExportTask task;
    private String EXPORT_TASK_ID = "ID";
    
    private ICitationCollection collection;
    private String COLLECTION_ID = "collectionId";
    
    private String SORT_BY = "title";
    
    private ExportTask task1;
    private String TASK1_ID = "task1";
    
    @Before
    public void setUp() throws GroupDoesNotExistException, ZoteroHttpStatusException {
        MockitoAnnotations.initMocks(this);
        
        user = new User();
        user.setUsername(USERNAME);
        results1 = new CitationResults();
        results1.setTotalResults(10);
        
        group = new CitationGroup();
        group.setId(groupId);
        group.setName(GROUP_NAME);
        
        Mockito.when(citationManager.getGroupItems(user, groupId.toString(), null, 1, SORT_BY)).thenReturn(results1);
        Mockito.when(citationManager.getGroupItems(user, groupId.toString(), COLLECTION_ID, 1, SORT_BY)).thenReturn(results1);
        Mockito.when(groupManager.getGroup(user, groupId.toString())).thenReturn(group);
        
        task = new ExportTask();
        task.setStatus(ExportStatus.STARTED);
        task.setId(EXPORT_TASK_ID);
        Mockito.when(taskRepo.saveAndFlush(Mockito.any(ExportTask.class))).thenReturn(task);
        
        Mockito.when(taskRepo.findById(EXPORT_TASK_ID)).thenReturn(Optional.of(task));
        
        collection = new CitationCollection();
        collection.setKey(COLLECTION_ID);
        
        Mockito.when(collectionManager.getCollection(user, groupId.toString(), COLLECTION_ID)).thenReturn(collection);
        
        task1 = new ExportTask();
        task1.setId(TASK1_ID);
        Mockito.when(taskRepo.findById(TASK1_ID)).thenReturn(Optional.of(task1));
        
        Whitebox.setInternalState(managerToTest, "maxExportSize", 300);
        Whitebox.setInternalState(managerToTest, "tasksPageSize", 5);
        managerToTest.init();
    }
    
    @Test
    public void test_export_group() throws GroupDoesNotExistException, ExportTypeNotSupportedException, ExportFailedException, ExportTooBigException, ZoteroHttpStatusException {
        managerToTest.export(ExportType.CSV, user, groupId.toString(), null);
        Mockito.verify(processor).runExport(ExportType.CSV, user, groupId.toString(), null, task, managerToTest);
        Mockito.verify(taskRepo).saveAndFlush(Mockito.any(ExportTask.class));
    }
    
    @Test
    public void test_export_collection() throws GroupDoesNotExistException, ExportTypeNotSupportedException, ExportFailedException, ExportTooBigException, ZoteroHttpStatusException {
        managerToTest.export(ExportType.CSV, user, groupId.toString(), COLLECTION_ID);
        Mockito.verify(processor).runExport(ExportType.CSV, user, groupId.toString(), COLLECTION_ID, task, managerToTest);
        Mockito.verify(taskRepo).saveAndFlush(Mockito.any(ExportTask.class));
    }
    
    @Test(expected=ExportTooBigException.class)
    public void test_export_maxExportSize() throws GroupDoesNotExistException, ExportTypeNotSupportedException, ExportFailedException, ExportTooBigException, ZoteroHttpStatusException {
        CitationResults results2 = new CitationResults();
        results2.setTotalResults(400);
        String GROUP2 = "GROUP2";
        Mockito.when(citationManager.getGroupItems(user, GROUP2, null, 1, SORT_BY)).thenReturn(results2);
        Mockito.when(groupManager.getGroup(user, GROUP2)).thenReturn(new CitationGroup());
        
        managerToTest.export(ExportType.CSV, user, GROUP2, null);
    }
    
    @Test(expected=GroupDoesNotExistException.class)
    public void test_export_groupDoesNotExist() throws GroupDoesNotExistException, ExportTypeNotSupportedException, ExportFailedException, ExportTooBigException, ZoteroHttpStatusException {
        managerToTest.export(ExportType.CSV, user, "GROUP3", null);
    }
    
    @Test(expected=ExportTypeNotSupportedException.class)
    public void test_export_typeNotSupported() throws GroupDoesNotExistException, ExportTypeNotSupportedException, ExportFailedException, ExportTooBigException, ZoteroHttpStatusException {
        Mockito.doThrow(ExportTypeNotSupportedException.class).when(processor).runExport(null, user, groupId.toString(), null, task, managerToTest);
        managerToTest.export(null, user, groupId.toString(), null);
    }
    
    @Test
    public void test_getTask_success() {
        IExportTask actual = managerToTest.getTask(TASK1_ID);
        Assert.assertEquals(TASK1_ID, actual.getId());
    }
    
    @Test
    public void test_getTask_noResult() {
        Mockito.when(taskRepo.findById("TASK2")).thenReturn(Optional.empty());
        Assert.assertNull(managerToTest.getTask("TASK2"));
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
    
    @Test
    public void test_exportFinished() throws GroupDoesNotExistException, ExportTypeNotSupportedException, ExportFailedException, ExportTooBigException, ZoteroHttpStatusException {
        ConcurrentHashMap<String, IExportTask> runningTasks = (ConcurrentHashMap<String, IExportTask>) Whitebox.getInternalState(managerToTest, "runningTasks");
        managerToTest.export(ExportType.CSV, user, groupId.toString(), null);
        Assert.assertEquals(1, runningTasks.size());
        managerToTest.exportFinished(EXPORT_TASK_ID);
        Assert.assertEquals(0, runningTasks.size());
    }
    
    @Test
    public void test_shutdown() throws GroupDoesNotExistException, ExportTypeNotSupportedException, ExportFailedException, ExportTooBigException, ZoteroHttpStatusException {
        ConcurrentHashMap<String, IExportTask> runningTasks = (ConcurrentHashMap<String, IExportTask>) Whitebox.getInternalState(managerToTest, "runningTasks");
        managerToTest.export(ExportType.CSV, user, groupId.toString(), null);
        Assert.assertEquals(1, runningTasks.size());
        managerToTest.shutdown();
        Assert.assertEquals(ExportStatus.FAILED, task.getStatus());
    }
}
