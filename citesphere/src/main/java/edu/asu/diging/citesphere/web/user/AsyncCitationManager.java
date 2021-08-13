package edu.asu.diging.citesphere.web.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.zotero.api.ItemDeletionResponse;
import org.springframework.social.zotero.api.ZoteroUpdateItemsStatuses;
import org.springframework.social.zotero.exception.ZoteroConnectionException;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;

import edu.asu.diging.citesphere.core.exceptions.CitationIsOutdatedException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroHttpStatusException;
import edu.asu.diging.citesphere.core.service.ICitationStore;
import edu.asu.diging.citesphere.core.service.impl.AsyncDeleteCitationsProcessor;
import edu.asu.diging.citesphere.core.service.impl.AsyncUpdateCitationsProcessor;
import edu.asu.diging.citesphere.model.bib.ICitation;
import edu.asu.diging.citesphere.user.IUser;

/**
 * Responsible for managing the state of an async citation tasks.
 *
 */
@Component
public class AsyncCitationManager {

    @Autowired
    private AsyncUpdateCitationsProcessor asyncUpdateCitationsProcessor;

    @Autowired
    private AsyncDeleteCitationsProcessor asyncDeleteCitationsProcessor;

    @Autowired
    private ICitationStore citationStore;

    private final Map<String, Future<ZoteroUpdateItemsStatuses>> updateTaskTracker;
    private final Map<String, Future<Map<ItemDeletionResponse, List<String>>>> deleteTaskTracker;

    public AsyncCitationManager() {
        this.updateTaskTracker = new ConcurrentHashMap<>();
        this.deleteTaskTracker = new ConcurrentHashMap<>();
    }

    /**
     * This method updates citations asynchronously.
     * 
     * @param user          User accessing Zotero
     * @param zoteroGroupId GroupId of the citations
     * @param citations     citations that have to be updated
     * @return AsyncUpdateCitationsResponse contains task id, response and status.
     */
    public AsyncUpdateCitationsResponse updateCitations(IUser user, String zoteroGroupId, List<ICitation> citations)
            throws JsonProcessingException, ZoteroConnectionException, CitationIsOutdatedException,
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

    /**
     * This method gets the response of an update citations request by giving task
     * id. The task id is generated when a new async task is submitted using
     * updateCitations() method in this class
     * 
     * @param taskId id of the task
     * @return AsyncUpdateCitationsResponse returns task status (complete or
     *         pending), task id and task response
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
     * If you no longer need a task in memory, use this method to remove that task
     * to free memory. Once the task is removed, its corresponding result can no
     * longer be retrieved.
     * 
     * @param taskId id of the task that is not needed in memory
     */
    public void clearUpdateTask(String taskId) {
        updateTaskTracker.remove(taskId);
    }

    /**
     * Deletes Citations asynchronously
     * 
     * @param user           User accessing Zotero
     * @param groupId        GroupId of the Citations
     * @param citationIdList List of the Citations to be deleted
     * @return AsyncDeleteCitationsResponse containing taskId and its current status
     */
    public AsyncDeleteCitationsResponse deleteCitations(IUser user, String groupId, List<String> citationIdList)
            throws ZoteroConnectionException, ZoteroHttpStatusException {
        String taskId = UUID.randomUUID().toString();
        Future<Map<ItemDeletionResponse, List<String>>> futureTask = asyncDeleteCitationsProcessor.deleteCitations(user,
                groupId, citationIdList);
        deleteTaskTracker.put(taskId, futureTask);
        AsyncDeleteCitationsResponse asyncResponse = new AsyncDeleteCitationsResponse();
        asyncResponse.setTaskID(taskId);
        asyncResponse.setTaskStatus(AsyncTaskStatus.PENDING);
        return asyncResponse;
    }

    /**
     * Gets the current status of the deletion task. If the task is completed, it
     * returns the status for each Citation which was requested to be deleted.
     * 
     * @param taskId Id of the task
     * @return AsyncDeleteCitationsResponse containing task id, task status, and
     *         response if the task is completed
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

    /**
     * Removes the Citation deletion task from memory and removes the deleted
     * Citations from index and local database
     * 
     * @param taskId Id of the task
     */
    public void clearDeleteTask(String taskId) throws InterruptedException, ExecutionException {
        Map<ItemDeletionResponse, List<String>> deletionResponse = deleteTaskTracker.remove(taskId).get();
        for (String itemKey : deletionResponse.getOrDefault(ItemDeletionResponse.SUCCESS, new ArrayList<>())) {
            Optional<ICitation> citation = citationStore.findById(itemKey);
            if (citation.isPresent()) {
                citationStore.delete(citation.get());
            }
        }
    }

}