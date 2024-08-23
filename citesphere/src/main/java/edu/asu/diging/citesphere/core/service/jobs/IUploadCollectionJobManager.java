package edu.asu.diging.citesphere.core.service.jobs;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.model.jobs.IUploadCollectionJob;
import edu.asu.diging.citesphere.user.IUser;

public interface IUploadCollectionJobManager {
    
    List<IUploadCollectionJob> createUploadJob(IUser user, MultipartFile[] files, List<byte[]> fileBytes, String groupId) throws GroupDoesNotExistException;


}
