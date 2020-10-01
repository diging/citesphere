package edu.asu.diging.citesphere.core.export;

import edu.asu.diging.citesphere.core.exceptions.AccessForbiddenException;
import edu.asu.diging.citesphere.core.exceptions.DownloadExportException;
import edu.asu.diging.citesphere.core.exceptions.ExportFailedException;
import edu.asu.diging.citesphere.core.exceptions.ExportTooBigException;
import edu.asu.diging.citesphere.core.exceptions.ExportTypeNotSupportedException;
import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroHttpStatusException;
import edu.asu.diging.citesphere.core.model.export.IExportTask;
import edu.asu.diging.citesphere.user.IUser;

public interface IExportManager {

    void export(ExportType exportType, IUser user, String groupId, String collectionId)
            throws GroupDoesNotExistException, ExportTypeNotSupportedException, ExportFailedException,
            ExportTooBigException, ZoteroHttpStatusException, AccessForbiddenException;

    void distributedExport(ExportType exportType, IUser user, String groupId, String collectionId)
            throws GroupDoesNotExistException, ExportTypeNotSupportedException, ExportFailedException,
            ExportTooBigException, ZoteroHttpStatusException, AccessForbiddenException;

    String getDistributedExportResult(IExportTask task) throws DownloadExportException;

}