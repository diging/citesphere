package edu.asu.diging.citesphere.core.service.jobs;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import edu.asu.diging.citesphere.core.model.IUser;
import edu.asu.diging.citesphere.core.model.jobs.IUploadJob;

public interface IUploadJobManager {

    IUploadJob findUploadJob(String id);

    List<IUploadJob> createUploadJob(IUser user, MultipartFile[] files, List<byte[]> fileBytes);

}