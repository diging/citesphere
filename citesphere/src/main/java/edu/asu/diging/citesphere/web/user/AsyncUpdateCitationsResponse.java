package edu.asu.diging.citesphere.web.user;

import org.springframework.social.zotero.api.ZoteroUpdateItemsStatuses;

public class AsyncUpdateCitationsResponse {
    private AsyncTaskStatus taskStatus;
    private ZoteroUpdateItemsStatuses response;
    private String taskID;

    public AsyncTaskStatus getTaskStatus() {
        return taskStatus;
    }

    public ZoteroUpdateItemsStatuses getResponse() {
        return response;
    }

    public void setTaskStatus(AsyncTaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public void setResponse(ZoteroUpdateItemsStatuses response) {
        this.response = response;
    } 
    
    public void setTaskID(String taskID) {
        this.taskID = taskID;
    }
    
    public String getTaskID() {
        return taskID;
    }
}
