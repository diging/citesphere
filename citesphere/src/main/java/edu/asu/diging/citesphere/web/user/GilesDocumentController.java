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
        
        // Find the upload that contains this file
        IGilesUpload upload = findUploadByFileId(citation, fileId);
        if (upload == null) {
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
        String filename = getFilename(upload, fileId);
        
        response.setContentType(upload.getUploadedFile().getContentType());
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
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
    
    private IGilesUpload findUploadByFileId(ICitation citation, String fileId) {
        for (IGilesUpload upload : citation.getGilesUploads()) {
            if (isFileInUpload(upload, fileId)) {
                return upload;
            }
        }
        return null;
    }
    
    private boolean isFileInUpload(IGilesUpload upload, String fileId) {
        // Check main uploaded file
        if (upload.getUploadedFile() != null && fileId.equals(upload.getUploadedFile().getId())) {
            return true;
        }
        
        // Check extracted text
        if (upload.getExtractedText() != null && fileId.equals(upload.getExtractedText().getId())) {
            return true;
        }
        
        // Check page files
        if (upload.getPages() != null) {
            for (var page : upload.getPages()) {
                if ((page.getImage() != null && fileId.equals(page.getImage().getId())) ||
                    (page.getText() != null && fileId.equals(page.getText().getId())) ||
                    (page.getOcr() != null && fileId.equals(page.getOcr().getId()))) {
                    return true;
                }
                
                if (page.getAdditionalFiles() != null) {
                    for (var additionalFile : page.getAdditionalFiles()) {
                        if (additionalFile != null && fileId.equals(additionalFile.getId())) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    private String getFilename(IGilesUpload upload, String fileId) {
        // Check main uploaded file
        if (upload.getUploadedFile() != null && fileId.equals(upload.getUploadedFile().getId())) {
            return upload.getUploadedFile().getFilename();
        }
        
        // Check extracted text
        if (upload.getExtractedText() != null && fileId.equals(upload.getExtractedText().getId())) {
            return upload.getExtractedText().getFilename();
        }
        
        // Check page files
        if (upload.getPages() != null) {
            for (var page : upload.getPages()) {
                if (page.getImage() != null && fileId.equals(page.getImage().getId())) {
                    return page.getImage().getFilename();
                }
                if (page.getText() != null && fileId.equals(page.getText().getId())) {
                    return page.getText().getFilename();
                }
                if (page.getOcr() != null && fileId.equals(page.getOcr().getId())) {
                    return page.getOcr().getFilename();
                }
                
                if (page.getAdditionalFiles() != null) {
                    for (var additionalFile : page.getAdditionalFiles()) {
                        if (additionalFile != null && fileId.equals(additionalFile.getId())) {
                            return additionalFile.getFilename();
                        }
                    }
                }
            }
        }
        
        return "download"; // fallback filename
    }
}
