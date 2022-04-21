package edu.asu.diging.citesphere.web.user;

import java.awt.TrayIcon.MessageType;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public void get(HttpServletResponse response, @PathVariable String itemId, @PathVariable String fileId, Authentication authentication) {
        
        String contentType = null;
        String fileName = null;
        ICitation citation = citationManager.getCitation(itemId);
        List<IGilesUpload> uploadOptionalList = citation.getGilesUploads().stream().filter(u -> u.getUploadedFile() != null && u.getDocumentStatus().equals(GilesStatus.COMPLETE)).collect(Collectors.toList());

        if(uploadOptionalList.size()!=0) {
            Optional<IGilesUpload> gilesUpload = uploadOptionalList.stream().filter(g -> g.getUploadedFile().getId().equals(fileId)).findFirst();
            
            if(!gilesUpload.isPresent()) {
                for(IGilesUpload gilesUploadedFile : uploadOptionalList) {
                    
                    if(gilesUploadedFile.getExtractedText().getId().equals(fileId)) {
                        contentType = gilesUploadedFile.getExtractedText().getContentType();
                        fileName = gilesUploadedFile.getExtractedText().getFilename();
                        break;
                    }
                    
                    List<GilesPage> gilesPages = gilesUploadedFile.getPages();
                    for(GilesPage gilesPage : gilesPages) {
                        if(gilesPage.getImage().getId().equals(fileId)) {
                            contentType = gilesPage.getImage().getContentType();
                            fileName = gilesPage.getImage().getFilename();
                            break;
                        }
                        else if(gilesPage.getOcr().getId().equals(fileId)) {
                            contentType = gilesPage.getOcr().getContentType();
                            fileName = gilesPage.getOcr().getFilename();
                            break;
                        }
                        else if(gilesPage.getText().getId().equals(fileId)) {
                            contentType = gilesPage.getText().getContentType();
                            fileName = gilesPage.getText().getFilename();
                            break;
                        }
                        else {
                            List<GilesFile> pageAdditionalFiles = gilesPage.getAdditionalFiles();
                            Optional<GilesFile> pageAdditionalFile = pageAdditionalFiles.stream().filter(a -> a.getId().equals(fileId)).findFirst();
                            if(pageAdditionalFile.isPresent()) {
                                contentType = pageAdditionalFile.get().getContentType();
                                fileName = pageAdditionalFile.get().getFilename();
                                break;
                            }
                        }
                    }
                    if(contentType.isEmpty() || contentType==null) {
                        List<GilesFile> additionalFiles = gilesUploadedFile.getAdditionaFiles();
                        Optional<GilesFile> gilesAdditionalFile = additionalFiles.stream().filter(a -> a.getId().equals(fileId)).findFirst();
                  
                        if(gilesAdditionalFile.isPresent()) {
                            contentType = gilesAdditionalFile.get().getContentType();
                            fileName = gilesAdditionalFile.get().getFilename();
                            break;
                        }
                    }
                    
                        
                }
                   
            }
            else {
                contentType = gilesUpload.get().getUploadedFile().getContentType();
                fileName = gilesUpload.get().getUploadedFile().getFilename();
            }
        }
        else if(uploadOptionalList.size()==0 || contentType==null){
            response.setStatus(org.apache.http.HttpStatus.SC_NOT_FOUND);
            return;
        }
        
        byte[] content = gilesConnector.getFile((IUser)authentication.getPrincipal(), fileId);

        response.setContentType(contentType);
        response.setHeader("Content-disposition", "inline; filename=\"" + fileName + "\""); 
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
