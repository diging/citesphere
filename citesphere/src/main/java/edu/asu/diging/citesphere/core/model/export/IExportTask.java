package edu.asu.diging.citesphere.core.model.export;

import edu.asu.diging.citesphere.core.export.ExportType;

public interface IExportTask {

    String getId();

    void setId(String id);

    String getUsername();

    void setUsername(String username);

    long getTotalRecords();

    void setTotalRecords(long totalRecords);

    long getProgress();

    void setProgress(long progress);

    ExportStatus getStatus();

    void setStatus(ExportStatus status);

    void setExportType(ExportType exportType);

    ExportType getExportType();

}