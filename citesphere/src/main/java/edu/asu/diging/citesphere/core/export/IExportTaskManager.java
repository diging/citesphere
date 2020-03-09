package edu.asu.diging.citesphere.core.export;

import java.util.List;

import edu.asu.diging.citesphere.core.model.export.ExportStatus;
import edu.asu.diging.citesphere.core.model.export.IExportTask;
import edu.asu.diging.citesphere.core.model.jobs.IExportJob;
import edu.asu.diging.citesphere.user.IUser;

public interface IExportTaskManager {

    IExportTask createExportTask(ExportType exportType, String username, ExportStatus status);

    IExportTask saveAndFlush(IExportTask task);

    IExportTask get(String id);

    List<IExportTask> getTasks(IUser user, int page);

    int getTasksTotalPages(IUser user);

    int getTasksTotal(IUser user);

    IExportTask save(IExportTask task);

    void updateTask(IExportJob job);

}