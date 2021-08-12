package edu.asu.diging.citesphere.web.user;

import java.util.List;
import java.util.Map;

import org.springframework.social.zotero.api.ItemDeletionResponse;

public class AsyncDeleteCitationsResponse {
    private AsyncTaskStatus taskStatus;
    private Map<ItemDeletionResponse, List<String>> response;
    private String taskID;

    public AsyncTaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(AsyncTaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public Map<ItemDeletionResponse, List<String>> getResponse() {
        return response;
    }

    public void setResponse(Map<ItemDeletionResponse, List<String>> response) {
        this.response = response;
    }

    public void setTaskID(String taskID) {
        this.taskID = taskID;
    }

    public String getTaskID() {
        return taskID;
    }
}
