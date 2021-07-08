package edu.asu.diging.citesphere.core.model.jobs.impl;

import javax.persistence.Entity;

import edu.asu.diging.citesphere.core.model.jobs.IUploadFileJob;

@Entity
public class UploadFileJob extends Job implements IUploadFileJob {

    private String filename;
    private long fileSize;
    private String contentType;
    private String citationGroup;
    private String itemKey;
    
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.jobs.impl.IUploadFileJob#getFilename()
     */
    @Override
    public String getFilename() {
        return filename;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.jobs.impl.IUploadFileJob#setFilename(java.lang.String)
     */
    @Override
    public void setFilename(String filename) {
        this.filename = filename;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.jobs.impl.IUploadFileJob#getFileSize()
     */
    @Override
    public long getFileSize() {
        return fileSize;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.jobs.impl.IUploadFileJob#setFileSize(long)
     */
    @Override
    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.jobs.impl.IUploadFileJob#getContentType()
     */
    @Override
    public String getContentType() {
        return contentType;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.jobs.impl.IUploadFileJob#setContentType(java.lang.String)
     */
    @Override
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.jobs.impl.IUploadFileJob#getCitationGroup()
     */
    @Override
    public String getCitationGroup() {
        return citationGroup;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.jobs.impl.IUploadFileJob#setCitationGroup(java.lang.String)
     */
    @Override
    public void setCitationGroup(String citationGroup) {
        this.citationGroup = citationGroup;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.jobs.impl.IUploadFileJob#getItemKey()
     */
    @Override
    public String getItemKey() {
        return itemKey;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.jobs.impl.IUploadFileJob#setItemKey(java.lang.String)
     */
    @Override
    public void setItemKey(String itemKey) {
        this.itemKey = itemKey;
    }
}
