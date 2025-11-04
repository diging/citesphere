package edu.asu.diging.citesphere.web.user;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.HttpClientErrorException;

import edu.asu.diging.citesphere.core.service.ICitationManager;
import edu.asu.diging.citesphere.core.service.giles.IGilesConnector;
import edu.asu.diging.citesphere.model.bib.ICitation;
import edu.asu.diging.citesphere.model.bib.IGilesUpload;
import edu.asu.diging.citesphere.model.bib.impl.GilesFile;
import edu.asu.diging.citesphere.model.bib.impl.GilesPage;
import edu.asu.diging.citesphere.user.IUser;

@Controller
public class GilesDocumentController {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private IGilesConnector gilesConnector;
    
    @Autowired
    private ICitationManager citationManager;

    @RequestMapping(value="/auth/group/{zoteroGroupId}/items/{itemId}/giles/{fileId}")
    public String get(HttpServletResponse response, @PathVariable String itemId, @PathVariable String fileId, Authentication authentication, Model model) {
        
        ICitation citation = citationManager.getCitation(itemId);
        if (citation == null) {
            response.setStatus(org.apache.http.HttpStatus.SC_NOT_FOUND);
            return "error/404";
        }
        
        // Find the upload and file info
        FileInfo fileInfo = findFileInfo(citation, fileId);
        if (fileInfo == null) {
            response.setStatus(org.apache.http.HttpStatus.SC_NOT_FOUND);
            return "error/404";
        }
        
        byte[] content = null;
        
        try {
            content = gilesConnector.getFile((IUser)authentication.getPrincipal(), fileId);
        } catch (HttpClientErrorException.NotFound ex) {
            logger.error("This file is not available. Maybe you uploaded it with a different Citesphere instance?", ex);
            return "error/gilesDocumentError";
        }
        
        response.setContentType(fileInfo.contentType);
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileInfo.filename + "\"");
        try {
            if (content != null) {
                response.setContentLength(content.length);
                response.getOutputStream().write(content);
                response.getOutputStream().close();
            }
        } catch (IOException e) {
            logger.error("Could not write file.", e);
        }
        return null;
    }
    
    private FileInfo findFileInfo(ICitation citation, String fileId) {
        for (IGilesUpload upload : citation.getGilesUploads()) {
            FileInfo fileInfo = getFileInfo(upload, fileId);
            if (fileInfo != null) {
                return fileInfo;
            }
        }
        return null;
    }
    
    private FileInfo getFileInfo(IGilesUpload upload, String fileId) {
        // Check main uploaded file
        if (upload.getUploadedFile() != null && fileId.equals(upload.getUploadedFile().getId())) {
            return new FileInfo(upload.getUploadedFile().getFilename(), upload.getUploadedFile().getContentType());
        }
        
        // Check extracted text
        if (upload.getExtractedText() != null && fileId.equals(upload.getExtractedText().getId())) {
            return new FileInfo(upload.getExtractedText().getFilename(), upload.getExtractedText().getContentType());
        }
        
        // Check page files
        if (upload.getPages() != null) {
            for (GilesPage page : upload.getPages()) {
                if (page.getImage() != null && fileId.equals(page.getImage().getId())) {
                    return new FileInfo(page.getImage().getFilename(), page.getImage().getContentType());
                }
                if (page.getText() != null && fileId.equals(page.getText().getId())) {
                    return new FileInfo(page.getText().getFilename(), page.getText().getContentType());
                }
                if (page.getOcr() != null && fileId.equals(page.getOcr().getId())) {
                    return new FileInfo(page.getOcr().getFilename(), page.getOcr().getContentType());
                }
                
                if (page.getAdditionalFiles() != null) {
                    for (GilesFile additionalFile : page.getAdditionalFiles()) {
                        if (additionalFile != null && fileId.equals(additionalFile.getId())) {
                            return new FileInfo(additionalFile.getFilename(), additionalFile.getContentType());
                        }
                    }
                }
            }
        }
        
        return null;
    }
    
    private static class FileInfo {
        final String filename;
        final String contentType;
        
        FileInfo(String filename, String contentType) {
            this.filename = filename != null ? filename : "download";
            this.contentType = contentType;
        }
    }
}



