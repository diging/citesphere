package edu.asu.diging.citesphere.core.model.jobs;

public interface IExportJob extends IJob {

    void setTaskId(String taskId);

    String getTaskId();

}