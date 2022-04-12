package edu.asu.diging.citesphere.web.user;

import java.awt.TrayIcon.MessageType;
import java.io.IOException;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.asu.diging.citesphere.core.service.ICitationManager;
import edu.asu.diging.citesphere.core.service.giles.IGilesConnector;
import edu.asu.diging.citesphere.model.bib.GilesStatus;
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
    public void get(HttpServletResponse response, @PathVariable String itemId, @PathVariable String fileId, Authentication authentication) {
        
        ICitation citation = citationManager.getCitation(itemId);
        Optional<IGilesUpload> uploadOptional = citation.getGilesUploads().stream().filter(u -> u.getUploadedFile() != null && u.getDocumentStatus().equals(GilesStatus.COMPLETE))
                .filter(g -> g.getUploadedFile().getId().equals(fileId) //Find fileId in uploaded file and extracted text file
                        || g.getExtractedText().getId().equals(fileId)
                        || g.getPages().stream().filter(p -> p!=null).anyMatch(a -> a.getImage().getId().equals(fileId)  //Find fileId in pages
                        || a.getText().getId().equals(fileId) 
                        || a.getOcr().getId().equals(fileId)
                        || a.getAdditionalFiles().stream().filter(f -> f!=null).anyMatch(f -> f.getId().equals(fileId)))).findFirst(); //Find fileId in additional files
        if (!uploadOptional.isPresent()) {
            response.setStatus(org.apache.http.HttpStatus.SC_NOT_FOUND);
            return;
        }
        
        IGilesUpload upload = uploadOptional.get();
        
        byte[] content = gilesConnector.getFile((IUser)authentication.getPrincipal(), fileId);
        
        response.setContentType(upload.getUploadedFile().getContentType());
        response.setHeader("Content-disposition", "filename=\"" + upload.getUploadedFile().getFilename() + "\""); 
        try {
            if (content != null) {
                response.setContentLength(content.length);
                response.getOutputStream().write(content);
                response.getOutputStream().close();
            }
        } catch (IOException e) {
            logger.error("Could not write file.", e);
        }

    }
}
