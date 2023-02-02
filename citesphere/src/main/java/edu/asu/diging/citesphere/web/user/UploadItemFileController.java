package edu.asu.diging.citesphere.web.user;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.social.zotero.exception.ZoteroConnectionException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.asu.diging.citesphere.core.exceptions.AccessForbiddenException;
import edu.asu.diging.citesphere.core.exceptions.CannotFindCitationException;
import edu.asu.diging.citesphere.core.exceptions.CitationIsOutdatedException;
import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroHttpStatusException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroItemCreationFailedException;
import edu.asu.diging.citesphere.core.service.jobs.IUploadFileJobManager;
import edu.asu.diging.citesphere.model.bib.IGilesUpload;
import edu.asu.diging.citesphere.user.impl.User;
import edu.asu.diging.citesphere.core.util.model.IGilesUtil;

@Controller
public class UploadItemFileController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private IUploadFileJobManager jobManager;
    
    @Autowired
    private IGilesUtil gilesUtil;

    @RequestMapping(value = "/auth/group/{zoteroGroupId}/items/{itemId}/files/upload", method = RequestMethod.POST)
    public ResponseEntity<String> uploadFile(Principal principal, @PathVariable String zoteroGroupId,
            @PathVariable String itemId, @RequestParam("files") MultipartFile[] files)
            throws AccessForbiddenException, CannotFindCitationException, ZoteroHttpStatusException,
            ZoteroConnectionException, CitationIsOutdatedException, ZoteroItemCreationFailedException {
        User user = null;
        if (principal instanceof UsernamePasswordAuthenticationToken) {
            user = (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        }

        if (user == null) {
            return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
        }

        List<byte[]> fileBytes = new ArrayList<>();
        gilesUtil.convertFilesToBytesList(fileBytes, files);
        
        IGilesUpload job;
        try {
            job = jobManager.createGilesJob(user, files[0], fileBytes.get(0), zoteroGroupId, itemId);
        } catch (GroupDoesNotExistException e) {
            logger.error("Could not create job because group does not exist.", e);
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode root = mapper.createObjectNode(); 
        gilesUtil.createJobObjectNode(root, job);

        return new ResponseEntity<String>(root.toString(), HttpStatus.OK);
    }
}
