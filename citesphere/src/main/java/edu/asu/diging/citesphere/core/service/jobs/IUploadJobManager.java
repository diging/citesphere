package edu.asu.diging.citesphere.core.service.jobs;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.model.jobs.IJob;
import edu.asu.diging.citesphere.core.model.jobs.IUploadJob;
import edu.asu.diging.citesphere.model.IUser;

public interface IUploadJobManager {

    IUploadJob findUploadJob(String id);

    List<IUploadJob> createUploadJob(IUser user, MultipartFile[] files, List<byte[]> fileBytes, String groupId) throws GroupDoesNotExistException;

    byte[] getUploadedFile(IUploadJob job);

    List<IUploadJob> getUploadJobs(IUser user, int page);

    IUploadJob findUploadJobFullyLoaded(String id);

    IJob findJob(String id);

}