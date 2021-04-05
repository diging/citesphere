package edu.asu.diging.citesphere.web.user;


public class AsyncTaskResponse<T> {
    private AsyncTaskStatus taskStatus;
    private T response;
    private String taskID;

    public AsyncTaskStatus getTaskStatus() {
        return taskStatus;
    }

    public T getResponse() {
        return response;
    }

    public void setTaskStatus(AsyncTaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public void setResponse(T response) {
        this.response = response;
    } 
    
    public void setTaskID(String taskID) {
        this.taskID = taskID;
    }
    
    public String getTaskID() {
        return taskID;
    }
}
