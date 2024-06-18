package edu.asu.diging.citesphere.core.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.social.zotero.api.ItemDeletionResponse;
import org.springframework.social.zotero.api.ZoteroUpdateItemsStatuses;
import org.springframework.social.zotero.exception.ZoteroConnectionException;

import com.fasterxml.jackson.core.JsonProcessingException;

import edu.asu.diging.citesphere.core.exceptions.CitationIsOutdatedException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroHttpStatusException;
import edu.asu.diging.citesphere.core.service.impl.async.AsyncDeleteCitationsResponse;
import edu.asu.diging.citesphere.core.service.impl.async.AsyncUpdateCitationsResponse;
import edu.asu.diging.citesphere.model.bib.ICitation;
import edu.asu.diging.citesphere.model.bib.impl.Citation;
import edu.asu.diging.citesphere.user.IUser;
import edu.asu.diging.citesphere.user.impl.User;
import edu.asu.diging.citesphere.web.user.AsyncTaskStatus;

public class AsyncCitationManagerTest {

    @InjectMocks
    private AsyncCitationManager managerToTest;

    @Mock
    private AsyncUpdateCitationsProcessor asyncUpdateCitationsProcessor;

    @Mock
    private AsyncDeleteCitationsProcessor asyncDeleteCitationsProcessor;

    private String groupId;
    private IUser user;

    private List<String> citationIdList;
    private List<ICitation> citationList;
    private String citationId1;
    private ICitation citation1;
    private String citationId2;
    private ICitation citation2;

    @Before
    public void init() throws NoSuchFieldException, SecurityException, Exception {
        MockitoAnnotations.initMocks(this);
        groupId = "123456";
        user = new User();
        citationId1 = "item1";
        citationId2 = "item2";

        citationIdList = new ArrayList<>();
        citationIdList.add(citationId1);
        citationIdList.add(citationId2);

        citationList = new ArrayList<>();
        citation1 = new Citation();
        citation1.setKey(citationId1);
        citationList.add(citation1);
        citation2 = new Citation();
        citation2.setKey(citationId2);
        citationList.add(citation2);

    }

    @Test
    public void test_updateCitations_success() throws JsonProcessingException, ZoteroConnectionException,
            CitationIsOutdatedException, ZoteroHttpStatusException, ExecutionException, InterruptedException {
        ZoteroUpdateItemsStatuses updateStatuses = new ZoteroUpdateItemsStatuses();
        updateStatuses.setSuccessItems(citationIdList);
        AsyncResult<ZoteroUpdateItemsStatuses> updateResponse = new AsyncResult<ZoteroUpdateItemsStatuses>(
                updateStatuses);

        Mockito.when(asyncUpdateCitationsProcessor.updateCitations(user, groupId, citationList))
                .thenReturn(updateResponse);

        AsyncUpdateCitationsResponse actualResponse = managerToTest.updateCitations(user, groupId, citationList);
        Assert.assertEquals(AsyncTaskStatus.PENDING, actualResponse.getTaskStatus());

        AsyncUpdateCitationsResponse updateStatus = managerToTest
                .getUpdateCitationsResponse(actualResponse.getTaskID());
        Assert.assertEquals(AsyncTaskStatus.COMPLETE, updateStatus.getTaskStatus());
        for (String citationId : citationIdList) {
            Assert.assertTrue(updateStatus.getResponse().getSuccessItems().contains(citationId));
        }
    }

    @Test
    public void test_updateCitations_failed() throws JsonProcessingException, ZoteroConnectionException,
            CitationIsOutdatedException, ZoteroHttpStatusException, ExecutionException, InterruptedException {
        ZoteroUpdateItemsStatuses updateStatuses = new ZoteroUpdateItemsStatuses();
        updateStatuses.setFailedItems(citationIdList);
        AsyncResult<ZoteroUpdateItemsStatuses> updateResponse = new AsyncResult<ZoteroUpdateItemsStatuses>(
                updateStatuses);

        Mockito.when(asyncUpdateCitationsProcessor.updateCitations(user, groupId, citationList))
                .thenReturn(updateResponse);

        AsyncUpdateCitationsResponse actualResponse = managerToTest.updateCitations(user, groupId, citationList);
        Assert.assertEquals(AsyncTaskStatus.PENDING, actualResponse.getTaskStatus());

        AsyncUpdateCitationsResponse updateStatus = managerToTest
                .getUpdateCitationsResponse(actualResponse.getTaskID());
        Assert.assertEquals(AsyncTaskStatus.COMPLETE, updateStatus.getTaskStatus());
        for (String citationId : citationIdList) {
            Assert.assertTrue(updateStatus.getResponse().getFailedItems().contains(citationId));
        }
    }

    @Test
    public void test_updateCitations_mixed() throws JsonProcessingException, ZoteroConnectionException,
            CitationIsOutdatedException, ZoteroHttpStatusException, ExecutionException, InterruptedException {
        ZoteroUpdateItemsStatuses updateStatuses = new ZoteroUpdateItemsStatuses();
        
        List<String> successList = new ArrayList<>();
        successList.add(citationId1);
        updateStatuses.setSuccessItems(successList);
        
        List<String> failedList = new ArrayList<>();
        failedList.add(citationId2);
        updateStatuses.setFailedItems(failedList);
        
        AsyncResult<ZoteroUpdateItemsStatuses> updateResponse = new AsyncResult<ZoteroUpdateItemsStatuses>(
                updateStatuses);

        Mockito.when(asyncUpdateCitationsProcessor.updateCitations(user, groupId, citationList))
                .thenReturn(updateResponse);

        AsyncUpdateCitationsResponse actualResponse = managerToTest.updateCitations(user, groupId, citationList);
        Assert.assertEquals(AsyncTaskStatus.PENDING, actualResponse.getTaskStatus());

        AsyncUpdateCitationsResponse updateStatus = managerToTest
                .getUpdateCitationsResponse(actualResponse.getTaskID());
        Assert.assertEquals(AsyncTaskStatus.COMPLETE, updateStatus.getTaskStatus());

        for (String citationId : successList) {
            Assert.assertTrue(updateStatus.getResponse().getSuccessItems().contains(citationId));
        }
        for (String citationId : failedList) {
            Assert.assertTrue(updateStatus.getResponse().getFailedItems().contains(citationId));
        }
    }

    @Test
    public void test_deleteCitations_success()
            throws ZoteroConnectionException, ZoteroHttpStatusException, ExecutionException, InterruptedException {
        Map<ItemDeletionResponse, List<String>> responseMap = new HashMap<>();
        responseMap.put(ItemDeletionResponse.SUCCESS, new ArrayList<>(citationIdList));
        AsyncResult<Map<ItemDeletionResponse, List<String>>> deletionResponse = new AsyncResult<Map<ItemDeletionResponse, List<String>>>(
                responseMap);

        Mockito.when(asyncDeleteCitationsProcessor.deleteCitations(user, groupId, citationIdList))
                .thenReturn(deletionResponse);

        AsyncDeleteCitationsResponse actualResponse = managerToTest.deleteCitations(user, groupId, citationIdList);
        Assert.assertEquals(AsyncTaskStatus.PENDING, actualResponse.getTaskStatus());

        AsyncDeleteCitationsResponse deletionStatus = managerToTest
                .getDeleteCitationsResponse(actualResponse.getTaskID());
        Assert.assertEquals(AsyncTaskStatus.COMPLETE, deletionStatus.getTaskStatus());
        for (String citationId : citationIdList) {
            Assert.assertTrue(deletionStatus.getResponse().get(ItemDeletionResponse.SUCCESS).contains(citationId));
        }
    }

    @Test
    public void test_deleteCitations_libraryLocked()
            throws ZoteroConnectionException, ZoteroHttpStatusException, ExecutionException, InterruptedException {
        Map<ItemDeletionResponse, List<String>> responseMap = new HashMap<>();
        responseMap.put(ItemDeletionResponse.LIBRARY_LOCKED, new ArrayList<>(citationIdList));
        AsyncResult<Map<ItemDeletionResponse, List<String>>> deletionResponse = new AsyncResult<Map<ItemDeletionResponse, List<String>>>(
                responseMap);

        Mockito.when(asyncDeleteCitationsProcessor.deleteCitations(user, groupId, citationIdList))
                .thenReturn(deletionResponse);

        AsyncDeleteCitationsResponse actualResponse = managerToTest.deleteCitations(user, groupId, citationIdList);
        Assert.assertEquals(AsyncTaskStatus.PENDING, actualResponse.getTaskStatus());

        AsyncDeleteCitationsResponse deletionStatus = managerToTest
                .getDeleteCitationsResponse(actualResponse.getTaskID());
        Assert.assertEquals(AsyncTaskStatus.COMPLETE, deletionStatus.getTaskStatus());
        for (String citationId : citationIdList) {
            Assert.assertTrue(
                    deletionStatus.getResponse().get(ItemDeletionResponse.LIBRARY_LOCKED).contains(citationId));
        }
    }
    
    @Test
    public void test_deleteCitations_mixed()
            throws ZoteroConnectionException, ZoteroHttpStatusException, ExecutionException, InterruptedException {
        Map<ItemDeletionResponse, List<String>> responseMap = new HashMap<>();
        
        List<String> successList = new ArrayList<>();
        successList.add(citationId1);
        responseMap.put(ItemDeletionResponse.SUCCESS, new ArrayList<>(successList));
        
        List<String> libraryVersionChangedList = new ArrayList<>();
        libraryVersionChangedList.add(citationId2);
        responseMap.put(ItemDeletionResponse.LIBRARY_VERSION_CHANGED, new ArrayList<>(libraryVersionChangedList));
        
        AsyncResult<Map<ItemDeletionResponse, List<String>>> deletionResponse = new AsyncResult<Map<ItemDeletionResponse, List<String>>>(
                responseMap);

        Mockito.when(asyncDeleteCitationsProcessor.deleteCitations(user, groupId, citationIdList))
                .thenReturn(deletionResponse);

        AsyncDeleteCitationsResponse actualResponse = managerToTest.deleteCitations(user, groupId, citationIdList);
        Assert.assertEquals(AsyncTaskStatus.PENDING, actualResponse.getTaskStatus());

        AsyncDeleteCitationsResponse deletionStatus = managerToTest
                .getDeleteCitationsResponse(actualResponse.getTaskID());
        Assert.assertEquals(AsyncTaskStatus.COMPLETE, deletionStatus.getTaskStatus());
        
        for (String citationId : successList) {
            Assert.assertTrue(deletionStatus.getResponse().get(ItemDeletionResponse.SUCCESS).contains(citationId));
        }
        for (String citationId : libraryVersionChangedList) {
            Assert.assertTrue(deletionStatus.getResponse().get(ItemDeletionResponse.LIBRARY_VERSION_CHANGED).contains(citationId));
        }
    }

}
