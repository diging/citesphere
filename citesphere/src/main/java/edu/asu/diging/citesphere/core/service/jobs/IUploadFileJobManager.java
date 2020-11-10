package edu.asu.diging.citesphere.core.service.jobs;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.model.jobs.IUploadFileJob;
import edu.asu.diging.citesphere.user.IUser;

public interface IUploadFileJobManager {

    List<IUploadFileJob> createUploadJob(IUser user, MultipartFile[] files, List<byte[]> fileBytes, String groupId,
            String itemKey) throws GroupDoesNotExistException;

}