package edu.asu.diging.citesphere.web.user.export;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.asu.diging.citesphere.core.export.IExportManager;
import edu.asu.diging.citesphere.core.model.export.IExportTask;
import edu.asu.diging.citesphere.core.service.upload.IFileStorageManager;

@Controller
public class DownloadExportController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private IFileStorageManager storageManager;

    @Autowired
    private IExportManager exportManager;

    @RequestMapping(value = "/auth/exports/{id}/download")
    public void download(@PathVariable("id") String id, Authentication authentication, HttpServletResponse response) {
        IExportTask task = exportManager.getTask(id);
        if (!task.getUsername().equals(authentication.getName())) {
            return;
        }
        String path = storageManager.getFolderPath(authentication.getName(), id);
        try {
            InputStream is = new ByteArrayInputStream(storageManager
                    .getFileContentFromUrl(new URL(("file:" + path + File.separator + task.getFilename()))));
            response.setHeader("Content-disposition", "attachment; filename=" + task.getFilename());
            IOUtils.copy(is, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException ex) {
            logger.info("Error writing file to output stream. Filename was '{}'", task.getFilename(), ex);
            throw new RuntimeException("IOError writing file to output stream");
        }
    }
}
