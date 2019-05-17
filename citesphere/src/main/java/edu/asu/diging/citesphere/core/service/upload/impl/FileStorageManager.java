package edu.asu.diging.citesphere.core.service.upload.impl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import edu.asu.diging.citesphere.core.exceptions.FileStorageException;
import edu.asu.diging.citesphere.core.service.upload.IFileStorageManager;

@Service
@PropertySource("classpath:/config.properties")
public class FileStorageManager implements IFileStorageManager {

    @Value("${_upload_files_directory}")
    private String baseDirectory;

    @PostConstruct
    public void init() {
        if (!baseDirectory.endsWith(File.separator)) {
            baseDirectory = baseDirectory + File.separator;
        }
    }

    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.service.upload.impl.IFileStorageManager#saveFile(java.lang.String, java.lang.String, java.lang.String, byte[])
     */
    @Override
    public void saveFile(String username, String jobId, String filename, byte[] bytes) throws FileStorageException {
        String filePath = getAndCreateStoragePath(username, jobId);

        File file = new File(filePath + File.separator + filename);
        BufferedOutputStream stream;
        try {
            stream = new BufferedOutputStream(new FileOutputStream(file));
        } catch (FileNotFoundException e) {
            throw new FileStorageException("Could not store file.", e);
        }
        try {
            stream.write(bytes);
            stream.close();
        } catch (IOException e) {
            throw new FileStorageException("Could not store file.", e);
        }
    }

    private String getAndCreateStoragePath(String username, String jobId) {
        String path = baseDirectory + File.separator + jobId;
        File dirFile = new File(path);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        return path;
    }

    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.service.upload.impl.IFileStorageManager#getFolderPath(java.lang.String, java.lang.String)
     */
    @Override
    public String getFolderPath(String username, String jobId) {
        StringBuffer path = new StringBuffer(baseDirectory);

        path.append(username);
        if (jobId == null) {
            return path.toString();
        }

        path.append(File.separator); 
        path.append(jobId);
        return path.toString();
    }

    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.service.upload.impl.IFileStorageManager#deleteFile(java.lang.String, java.lang.String, java.lang.String, boolean)
     */
    @Override
    public boolean deleteFile(String username, String jobId, String filename,
            boolean deleteEmptyFolders) {
        String path = baseDirectory + File.separator + username + File.separator + jobId;
        File file = new File(path + File.separator + filename);

        if (file.exists()) {
            file.delete();
        }

        if (deleteEmptyFolders) {
            File docFolder = new File(path);
            if (docFolder.isDirectory() && docFolder.list().length == 0) {
                docFolder.delete();
            }
        }

        return true;
    }

}