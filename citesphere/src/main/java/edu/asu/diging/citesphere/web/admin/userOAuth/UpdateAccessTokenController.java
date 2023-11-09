package edu.asu.diging.citesphere.web.admin.userOAuth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.asu.diging.citesphere.core.exceptions.CannotFindClientException;
import edu.asu.diging.citesphere.core.service.oauth.IUserTokenManager;
import edu.asu.diging.citesphere.core.service.oauth.OAuthCredentials;

public class UpdateAccessTokenController {
    @Autowired
    private IUserTokenManager userTokenManager;
    
    @RequestMapping(value="/admin/apps/{accessTokenId}/secret/update", method=RequestMethod.POST)
    public @ResponseBody OAuthCredentials regenerateClientSecret(Model model, @PathVariable("accessTokenId") String accessTokenId) throws CannotFindClientException {
        return userTokenManager.updateSecret(accessTokenId);
    }
}
