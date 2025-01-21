package edu.asu.diging.citesphere.core.service.giles;

import java.util.Set;

import org.springframework.scheduling.annotation.Scheduled;

import edu.asu.diging.citesphere.model.bib.ICitation;
import edu.asu.diging.citesphere.model.bib.IGilesUpload;
import edu.asu.diging.citesphere.user.IUser;

public interface GilesUploadChecker {

    void add(ICitation upload);

    // in milliseconds (60000ms = 1m)
    void checkUploads();

}