package edu.asu.diging.citesphere.core.model.jobs;

import edu.asu.diging.citesphere.model.bib.ICitationGroup;

public interface IUploadCollectionJob extends IJob {

    String getFilename();

    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.jobs.impl.IUploadJob#setFilename(java.lang.String)
     */
    void setFilename(String filename);

    long getFileSize();

    void setFileSize(long fileSize);

    String getContentType();

    void setContentType(String contentType);

    String getCitationGroup();

    void setCitationGroup(String citationGroup);

    ICitationGroup getCitationGroupDetail();

    void setCitationGroupDetail(ICitationGroup citationGroupDetail);

}