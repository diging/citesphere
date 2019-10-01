package edu.asu.diging.citesphere.core.service.jobs;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.model.IUser;
import edu.asu.diging.citesphere.core.model.bib.ICitationGroup;
import edu.asu.diging.citesphere.core.model.jobs.IUploadJob;

public interface IUploadJobManager {

    IUploadJob findUploadJob(String id);

    List<IUploadJob> createUploadJob(IUser user, MultipartFile[] files, List<byte[]> fileBytes, String groupId) throws GroupDoesNotExistException;

    byte[] getUploadedFile(IUploadJob job);

    List<IUploadJob> getUploadJobs(String username, int page);

    IUploadJob findUploadJobFullyLoaded(String id);

    ICitationGroup getCitationGroup(IUploadJob job);

}
