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
    
    @RequestMapping(value="/auth/group/{zoteroGroupId}/items/{itemId}/giles/{fileId}")
    public void get(HttpServletResponse response, @PathVariable String itemId, @PathVariable String fileId, Authentication authentication) {
        
        String contentType = null;
        String fileName = null;
        ICitation citation = citationManager.getCitation(itemId);
        List<IGilesUpload> uploadOptionalList = citation.getGilesUploads().stream().filter(u -> u.getUploadedFile() != null && u.getDocumentStatus().equals(GilesStatus.COMPLETE)).collect(Collectors.toList());
        
        if(uploadOptionalList.size()!=0) {
            
            for(IGilesUpload gilesUpload : uploadOptionalList) {
                Stream<IGilesFile> gilesFilesStream = generateStream(gilesUpload);
                List<IGilesFile> gilesFiles = gilesFilesStream.collect(Collectors.toList());
                    
                for(IGilesFile gilesFile : gilesFiles) {
                    if(gilesFile.getId().equals(fileId)) {
                        contentType = gilesFile.getContentType();
                        fileName = gilesFile.getFilename();
                        break;
                    }
                }
                
                
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
    
    
    private static Stream<IGilesFile> generateStream(IGilesUpload gilesUpload) {
        
        Stream<IGilesFile> gilesUploadStream = Stream.generate(new Supplier<IGilesFile>() {
                
            @Override
            public IGilesFile get() {
                giles.add(gilesUpload.getUploadedFile()); 
                giles.add(gilesUpload.getExtractedText());
                if(gilesUpload.getPages() != null) {      //Extracting pages 
                    gilesUpload.getPages().forEach(p -> {
                        giles.add(p.getImage());
                        giles.add(p.getOcr());
                        giles.add(p.getText());
                        p.getAdditionalFiles().forEach(a -> giles.add(a));  //Extracting additional files of pages
                    });
                }
                if(gilesUpload.getAdditionaFiles() != null) {
                    gilesUpload.getAdditionaFiles().forEach(a -> giles.add(a)); //Extracting additional files
                }
                return gilesListIterator.next();
            }
        });
        
        return gilesUploadStream;
    }
    
}
