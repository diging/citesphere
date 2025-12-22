package edu.asu.diging.citesphere.web.admin.userOAuth;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.asu.diging.citesphere.core.exceptions.CannotFindTokenException;
import edu.asu.diging.citesphere.core.service.oauth.IPersonalAccessTokenManager;
import edu.asu.diging.citesphere.core.service.oauth.PersonalAccessTokenCredentials;
import edu.asu.diging.citesphere.core.user.IUserManager;
import edu.asu.diging.citesphere.user.IUser;

@Controller
public class UpdateAccessTokenController {

    @Autowired
    private IUserManager userManager;

    @Autowired
    private IPersonalAccessTokenManager tokenManager;

    @RequestMapping(value="/admin/user/auth/accessTokens/{accessTokenId}/secret/update", method=RequestMethod.POST)
    public @ResponseBody PersonalAccessTokenCredentials regenerateToken(Model model, @PathVariable("accessTokenId") String accessTokenId, Principal principal) throws CannotFindTokenException {
        IUser user = userManager.findByUsername(principal.getName());
        return tokenManager.regenerateToken(accessTokenId, user);
    }
}
