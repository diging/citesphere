package edu.asu.diging.citesphere.core.service.jobs;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.model.jobs.IUploadJob;
import edu.asu.diging.citesphere.user.IUser;

public interface IUploadCollectionJobManager {
    
    List<IUploadJob> createUploadJob(IUser user, MultipartFile[] files, List<byte[]> fileBytes, String groupId, String collectionId) throws GroupDoesNotExistException;


}
