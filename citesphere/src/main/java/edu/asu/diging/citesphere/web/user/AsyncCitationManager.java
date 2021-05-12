package edu.asu.diging.citesphere.web.user;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.zotero.api.ZoteroUpdateItemsStatuses;
import org.springframework.social.zotero.exception.ZoteroConnectionException;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;

import edu.asu.diging.citesphere.core.exceptions.CitationIsOutdatedException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroHttpStatusException;
import edu.asu.diging.citesphere.core.service.impl.AsyncUpdateCitationsProcessor;
import edu.asu.diging.citesphere.model.bib.ICitation;
import edu.asu.diging.citesphere.user.IUser;

/**
 * Responsible for managing the state of an async update citations task.
 *
 */
@Component
public class AsyncCitationManager {
    @Autowired
    private AsyncUpdateCitationsProcessor asyncUpdateCitationsProcessor;

    private final Map<String, Future<ZoteroUpdateItemsStatuses>> taskTracker;

    public AsyncCitationManager() {
        this.taskTracker = new ConcurrentHashMap<>();
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
        taskTracker.put(taskId, futureTask);
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
     * @param taskId: id of the task
     * @return AsyncUpdateCitationsResponse returns task status (complete or
     *         pending), task id and task response
     */
    public AsyncUpdateCitationsResponse getUpdateCitationsResponse(String taskId)
            throws ExecutionException, InterruptedException {
        AsyncUpdateCitationsResponse response = new AsyncUpdateCitationsResponse();
        Future<ZoteroUpdateItemsStatuses> futureTask = taskTracker.get(taskId);
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
     * to free memory. Once the task is removed, its corresponding result can
     * no longer be retrieved.
     * 
     * @param taskId : id of the task that is not needed in memory
     */
    public void clearTask(String taskId) {
        taskTracker.remove(taskId);
    }

}