package edu.asu.diging.citesphere.core.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.social.zotero.api.ItemDeletionResponse;
import org.springframework.social.zotero.api.ZoteroUpdateItemsStatuses;
import org.springframework.social.zotero.exception.ZoteroConnectionException;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;

import edu.asu.diging.citesphere.core.exceptions.CitationIsOutdatedException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroHttpStatusException;
import edu.asu.diging.citesphere.core.service.IAsyncCitationManager;
import edu.asu.diging.citesphere.core.service.impl.async.AsyncDeleteCitationsResponse;
import edu.asu.diging.citesphere.core.service.impl.async.AsyncUpdateCitationsResponse;
import edu.asu.diging.citesphere.model.bib.ICitation;
import edu.asu.diging.citesphere.user.IUser;
import edu.asu.diging.citesphere.web.user.AsyncTaskStatus;

@Component
public class AsyncCitationManager implements IAsyncCitationManager {

    @Autowired
    private AsyncUpdateCitationsProcessor asyncUpdateCitationsProcessor;

    @Autowired
    private AsyncDeleteCitationsProcessor asyncDeleteCitationsProcessor;
    
    @Autowired
    private CitationManager citationManager;

    private final Map<String, Future<ZoteroUpdateItemsStatuses>> updateTaskTracker;
    private final Map<String, Future<Map<ItemDeletionResponse, List<String>>>> deleteTaskTracker;
    private final Map<String, Long> deleteTaskTimestamps;
    private HashSet<String> hiddenItemsSet;
    
    @Value("${task_cleanup_cycle}")
    private long cleanupCycle;

    public AsyncCitationManager() {
        this.updateTaskTracker = new ConcurrentHashMap<>();
        this.deleteTaskTracker = new ConcurrentHashMap<>();
        this.deleteTaskTimestamps = new ConcurrentHashMap<>();
        this.hiddenItemsSet = new HashSet<>();
    }

    public HashSet<String> getHiddenItemsList() {
        return hiddenItemsSet;
    }

    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.service.IAsyncCitationManager#updateCitations(edu.asu.diging.citesphere.user.IUser, java.lang.String, java.util.List)
     */
    @Override
    public AsyncUpdateCitationsResponse updateCitations(IUser user, String zoteroGroupId, List<ICitation> citations) throws JsonProcessingException, ZoteroConnectionException, CitationIsOutdatedException,
            ZoteroHttpStatusException, InterruptedException, ExecutionException {
        String taskId = UUID.randomUUID().toString();
        Future<ZoteroUpdateItemsStatuses> futureTask = asyncUpdateCitationsProcessor.updateCitations(user,
                zoteroGroupId, citations);
        updateTaskTracker.put(taskId, futureTask);
        AsyncUpdateCitationsResponse asyncResponse = new AsyncUpdateCitationsResponse();
        asyncResponse.setTaskID(taskId);
        asyncResponse.setTaskStatus(AsyncTaskStatus.PENDING);
        return asyncResponse;
    }

    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.service.IAsyncCitationManager#getUpdateCitationsResponse(java.lang.String)
     */
    public AsyncUpdateCitationsResponse getUpdateCitationsResponse(String taskId)
            throws ExecutionException, InterruptedException {
        AsyncUpdateCitationsResponse response = new AsyncUpdateCitationsResponse();
        Future<ZoteroUpdateItemsStatuses> futureTask = updateTaskTracker.get(taskId);
        response.setTaskID(taskId);
        if (futureTask.isDone()) {
            response.setTaskStatus(AsyncTaskStatus.COMPLETE);
            response.setResponse(futureTask.get());
        } else {
            response.setTaskStatus(AsyncTaskStatus.PENDING);
        }
        return response;
    }

    /**
     * Removes the finished deletion tasks every 24 hrs
     */
    @Scheduled(fixedDelayString  = "${task_cleanup_cycle}")
    public void scheduledTaskCleanup() {
        Long currentTime = System.currentTimeMillis();
        for (String task : deleteTaskTracker.keySet()) {
            if (currentTime
                    - deleteTaskTimestamps.getOrDefault(task, currentTime - cleanupCycle - 1) > cleanupCycle
                    && deleteTaskTracker.get(task).isDone()) {
                deleteTaskTracker.remove(task);
                deleteTaskTimestamps.remove(task);
            }
        }
    }
    
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.service.IAsyncCitationManager#clearUpdateTask(java.lang.String)
     */
    public void clearUpdateTask(String taskId) {
        updateTaskTracker.remove(taskId);
    }

    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.service.IAsyncCitationManager#deleteCitations(edu.asu.diging.citesphere.user.IUser, java.lang.String, java.util.List)
     */
    public AsyncDeleteCitationsResponse deleteCitations(IUser user, String groupId, List<String> citationIdList)
            throws ZoteroConnectionException, ZoteroHttpStatusException {
        String taskId = UUID.randomUUID().toString();
        Future<Map<ItemDeletionResponse, List<String>>> futureTask = asyncDeleteCitationsProcessor.deleteCitations(user,
                groupId, citationIdList);
        deleteTaskTracker.put(taskId, futureTask);
        deleteTaskTimestamps.put(taskId, System.currentTimeMillis());
        AsyncDeleteCitationsResponse asyncResponse = new AsyncDeleteCitationsResponse();
        asyncResponse.setTaskID(taskId);
        asyncResponse.setTaskStatus(AsyncTaskStatus.PENDING);
        return asyncResponse;
    }

    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.service.IAsyncCitationManager#getDeleteCitationsResponse(java.lang.String)
     */
    public AsyncDeleteCitationsResponse getDeleteCitationsResponse(String taskId)
            throws ExecutionException, InterruptedException {
        AsyncDeleteCitationsResponse response = new AsyncDeleteCitationsResponse();
        Future<Map<ItemDeletionResponse, List<String>>> futureTask = deleteTaskTracker.get(taskId);
        response.setTaskID(taskId);
        if (futureTask.isDone()) {
            response.setTaskStatus(AsyncTaskStatus.COMPLETE);
            response.setResponse(futureTask.get());
        } else {
            response.setTaskStatus(AsyncTaskStatus.PENDING);
        }
        return response;
    }
    
    
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.service.IAsyncCitationManager#moveCitationsToCollection(IUser, String, List)
     */
    public AsyncUpdateCitationsResponse moveCitationsToCollection(IUser user, String zoteroGroupId, List<ICitation> citations)
            throws JsonProcessingException, ZoteroConnectionException, CitationIsOutdatedException,
            ZoteroHttpStatusException, InterruptedException, ExecutionException {
        String taskId = UUID.randomUUID().toString();
        Future<ZoteroUpdateItemsStatuses> futureTask = asyncUpdateCitationsProcessor.moveCitationsToCollection(user,
                zoteroGroupId, citations);
        updateTaskTracker.put(taskId, futureTask);
        AsyncUpdateCitationsResponse asyncResponse = new AsyncUpdateCitationsResponse();
        asyncResponse.setTaskID(taskId);
        asyncResponse.setTaskStatus(AsyncTaskStatus.PENDING);
        return asyncResponse;
    }
    
    public AsyncDeleteCitationsResponse hideCitations(IUser user, String groupId, List<String> citationIdList) {
        String taskId = UUID.randomUUID().toString();
        
//        hiddenItemsSet.addAll(citationIdList);
        for(String item : citationIdList) {
            try {
            ICitation citation = citationManager.getCitation(user, groupId, item);
            citation.setHidden(true);
            citationManager.updateCitation(user, groupId, citation);
            }
            catch(Exception ex) {
               
            }
        }
        AsyncDeleteCitationsResponse asyncResponse = new AsyncDeleteCitationsResponse();
        asyncResponse.setTaskID(taskId);
        asyncResponse.setTaskStatus(AsyncTaskStatus.COMPLETE);
        return asyncResponse;
    }

}