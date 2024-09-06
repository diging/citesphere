package edu.asu.diging.citesphere.api.v1.user;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.service.ICitationManager;
import edu.asu.diging.citesphere.core.service.IGroupManager;
import edu.asu.diging.citesphere.core.user.IUserManager;
import edu.asu.diging.citesphere.model.bib.ICitation;
import edu.asu.diging.citesphere.model.bib.ICitationGroup;
import edu.asu.diging.citesphere.user.IUser;

@Controller
public class AddItemTextController {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private ICitationManager citationManager;

    @Autowired
    private IGroupManager groupManager;

    @Autowired
    private IUserManager userManager;
    
    @GetMapping(value = "/groups/{groupId}/items/{item}/text", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<String> getItem(@PathVariable("groupId") String groupId, @PathVariable("item") String itemKey,
            Principal principal) throws GroupDoesNotExistException {
        
        IUser user = userManager.findByUsername(principal.getName());

        ICitationGroup group = groupManager.getGroup(user, groupId);
        if (group == null) {
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }

        ICitation item;
        
        
        return null;
    }
}
