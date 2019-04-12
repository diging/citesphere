package edu.asu.diging.citesphere.web.user.authorities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import edu.asu.diging.citesphere.core.model.IUser;
import edu.asu.diging.citesphere.core.model.authority.IAuthorityEntry;
import edu.asu.diging.citesphere.core.service.IAuthorityService;

@Controller
public class DeleteAuthorityController {
    
    @Autowired
    private IAuthorityService authorityService;

    @RequestMapping(value="/auth/authority/{authorityId}", method=RequestMethod.DELETE)
    public ResponseEntity<String> deleteAuthority(@PathVariable("authorityId") String authorityId, Authentication authentication) {
        IAuthorityEntry entry = authorityService.find(authorityId);
        if (entry == null) {
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }
        if (!entry.getUsername().equals(((IUser)authentication.getPrincipal()).getUsername())) {
            return new ResponseEntity<String>(HttpStatus.FORBIDDEN);
        }
        boolean success = authorityService.deleteAuthority(authorityId);
        if (success) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
