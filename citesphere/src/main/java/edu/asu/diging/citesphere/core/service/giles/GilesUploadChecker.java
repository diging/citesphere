package edu.asu.diging.citesphere.core.service.giles;

import org.springframework.scheduling.annotation.Scheduled;

import edu.asu.diging.citesphere.model.bib.ICitation;

public interface GilesUploadChecker {

    void add(String uploadKey);

    // in milliseconds (60000ms = 1m)
    void checkUploads();

}