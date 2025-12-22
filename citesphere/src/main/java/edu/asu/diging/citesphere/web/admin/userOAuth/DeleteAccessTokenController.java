package edu.asu.diging.citesphere.web.admin.userOAuth;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.asu.diging.citesphere.core.exceptions.CannotFindTokenException;
import edu.asu.diging.citesphere.core.service.oauth.IPersonalAccessTokenManager;
import edu.asu.diging.citesphere.core.user.IUserManager;
import edu.asu.diging.citesphere.user.IUser;

@Controller
public class DeleteAccessTokenController {

    @Autowired
    private IUserManager userManager;

    @Autowired
    private IPersonalAccessTokenManager tokenManager;

    @RequestMapping(value = "/admin/user/auth/accessTokens/{accessTokenId}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteApp(@PathVariable("accessTokenId") String accessTokenId, Principal principal) {
        IUser user = userManager.findByUsername(principal.getName());
        try {
            tokenManager.deleteToken(accessTokenId, user);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (CannotFindTokenException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
