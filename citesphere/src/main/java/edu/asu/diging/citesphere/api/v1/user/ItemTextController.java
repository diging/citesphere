package edu.asu.diging.citesphere.api.v1.user;

import java.io.IOException;
import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.social.zotero.exception.ZoteroConnectionException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.asu.diging.citesphere.api.v1.V1Controller;
import edu.asu.diging.citesphere.core.exceptions.CannotFindCitationException;
import edu.asu.diging.citesphere.core.exceptions.CitationIsOutdatedException;
import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroHttpStatusException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroItemCreationFailedException;
import edu.asu.diging.citesphere.core.service.ICitationManager;
import edu.asu.diging.citesphere.core.service.IGroupManager;
import edu.asu.diging.citesphere.core.service.jobs.IUploadFileJobManager;
import edu.asu.diging.citesphere.core.user.IUserManager;
import edu.asu.diging.citesphere.core.util.IGilesUtil;
import edu.asu.diging.citesphere.model.bib.ICitation;
import edu.asu.diging.citesphere.model.bib.ICitationGroup;
import edu.asu.diging.citesphere.model.bib.IGilesUpload;
import edu.asu.diging.citesphere.user.IUser;

@Controller
public class ItemTextController extends V1Controller {
    
    private Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private ICitationManager citationManager;

    @Autowired
    private IGroupManager groupManager;

    @Autowired
    private IUserManager userManager;
    
    @Autowired
    private IUploadFileJobManager jobManager;
    
    @Autowired
    private IGilesUtil gilesUtil;
    
    @PostMapping(value = "/groups/{groupId}/items/{item}/text", consumes= {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Object> getItemText(@PathVariable("groupId") String groupId, @PathVariable("item") String itemKey,
            @RequestParam("files") MultipartFile[] files, Principal principal) throws GroupDoesNotExistException {
        
        IUser user = userManager.findByUsername(principal.getName());

        ICitationGroup group = groupManager.getGroup(user, groupId);
        if (group == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        ICitation citation = null;
        try {
            citation = citationManager.getCitation(user, groupId, itemKey);
        } catch (GroupDoesNotExistException | CannotFindCitationException | ZoteroHttpStatusException e) {
            // TODO Give proper msgs
            e.printStackTrace();
        }
        
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode root = mapper.createObjectNode(); 
        for (int i=0; i<files.length; i++) {
            IGilesUpload job = null;
            try {
                    job = jobManager.createGilesJob(user, files[i], files[i].getBytes(), groupId,
                            citation.getKey());
            } catch (ZoteroHttpStatusException | ZoteroConnectionException | ZoteroItemCreationFailedException e) {
                // TODO Give proper msgs
                e.printStackTrace();
            } catch (GroupDoesNotExistException e) {
                logger.error("Could not create job because group does not exist.", e);
                return new ResponseEntity<>("Error: Could not create job because group does not exist.", HttpStatus.BAD_REQUEST);
            } catch (CannotFindCitationException e) {
                logger.error("Could not find the newly created citation.", e);
                return new ResponseEntity<>("Error: Could not find the newly created citation.", HttpStatus.INTERNAL_SERVER_ERROR);
            } catch (CitationIsOutdatedException e) {
                logger.error("Citation outdated. ", e);
                return new ResponseEntity<>("Error: Citation outdated.", HttpStatus.BAD_REQUEST);
            } catch (IOException e) {
                logger.error("Could not read file from the request. ", e);
                return new ResponseEntity<>("Error: Could not read file from the request.", HttpStatus.BAD_REQUEST);
            }

            gilesUtil.createJobObjectNode(root, job);
        }
        
        return new ResponseEntity<>(citation, HttpStatus.OK);
    }
}
