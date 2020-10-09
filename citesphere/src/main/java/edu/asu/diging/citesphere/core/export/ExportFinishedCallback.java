package edu.asu.diging.citesphere.core.export;

import edu.asu.diging.citesphere.core.model.jobs.IExportJob;

public interface ExportFinishedCallback {

    public void exportFinished(String taskId);
    
    public void retryExport(IExportJob job);
}
