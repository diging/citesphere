package edu.asu.diging.citesphere.web.user;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;
import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.HttpClientErrorException;

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

    @RequestMapping(value="/auth/group/{zoteroGroupId}/items/{itemId}/giles/{fileId}")
    public void get(HttpServletResponse response, @PathVariable String itemId, @PathVariable String fileId, Authentication authentication, Model model) {
        
        IUser user = (IUser) authentication.getPrincipal();
        
        try {
            // Get file content from Giles
            byte[] fileContent = gilesConnector.getFile(user, fileId);
            
            if (fileContent == null || fileContent.length == 0) {
                response.setStatus(org.apache.http.HttpStatus.SC_NOT_FOUND);
                return;
            }
            
            // Create input stream from byte array
            InputStream is = new ByteArrayInputStream(fileContent);
            
            // Set response headers - we'll use a generic filename since we don't have metadata readily available
            response.setHeader("Content-disposition", "attachment; filename=giles_file_" + fileId);
            
            // Copy file content to response
            IOUtils.copy(is, response.getOutputStream());
            response.flushBuffer();
            
        } catch (HttpClientErrorException e) {
            logger.error("Error retrieving file from Giles with fileId: {}", fileId, e);
            response.setStatus(org.apache.http.HttpStatus.SC_NOT_FOUND);
        } catch (IOException e) {
            logger.error("Error writing file to output stream. FileId was '{}'", fileId, e);
            response.setStatus(org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
