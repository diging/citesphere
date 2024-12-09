package edu.asu.diging.citesphere.core.service.jobs;

import java.util.List;

import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.model.jobs.IImportCrossrefJob;
import edu.asu.diging.citesphere.user.IUser;

public interface IImportCrossrefJobManager {

    IImportCrossrefJob createJob(IUser user, String groupId, List<String> dois) throws GroupDoesNotExistException;

}