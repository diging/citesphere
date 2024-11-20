package edu.asu.diging.citesphere.web.user.jobs;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.asu.diging.citesphere.api.v1.model.impl.Collections;
import edu.asu.diging.citesphere.api.v1.user.JsonUtil;
import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.model.jobs.IUploadJob;
import edu.asu.diging.citesphere.core.service.ICitationCollectionManager;
import edu.asu.diging.citesphere.core.service.ICitationManager;
import edu.asu.diging.citesphere.core.service.IGroupManager;
import edu.asu.diging.citesphere.core.service.jobs.IUploadCollectionJobManager;
import edu.asu.diging.citesphere.core.user.IUserManager;
import edu.asu.diging.citesphere.model.bib.ICitationGroup;
import edu.asu.diging.citesphere.user.IUser;
import edu.asu.diging.citesphere.user.impl.User;

@Controller
public class ImportCollectionsController {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private IUploadCollectionJobManager jobManager;
    
    @Autowired
    private ICitationManager citationManager;
    
    @Autowired
    private ICitationCollectionManager collectionManager;

    @Autowired
    private IGroupManager groupManager;

    @Autowired
    private IUserManager userManager;
    

    @Autowired
    private JsonUtil jsonUtil;
    
    @RequestMapping(value = "/auth/import/collection", method = RequestMethod.GET)
    public String show(Model model, Authentication authentication) {
        model.addAttribute("groups", citationManager.getGroups((IUser)authentication.getPrincipal()));
        return "auth/import/collection";
    }
    
    @RequestMapping(value = "/auth/import/collection/getgroupcollections", method = RequestMethod.GET)
    public ResponseEntity<Collections> getCollections( @RequestParam("groupId") String groupId, Authentication authentication) {
        IUser user = (IUser)authentication.getPrincipal();

        ICitationGroup group = groupManager.getGroup(user, groupId);
        if (group == null) {
            return new ResponseEntity<Collections>(HttpStatus.NOT_FOUND);
        }

        Collections collectionResponse = new Collections();
        collectionResponse.setGroup(jsonUtil.createGroup(group));
        try {
            collectionResponse.setCollections(
                    collectionManager.getAllCollections(user, groupId, null, "title", new Integer("20")));
        } catch (NumberFormatException | GroupDoesNotExistException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<Collections>(collectionResponse, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/auth/import/collection", method = RequestMethod.POST)
    public ResponseEntity<String> uploadCollection(Principal principal, @RequestParam("group") String group,
            @RequestParam("collectionId") String collectionId, @RequestParam("files") MultipartFile[] files) {

        User user = null;
        if (principal instanceof UsernamePasswordAuthenticationToken) {
            user = (User) ((UsernamePasswordAuthenticationToken) principal)
                    .getPrincipal();
        }
        
        if (user == null) {
            return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
        }

        List<byte[]> fileBytes = new ArrayList<>();
        for (MultipartFile file : files) {
            try {
                fileBytes.add(file.getBytes());
            } catch (IOException e) {
                logger.error("Could not get file content from request.", e);
                fileBytes.add(null);
            }
        }
        List<IUploadJob> jobs;
        try {
            jobs = jobManager.createUploadJob(user, files, fileBytes, group, collectionId);
        } catch (GroupDoesNotExistException e) {
            logger.error("Could not create job because group does not exist.", e);
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
        
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode root = mapper.createObjectNode();
        ArrayNode filesNode = root.putArray("jobs");
        for (IUploadJob job : jobs) {
            ObjectNode jobNode = mapper.createObjectNode();
            jobNode.put("jobId", job.getId());
            jobNode.put("filename", job.getFilename());
            jobNode.put("status", job.getStatus().name());
            filesNode.add(jobNode);
        }
        return new ResponseEntity<String>(root.toString(), HttpStatus.OK);
    }

    
}
