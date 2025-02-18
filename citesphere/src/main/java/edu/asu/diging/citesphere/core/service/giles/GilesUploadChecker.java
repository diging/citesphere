package edu.asu.diging.citesphere.core.service.giles;

public interface GilesUploadChecker {

    void add(String uploadKey);

    // in milliseconds (60000ms = 1m)
    void checkUploads();

    void checkUploadStatus(String citationKey);

}