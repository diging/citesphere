package edu.asu.diging.citesphere.core.service.giles;

import edu.asu.diging.citesphere.model.bib.IGilesUpload;
import edu.asu.diging.citesphere.user.IUser;

public interface GilesUploadChecker {

    void add(String uploadKey);

    // in milliseconds (60000ms = 1m)
    void checkUploads();

    void checkUploadStatus(String citationKey);

    void checkFileUploadStatus(String itemId, IUser principal, String fileId);
    
    boolean canReprocess(IGilesUpload upload, IUser user);

}