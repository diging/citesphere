package edu.asu.diging.citesphere.web.user.export;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.asu.diging.citesphere.core.exceptions.DownloadExportException;
import edu.asu.diging.citesphere.core.export.IExportJobManager;
import edu.asu.diging.citesphere.core.export.IExportManager;
import edu.asu.diging.citesphere.core.export.IExportTaskManager;
import edu.asu.diging.citesphere.core.model.export.IExportTask;
import edu.asu.diging.citesphere.core.model.jobs.IExportJob;
import edu.asu.diging.citesphere.core.service.upload.IFileStorageManager;

@Controller
public class DownloadExportController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private IFileStorageManager storageManager;

    @Autowired
    private IExportTaskManager exportTaskManager;

    @Autowired
    private IExportJobManager exportJobManager;

    @Autowired
    private IExportManager exportManager;

    @RequestMapping(value = "/auth/exports/{id}/download")
    public void download(@PathVariable("id") String id, Authentication authentication, HttpServletResponse response) {
        IExportTask task = exportTaskManager.get(id);
        if (!task.getUsername().equals(authentication.getName())) {
            return;
        }

        IExportJob job = exportJobManager.findByTaskId(task.getId());
        String exportFile;
        String filename;
        if (job != null) {
            try {
                exportFile = exportManager.getDistributedExportResult(task);
                filename = new File(exportFile).getName();
            } catch (DownloadExportException e) {
                logger.error("Could not download file from Citesphere Exporter.", e);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return;
            }
        } else {
            filename = task.getFilename();
            exportFile = storageManager.getFolderPath(authentication.getName(), id);
        }
        
        if (exportFile == null || exportFile.trim().isEmpty() || filename == null || filename.trim().isEmpty()) {
            logger.error("Could not find file to downlaod.");
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        
        try {
            InputStream is = new ByteArrayInputStream(
                    storageManager.getFileContentFromUrl(new URL(("file:" + exportFile))));
            response.setHeader("Content-disposition", "attachment; filename=" + filename);
            IOUtils.copy(is, response.getOutputStream());
            response.flushBuffer();
            return;
        } catch (IOException ex) {
            logger.info("Error writing file to output stream. Filename was '{}'", filename, ex);
            throw new RuntimeException("IOError writing file to output stream");
        }
    }

}
