package edu.asu.diging.citesphere.web.user;


import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

import javax.inject.Singleton;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

@Singleton
@Component
public class AsyncTaskProcessor<T>  {
    private final Map<String, Future<T>> taskTracker;
    
    public AsyncTaskProcessor() {
        this.taskTracker = new ConcurrentHashMap<>();
    }
    
    public AsyncTaskResponse<T> submitTask(Task<T> task) throws Exception {
        String taskID = UUID.randomUUID().toString();
        Future<T> taskOutput =  executeAsyncTask(task);
        taskTracker.put(taskID, taskOutput);
        AsyncTaskResponse<T> asyncTaskResponse = new AsyncTaskResponse<T>();
        asyncTaskResponse.setTaskID(taskID);
        asyncTaskResponse.setTaskStatus(AsyncTaskStatus.PENDING);
        return asyncTaskResponse;        
    }
    
    public AsyncTaskResponse<T> getResponse(String taskID) throws Exception {
        AsyncTaskResponse<T> response = new AsyncTaskResponse<T>();
        Future<T> taskOutput = taskTracker.get(taskID);
        response.setTaskID(taskID);
        if (taskOutput.isDone()) {
            response.setTaskStatus(AsyncTaskStatus.COMPLETE);
            response.setResponse( taskOutput.get());
        } else {
            response.setTaskStatus(AsyncTaskStatus.PENDING);
        }
        return response;
    }
    
    public boolean clearTask(String taskID) {
        taskTracker.remove(taskID);
        return true;
    }
    
    @Async
    private Future<T> executeAsyncTask(Task<T> task) throws Exception{
        return new AsyncResult<T>(task.enact());
    }
}

interface Task<T> {
    T enact() throws Exception;
}