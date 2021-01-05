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

import edu.asu.diging.citesphere.api.v1.model.impl.Group;
import edu.asu.diging.citesphere.api.v1.user.JsonUtil;
import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.service.IGroupManager;
import edu.asu.diging.citesphere.core.user.IUserManager;
import edu.asu.diging.citesphere.model.bib.ICitationGroup;
import edu.asu.diging.citesphere.user.IUser;

@Controller
public class DeleteLocalCopy{
    
    
    @Autowired
    private IGroupManager groupManager;

    @Autowired
    private IUserManager userManager;
        
    @Autowired
    private JsonUtil jsonUtil;
	
	@RequestMapping(value = {"/auth/group/{zoteroGroupId}/mongo"})
	public ResponseEntity<Group> getCollectionsByGroupId(@RequestHeader HttpHeaders headers,
            @PathVariable("zoteroGroupId") String groupId, Principal principal) throws GroupDoesNotExistException {
		
		System.out.println("Inside delete");

        IUser user = userManager.findByUsername(principal.getName());

        groupManager.deleteGroup(user, groupId);
//        if (group == null) {
//            return new ResponseEntity<Group>(HttpStatus.NOT_FOUND);
//        }

//        Group jsonGroup = jsonUtil.createGroup(group);
//        System.out.println("Extracting ID from json " + jsonGroup.getId());
        
        
         /** TODO
          * jsonGroup is getting all the information about the group and group ID for the group we need to delete
          * After Delete button is clicked currently I am only returning group info in JSON format
          * Next step is to write the delete query for Mongo Repository
          * 
          */
//        groupRepository.deleteById(jsonGroup.getId());
//        groupRepository.delete(entity);
//        
        return new ResponseEntity<Group>(HttpStatus.OK);
		
        
    }
	

}
