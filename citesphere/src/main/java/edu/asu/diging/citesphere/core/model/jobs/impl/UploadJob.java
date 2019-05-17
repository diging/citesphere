package edu.asu.diging.citesphere.core.model.jobs.impl;

import javax.persistence.Entity;

import edu.asu.diging.citesphere.core.model.jobs.IUploadJob;

@Entity
public class UploadJob extends Job implements IUploadJob {

    
    private String filename;
    private long fileSize;
    private String contentType;
    
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.jobs.impl.IUploadJob#getFilename()
     */
    @Override
    public String getFilename() {
        return filename;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.jobs.impl.IUploadJob#setFilename(java.lang.String)
     */
    @Override
    public void setFilename(String filename) {
        this.filename = filename;
    }
    public long getFileSize() {
        return fileSize;
    }
    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }
    public String getContentType() {
        return contentType;
    }
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
      
}
