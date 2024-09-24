package edu.asu.diging.citesphere.api.v1.user;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.asu.diging.citesphere.api.v1.V1Controller;
import edu.asu.diging.citesphere.api.v1.model.impl.Group;
import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.service.IGroupManager;
import edu.asu.diging.citesphere.core.user.IUserManager;
import edu.asu.diging.citesphere.model.bib.ICitationGroup;
import edu.asu.diging.citesphere.user.IUser;

@Controller
public class GroupApiController extends V1Controller {

    @Autowired
    private IGroupManager groupManager;

    @Autowired
    private IUserManager userManager;
    
    @Autowired
    private JsonUtil jsonUtil;
    
    @RequestMapping(value = { "/groups/{zoteroGroupId}" }, produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Group> getCollectionsByGroupId(@RequestHeader HttpHeaders headers,
            @PathVariable("zoteroGroupId") String groupId, Principal principal) throws GroupDoesNotExistException {

        IUser user = userManager.findByUsername(principal.getName());

        ICitationGroup group = groupManager.getGroup(user, groupId);
        if (group == null) {
            return new ResponseEntity<Group>(HttpStatus.NOT_FOUND);
        }

        Group jsonGroup = jsonUtil.createGroup(group);
        jsonGroup.setSyncInfo(getSyncInfo(group));
        return new ResponseEntity<Group>(jsonGroup, HttpStatus.OK);
    }
}
