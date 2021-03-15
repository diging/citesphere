package edu.asu.diging.citesphere.web.user;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.service.IGroupManager;
import edu.asu.diging.citesphere.core.user.IUserManager;
import edu.asu.diging.citesphere.user.IUser;

@Controller
public class DeleteLocalGroupCopyController{
     
    @Autowired
    private IGroupManager groupManager;

    @Autowired
    private IUserManager userManager;
    
    @RequestMapping(value = "/auth/group/{zoteroGroupId}/resync", method = RequestMethod.POST)
    public ResponseEntity<String> getCollectionsByGroupId(@RequestHeader HttpHeaders headers,
            @PathVariable("zoteroGroupId") String groupId, Principal principal) throws GroupDoesNotExistException {
		
        IUser user = userManager.findByUsername(principal.getName());
        groupManager.deleteLocalGroupCopy(user, groupId);
        return new ResponseEntity<String>(HttpStatus.OK);        
    }	
}
