package edu.asu.diging.citesphere.core.service.giles;

import org.springframework.web.multipart.MultipartFile;

import edu.asu.diging.citesphere.model.bib.IGilesUpload;
import edu.asu.diging.citesphere.user.IUser;

public interface GilesUploadService {

    IGilesUpload uploadFile(IUser user, MultipartFile file, byte[] fileBytes);

}