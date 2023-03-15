package edu.asu.diging.citesphere.core.export.impl;

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
import edu.asu.diging.citesphere.core.export.IExportTaskManager;
import edu.asu.diging.citesphere.core.model.export.ExportStatus;
import edu.asu.diging.citesphere.core.model.export.IExportTask;
import edu.asu.diging.citesphere.core.model.export.impl.ExportTask;
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
    private IExportTaskManager taskManager;
    
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
        group.setGroupId(groupId);
        group.setName(GROUP_NAME);
        
        Mockito.when(citationManager.getGroupItems(user, groupId.toString(), null, 1, SORT_BY, null, null)).thenReturn(results1);
        Mockito.when(citationManager.getGroupItems(user, groupId.toString(), COLLECTION_ID, 1, SORT_BY, null, null)).thenReturn(results1);
        Mockito.when(groupManager.getGroup(user, groupId.toString())).thenReturn(group);
        
        task = new ExportTask();
        task.setStatus(ExportStatus.STARTED);
        task.setId(EXPORT_TASK_ID);
        Mockito.when(taskManager.saveAndFlush(Mockito.any(ExportTask.class))).thenReturn(task);
        
        Mockito.when(taskManager.get(EXPORT_TASK_ID)).thenReturn(task);
        
        collection = new CitationCollection();
        collection.setKey(COLLECTION_ID);
        
        Mockito.when(collectionManager.getCollection(user, groupId.toString(), COLLECTION_ID)).thenReturn(collection);
        
        task1 = new ExportTask();
        task1.setId(TASK1_ID);
        Mockito.when(taskManager.get(TASK1_ID)).thenReturn(task1);
        
        IExportTask newExportTask = new ExportTask();
        newExportTask.setExportType(ExportType.CSV);
        newExportTask.setUsername(user.getUsername());
        newExportTask.setStatus(ExportStatus.PENDING);
        Mockito.when(taskManager.createExportTask(ExportType.CSV, user.getUsername(), ExportStatus.PENDING)).thenReturn(newExportTask);
        
        
        Whitebox.setInternalState(managerToTest, "maxExportSize", 300);
        managerToTest.init();
    }
    
    @Test
    public void test_export_group() throws GroupDoesNotExistException, ExportTypeNotSupportedException, ExportFailedException, ExportTooBigException, ZoteroHttpStatusException {
        managerToTest.export(ExportType.CSV, user, groupId.toString(), null);
        Mockito.verify(processor).runExport(ExportType.CSV, user, groupId.toString(), null, task, managerToTest);
        Mockito.verify(taskManager).saveAndFlush(Mockito.any(ExportTask.class));
    }
    
    @Test
    public void test_export_collection() throws GroupDoesNotExistException, ExportTypeNotSupportedException, ExportFailedException, ExportTooBigException, ZoteroHttpStatusException {
        managerToTest.export(ExportType.CSV, user, groupId.toString(), COLLECTION_ID);
        Mockito.verify(processor).runExport(ExportType.CSV, user, groupId.toString(), COLLECTION_ID, task, managerToTest);
        Mockito.verify(taskManager).saveAndFlush(Mockito.any(ExportTask.class));
    }
    
    @Test(expected=ExportTooBigException.class)
    public void test_export_maxExportSize() throws GroupDoesNotExistException, ExportTypeNotSupportedException, ExportFailedException, ExportTooBigException, ZoteroHttpStatusException {
        CitationResults results2 = new CitationResults();
        results2.setTotalResults(400);
        String GROUP2 = "GROUP2";
        Mockito.when(citationManager.getGroupItems(user, GROUP2, null, 1, SORT_BY, null, null)).thenReturn(results2);
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
        
        IExportTask newExportTask = new ExportTask();
        newExportTask.setExportType(null);
        newExportTask.setUsername(user.getUsername());
        newExportTask.setStatus(ExportStatus.PENDING);
        Mockito.when(taskManager.createExportTask(null, user.getUsername(), ExportStatus.PENDING)).thenReturn(newExportTask);
        
        managerToTest.export(null, user, groupId.toString(), null);
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
