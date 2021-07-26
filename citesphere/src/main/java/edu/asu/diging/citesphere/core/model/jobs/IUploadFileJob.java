package edu.asu.diging.citesphere.core.model.jobs;

public interface IUploadFileJob extends IJob {

    String getFilename();

    void setFilename(String filename);

    long getFileSize();

    void setFileSize(long fileSize);

    String getContentType();

    void setContentType(String contentType);

    String getCitationGroup();

    void setCitationGroup(String citationGroup);

    String getItemKey();

    void setItemKey(String itemKey);

}