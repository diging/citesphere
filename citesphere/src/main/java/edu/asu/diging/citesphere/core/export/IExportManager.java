package edu.asu.diging.citesphere.core.export;

import edu.asu.diging.citesphere.core.exceptions.ExportFailedException;
import edu.asu.diging.citesphere.core.exceptions.ExportTypeNotSupportedException;
import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.model.IUser;

public interface IExportManager {

    void export(ExportType exportType, IUser user, String groupId) throws GroupDoesNotExistException, ExportTypeNotSupportedException, ExportFailedException;

}