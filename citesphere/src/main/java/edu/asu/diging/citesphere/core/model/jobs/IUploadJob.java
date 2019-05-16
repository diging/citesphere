package edu.asu.diging.citesphere.core.model.jobs;

public interface IUploadJob {

    String getId();

    void setId(String id);

    String getUsername();

    void setUsername(String username);

    String getFilename();

    void setFilename(String filename);

}