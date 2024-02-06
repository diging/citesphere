package edu.asu.diging.citesphere.web.admin.userOAuth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.asu.diging.citesphere.core.exceptions.CannotFindClientException;
import edu.asu.diging.citesphere.core.service.oauth.IOAuthClientManager;
import edu.asu.diging.citesphere.core.service.oauth.OAuthCredentials;

@Controller
public class UpdateAccessTokenController {
    
    @Autowired
    private IOAuthClientManager clientManager;
    
    @RequestMapping(value="/admin/user/auth/accessTokens/{accessTokenId}/secret/update", method=RequestMethod.POST)
    public @ResponseBody OAuthCredentials regenerateClientSecret(Model model, @PathVariable("accessTokenId") String accessTokenId) throws CannotFindClientException {
        return clientManager.updateClientSecret(accessTokenId);
    }
}
