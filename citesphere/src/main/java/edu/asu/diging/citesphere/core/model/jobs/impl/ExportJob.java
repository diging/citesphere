package edu.asu.diging.citesphere.core.model.jobs.impl;

import javax.persistence.Entity;

import edu.asu.diging.citesphere.core.model.jobs.IExportJob;

@Entity
public class ExportJob extends Job implements IExportJob {

    private String taskId;

    @Override
    public String getTaskId() {
        return taskId;
    }

    @Override
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
}
