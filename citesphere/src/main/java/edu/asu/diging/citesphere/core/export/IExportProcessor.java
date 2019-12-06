package edu.asu.diging.citesphere.core.export;

import edu.asu.diging.citesphere.core.exceptions.ExportFailedException;
import edu.asu.diging.citesphere.core.exceptions.ExportTypeNotSupportedException;
import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroHttpStatusException;
import edu.asu.diging.citesphere.core.model.IUser;
import edu.asu.diging.citesphere.core.model.export.IExportTask;

public interface IExportProcessor {

    void runExport(ExportType exportType, IUser user, String groupId, String collectionId, IExportTask task, ExportFinishedCallback callback)
            throws GroupDoesNotExistException, ExportTypeNotSupportedException, ExportFailedException, ZoteroHttpStatusException;

}