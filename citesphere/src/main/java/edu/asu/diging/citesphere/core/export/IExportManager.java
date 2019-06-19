package edu.asu.diging.citesphere.core.export;

import java.util.List;

import edu.asu.diging.citesphere.core.exceptions.ExportFailedException;
import edu.asu.diging.citesphere.core.exceptions.ExportTypeNotSupportedException;
import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.model.IUser;
import edu.asu.diging.citesphere.core.model.export.IExportTask;

public interface IExportManager {

    void export(ExportType exportType, IUser user, String groupId, String collectionId) throws GroupDoesNotExistException, ExportTypeNotSupportedException, ExportFailedException;

    List<IExportTask> getTasks(IUser user, int page);

    IExportTask getTask(String id);

}