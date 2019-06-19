package edu.asu.diging.citesphere.core.model.export.impl;

import java.time.OffsetDateTime;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import edu.asu.diging.citesphere.core.export.ExportType;
import edu.asu.diging.citesphere.core.model.export.ExportStatus;
import edu.asu.diging.citesphere.core.model.export.IExportTask;

@Entity
public class ExportTask implements IExportTask {

    @Id
    @GeneratedValue(generator = "page_id_generator")
    @GenericGenerator(name = "page_id_generator",    
                    parameters = @Parameter(name = "prefix", value = "EX"), 
                    strategy = "edu.asu.diging.citesphere.core.repository.IdGenerator"
            )
    private String id;
    
    private String username;
    private long totalRecords;
    private long progress;
    private ExportType exportType;
    
    private OffsetDateTime createdOn;
    private OffsetDateTime finishedOn;
    private String filename;
    
    @Enumerated(EnumType.STRING)
    private ExportStatus status;

    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.export.impl.IExportTask#getId()
     */
    @Override
    public String getId() {
        return id;
    }

    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.export.impl.IExportTask#setId(java.lang.String)
     */
    @Override
    public void setId(String id) {
        this.id = id;
    }

    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.export.impl.IExportTask#getUsername()
     */
    @Override
    public String getUsername() {
        return username;
    }

    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.export.impl.IExportTask#setUsername(java.lang.String)
     */
    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.export.impl.IExportTask#getTotalRecords()
     */
    @Override
    public long getTotalRecords() {
        return totalRecords;
    }

    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.export.impl.IExportTask#setTotalRecords(long)
     */
    @Override
    public void setTotalRecords(long totalRecords) {
        this.totalRecords = totalRecords;
    }

    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.export.impl.IExportTask#getProgress()
     */
    @Override
    public long getProgress() {
        return progress;
    }

    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.export.impl.IExportTask#setProgress(long)
     */
    @Override
    public void setProgress(long progress) {
        this.progress = progress;
    }

    @Override
    public ExportType getExportType() {
        return exportType;
    }

    @Override
    public void setExportType(ExportType exportType) {
        this.exportType = exportType;
    }

    @Override
    public OffsetDateTime getCreatedOn() {
        return createdOn;
    }

    @Override
    public void setCreatedOn(OffsetDateTime createdOn) {
        this.createdOn = createdOn;
    }

    @Override
    public OffsetDateTime getFinishedOn() {
        return finishedOn;
    }

    @Override
    public void setFinishedOn(OffsetDateTime finishedOn) {
        this.finishedOn = finishedOn;
    }

    @Override
    public String getFilename() {
        return filename;
    }

    @Override
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.export.impl.IExportTask#getStatus()
     */
    @Override
    public ExportStatus getStatus() {
        return status;
    }

    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.export.impl.IExportTask#setStatus(edu.asu.diging.citesphere.core.model.export.ExportStatus)
     */
    @Override
    public void setStatus(ExportStatus status) {
        this.status = status;
    }
    
}
