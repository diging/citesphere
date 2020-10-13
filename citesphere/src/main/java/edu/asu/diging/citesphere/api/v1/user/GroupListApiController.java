package edu.asu.diging.citesphere.api.v1.user;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.asu.diging.citesphere.api.v1.V1Controller;
import edu.asu.diging.citesphere.api.v1.model.impl.Group;
import edu.asu.diging.citesphere.core.service.ICitationManager;
import edu.asu.diging.citesphere.core.user.IUserManager;
import edu.asu.diging.citesphere.model.bib.ICitationGroup;
import edu.asu.diging.citesphere.user.IUser;

@Controller
public class GroupListApiController extends V1Controller {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private IUserManager userManager;
    
    @Autowired
    private ICitationManager citationManager;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private JsonUtil jsonUtil;


    @RequestMapping(value = { "/groups" }, produces = { MediaType.APPLICATION_JSON_UTF8_VALUE } )
    public ResponseEntity<String> list(Principal principal) {
        IUser user = userManager.findByUsername(principal.getName());
        if (user == null) {
            return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
        }
        
        List<ICitationGroup> groups = citationManager.getGroups(user);
        List<Group> jsonGroups = groups.stream().map(g -> jsonUtil.createGroup(g)).collect(Collectors.toList());
        
        String jsonResponse = "";
        try {
            jsonResponse = objectMapper.writeValueAsString(jsonGroups);
        } catch (IOException e) {
            logger.error("Unable to process JSON response ", e);
            return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<String>(jsonResponse, HttpStatus.OK);
    }
}
