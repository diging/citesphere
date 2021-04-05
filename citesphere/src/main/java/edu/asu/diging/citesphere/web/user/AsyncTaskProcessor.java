package edu.asu.diging.citesphere.web.user;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.social.zotero.api.ZoteroUpdateItemsStatuses;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class AsyncTaskProcessor<T>  {
    private final Map<String, T> taskTracker;
    
    public AsyncTaskProcessor() {
        this.taskTracker = new ConcurrentHashMap<>();
    }
    
    public AsyncTaskResponse<T> submitTask(Task<T> task) throws Exception {
        String taskID = UUID.randomUUID().toString();
        System.out.println("In submit task");
        System.out.println(taskID);
        executeAsyncTask(task, taskID);
        //taskTracker.put(taskID, taskOutput);
        System.out.println(taskTracker);
        AsyncTaskResponse<T> asyncTaskResponse = new AsyncTaskResponse<T>();
        asyncTaskResponse.setTaskID(taskID);
        asyncTaskResponse.setTaskStatus(AsyncTaskStatus.PENDING);
        return asyncTaskResponse;        
    }
    
    public AsyncTaskResponse<T> getResponse(String taskID) throws Exception {
        AsyncTaskResponse<T> response = new AsyncTaskResponse<T>();
        System.out.println("In get response");
        System.out.println(taskID);
        System.out.println(taskTracker);
        //Future<T> taskOutput = taskTracker.get(taskID);
        //System.out.println(taskOutput+" task output");
        if (taskTracker.containsKey(taskID)) {
            response.setTaskStatus(AsyncTaskStatus.COMPLETE);
            response.setResponse( taskTracker.get(taskID));
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
    private void executeAsyncTask(Task<T> task, String taskID) throws Exception{
        Future<T> futureResponse= new AsyncResult<T>(task.enact());
        while(futureResponse.isDone()) {
            taskTracker.put(taskID, futureResponse.get());
        }
    }
}

interface Task<T> {
    T enact() throws Exception;
}