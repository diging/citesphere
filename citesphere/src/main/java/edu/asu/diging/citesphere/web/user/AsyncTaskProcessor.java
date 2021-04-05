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
public class AsyncTaskProcessor<T> {
    private final Map<String, Future<T>> taskTracker;

    public AsyncTaskProcessor() {
        this.taskTracker = new ConcurrentHashMap<>();
    }

    /**
     * Use this method to submit asynchronous task
     * 
     * @param task : task that has to be executed asynchronously
     * @return returns AsyncTaskResponse that has task status, task id and task response
     * @throws Exception
     */
    public AsyncTaskResponse<T> submitTask(Task<T> task) throws Exception {
        String taskID = UUID.randomUUID().toString();
        Future<T> taskOutput = executeAsyncTask(task);
        taskTracker.put(taskID, taskOutput);
        AsyncTaskResponse<T> asyncTaskResponse = new AsyncTaskResponse<T>();
        asyncTaskResponse.setTaskID(taskID);
        asyncTaskResponse.setTaskStatus(AsyncTaskStatus.PENDING);
        return asyncTaskResponse;
    }

    
    /**
     * Use this method to get response of a task by giving task id.
     * 
     * @param taskID: id of the task
     * @return: returns AsyncTaskResponse that has task status, task id and task
     *          response
     * @throws Exception
     */
    public AsyncTaskResponse<T> getResponse(String taskID) throws Exception {
        AsyncTaskResponse<T> response = new AsyncTaskResponse<T>();
        Future<T> taskOutput = taskTracker.get(taskID);
        response.setTaskID(taskID);
        if (taskOutput.isDone()) {
            response.setTaskStatus(AsyncTaskStatus.COMPLETE);
            response.setResponse(taskOutput.get());
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
     * @return returns true of taskID is found and removed.
     */
    public boolean clearTask(String taskID) {
        taskTracker.remove(taskID);
        return true;
    }

    /**
     * This method takes a task that has to be executed asynchronously
     *
     * @param task
     * @return : returns Future object.
     * @throws Exception
     */
    @Async
    private Future<T> executeAsyncTask(Task<T> task) throws Exception {
        return new AsyncResult<T>(task.enact());
    }
}

interface Task<T> {
    T enact() throws Exception;
}