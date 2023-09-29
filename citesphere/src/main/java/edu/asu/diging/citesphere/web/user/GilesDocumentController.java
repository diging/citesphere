package edu.asu.diging.citesphere.web.user;

import java.awt.TrayIcon.MessageType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
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

import com.google.common.base.Supplier;

import java.util.stream.Stream;

import edu.asu.diging.citesphere.core.service.ICitationManager;
import edu.asu.diging.citesphere.core.service.giles.IGilesConnector;
import edu.asu.diging.citesphere.model.bib.GilesStatus;
import edu.asu.diging.citesphere.model.bib.ICitation;
import edu.asu.diging.citesphere.model.bib.IGilesUpload;
import edu.asu.diging.citesphere.model.bib.impl.GilesPage;
import edu.asu.diging.citesphere.model.bib.impl.IGilesFile;
import edu.asu.diging.citesphere.user.IUser;

@Controller
public class GilesDocumentController {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private IGilesConnector gilesConnector;
    
    @Autowired
    private ICitationManager citationManager;
    
    private IGilesUpload gilesUpload;
    
    private List<GilesPage> gilesFile;
    
    private static ArrayList<IGilesFile> giles = new ArrayList<>();
    
    private static ListIterator<IGilesFile> gilesListIterator = giles.listIterator();
    
    private static IGilesUpload currentGilesUpload;

    private static IGilesFile currentFile;

    @RequestMapping(value="/auth/group/{zoteroGroupId}/items/{itemId}/giles/{fileId}")
    public void get(HttpServletResponse response, @PathVariable String itemId, @PathVariable String fileId, Authentication authentication) {
        
        String contentType = null;
        String fileName = null;
        ICitation citation = citationManager.getCitation(itemId);
        List<IGilesUpload> uploadOptionalList = citation.getGilesUploads().stream().filter(u -> u.getUploadedFile() != null && u.getUploadedFile().getId().equals(fileId) && u.getDocumentStatus().equals(GilesStatus.COMPLETE)).collect(Collectors.toList());
        
        if(uploadOptionalList.size()!=0) {
            
//            for(IGilesUpload gilesUpload : uploadOptionalList) {
//                Stream<Object> gilesFilesStream = generateStream(uploadOptionalList);
//                List<Object> gilesFiles = gilesFilesStream.collect(Collectors.toList());
//                    
//                for(Object tempFile : gilesFiles) {
//                    
//                    IGilesFile gilesFile = (IGilesFile) tempFile;
//                    if(gilesFile.getId().equals(fileId)) {
//                        contentType = gilesFile.getContentType();
//                        fileName = gilesFile.getFilename();
//                        break;
//                    }
//                }
//                
//                if(contentType!=null) {
//                    break;
//                }
//            }
            Stream<IGilesFile> gilesFilesStream = generateStream(uploadOptionalList);
            IGilesFile foundFile = gilesFilesStream.filter(file -> file.getId().equals(fileId)).findFirst().orElse(null);

            if(foundFile != null) {
                contentType = foundFile.getContentType();
                fileName = foundFile.getFilename();
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
    
    
    private static Stream<IGilesFile> generateStream(List<IGilesUpload> uploadOptionalList) {

        Stream<IGilesFile> gilesFilesStream = uploadOptionalList.stream().flatMap(gilesUpload -> {
            List<IGilesFile> gilesFiles = new ArrayList<>();
            gilesFiles.add(gilesUpload.getUploadedFile()); 
            gilesFiles.add(gilesUpload.getExtractedText());
            if(gilesUpload.getPages() != null) {      // Extracting pages 
                gilesUpload.getPages().forEach(p -> {
                    gilesFiles.add(p.getImage());
                    gilesFiles.add(p.getOcr());
                    gilesFiles.add(p.getText());
                    p.getAdditionalFiles().forEach(a -> gilesFiles.add(a));  // Extracting additional files of pages
                });
            }
            if(gilesUpload.getAdditionaFiles() != null) {
                gilesUpload.getAdditionaFiles().forEach(a -> gilesFiles.add(a)); // Extracting additional files
            }
            return gilesFiles.stream();
        });
        

//            @Override
//            public List<IGilesFile> get() {
//                List<IGilesFile> gilesFiles = new ArrayList<>();
//                for(IGilesUpload gilesUpload : uploadOptionalList) {
//
//                    gilesFiles.add(gilesUpload.getUploadedFile()); 
//                    gilesFiles.add(gilesUpload.getExtractedText());
//                    if(gilesUpload.getPages() != null) {      //Extracting pages 
//                        gilesUpload.getPages().forEach(p -> {
//                            gilesFiles.add(p.getImage());
//                            gilesFiles.add(p.getOcr());
//                            gilesFiles.add(p.getText());
//                            p.getAdditionalFiles().forEach(a -> gilesFiles.add(a));  //Extracting additional files of pages
//                        });
//                    }
//                    if(gilesUpload.getAdditionaFiles() != null) {
//                        gilesUpload.getAdditionaFiles().forEach(a -> gilesFiles.add(a)); //Extracting additional files
//                    }
//                }
//                return gilesFiles;
//            }
//        });
        return gilesFilesStream;
    }
}
