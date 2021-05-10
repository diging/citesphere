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
import edu.asu.diging.citesphere.core.service.impl.AsyncCitationManager;
import edu.asu.diging.citesphere.model.bib.ICitation;
import edu.asu.diging.citesphere.user.IUser;

@Component
public class AsyncUpdateCitationsProcessor {
    @Autowired
    private AsyncCitationManager asyncCitationManager;

    private final Map<String, Future<ZoteroUpdateItemsStatuses>> taskTracker;

    public AsyncUpdateCitationsProcessor() {
        this.taskTracker = new ConcurrentHashMap<>();
    }

    /**
     * This method updates citations.
     * 
     * @param user          User accessing Zotero
     * @param zoteroGroupId GroupId of the citations
     * @param citations     citations that have to be updated
     * @return returns AsyncUpdateCitationsResponse object that contains task id,
     *         response and status.
     */
    public AsyncUpdateCitationsResponse updateCitations(IUser user, String zoteroGroupId, List<ICitation> citations)
            throws JsonProcessingException, ZoteroConnectionException, CitationIsOutdatedException,
            ZoteroHttpStatusException, InterruptedException, ExecutionException {
        String taskID = UUID.randomUUID().toString();
        Future<ZoteroUpdateItemsStatuses> futureTask = asyncCitationManager.updateCitations(user, zoteroGroupId,
                citations);
        taskTracker.put(taskID, futureTask);
        AsyncUpdateCitationsResponse asyncResponse = new AsyncUpdateCitationsResponse();
        asyncResponse.setTaskID(taskID);
        asyncResponse.setTaskStatus(AsyncTaskStatus.PENDING);
        return asyncResponse;
    }

    /**
     * This method gets response of a update citations request by giving task id.
     * 
     * @param taskID: id of the task
     * @return: returns AsyncTaskResponse that has task status, task id and task
     *          response
     */
    public AsyncUpdateCitationsResponse getUpdateCitationsResponse(String taskID)
            throws ExecutionException, InterruptedException {
        AsyncUpdateCitationsResponse response = new AsyncUpdateCitationsResponse();
        Future<ZoteroUpdateItemsStatuses> futureTask = taskTracker.get(taskID);
        response.setTaskID(taskID);
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
     * to free memory.
     * 
     * @param taskID : id of the task that is not needed in memory
     */
    public void clearTask(String taskID) {
        taskTracker.remove(taskID);
    }

}