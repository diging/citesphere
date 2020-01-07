package edu.asu.diging.citesphere.core.export;

import java.util.List;

import edu.asu.diging.citesphere.core.exceptions.ExportFailedException;
import edu.asu.diging.citesphere.core.exceptions.ExportTooBigException;
import edu.asu.diging.citesphere.core.exceptions.ExportTypeNotSupportedException;
import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroHttpStatusException;
import edu.asu.diging.citesphere.core.model.export.IExportTask;
import edu.asu.diging.citesphere.model.IUser;

public interface IExportManager {

    void export(ExportType exportType, IUser user, String groupId, String collectionId) throws GroupDoesNotExistException, ExportTypeNotSupportedException, ExportFailedException, ExportTooBigException, ZoteroHttpStatusException;

    List<IExportTask> getTasks(IUser user, int page);

    IExportTask getTask(String id);

    int getTasksTotal(IUser user);

    int getTasksTotalPages(IUser user);

    void distributedExport(ExportType exportType, IUser user, String groupId, String collectionId) throws GroupDoesNotExistException,
            ExportTypeNotSupportedException, ExportFailedException, ExportTooBigException, ZoteroHttpStatusException;

}