package edu.asu.diging.citesphere.core.service.upload;

import edu.asu.diging.citesphere.core.exceptions.FileStorageException;

public interface IFileStorageManager {

    void saveFile(String username, String jobId, String filename, byte[] bytes) throws FileStorageException;

    String getFolderPath(String username, String jobId);

    boolean deleteFile(String username, String jobId, String filename, boolean deleteEmptyFolders);

}