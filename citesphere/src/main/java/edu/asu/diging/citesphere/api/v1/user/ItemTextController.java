package edu.asu.diging.citesphere.api.v1.user;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.HttpClientErrorException;

import edu.asu.diging.citesphere.api.v1.V1Controller;
import edu.asu.diging.citesphere.core.exceptions.AccessForbiddenException;
import edu.asu.diging.citesphere.core.exceptions.CannotFindCitationException;
import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroHttpStatusException;
import edu.asu.diging.citesphere.core.service.ICitationManager;
import edu.asu.diging.citesphere.core.service.IGroupManager;
import edu.asu.diging.citesphere.core.user.IUserManager;
import edu.asu.diging.citesphere.model.bib.ICitationGroup;
import edu.asu.diging.citesphere.user.IUser;

@Controller
public class ItemTextController extends V1Controller {
    
    @Autowired
    private ICitationManager citationManager;

    @Autowired
    private IGroupManager groupManager;

    @Autowired
    private IUserManager userManager;
    
    @GetMapping(value = "/groups/{groupId}/items/{item}/text", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<String> getItemText(@PathVariable("groupId") String groupId, @PathVariable("item") String itemKey,
            Principal principal) throws GroupDoesNotExistException {
        
        IUser user = userManager.findByUsername(principal.getName());

        ICitationGroup group = groupManager.getGroup(user, groupId);
        if (group == null) {
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }

        StringBuilder itemText = new StringBuilder();
        try {
            itemText = citationManager.getText(user, groupId, itemKey);
        } catch (AccessForbiddenException e) {
            return new ResponseEntity<String>(HttpStatus.FORBIDDEN);
        } catch (CannotFindCitationException | ZoteroHttpStatusException e) {
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        } catch (HttpClientErrorException.Unauthorized e) {
            return new ResponseEntity<String>("Error: Unauthorized access when fetching file contents", HttpStatus.UNAUTHORIZED);
        } catch (HttpClientErrorException e) {
            return new ResponseEntity<String>("Error: Giles HTTP Client error when fetching file contents", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
        return new ResponseEntity<String>(itemText.toString(), HttpStatus.OK);
    }
}
