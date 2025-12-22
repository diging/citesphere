package edu.asu.diging.citesphere.web.admin.userOAuth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.asu.diging.citesphere.core.model.oauth.IPersonalAccessToken;
import edu.asu.diging.citesphere.core.service.oauth.IPersonalAccessTokenManager;

@Controller
public class AccessTokenDetailsController {

    @Autowired
    private IPersonalAccessTokenManager tokenManager;

    @RequestMapping(value="/admin/user/auth/accessTokens/{accessTokenId}", method=RequestMethod.GET)
    public String showAppDetails(Model model, @PathVariable("accessTokenId") String accessTokenId) {
        IPersonalAccessToken token = tokenManager.getTokenById(accessTokenId);
        if (token != null) {
            model.addAttribute("clientName", token.getName());
            model.addAttribute("clientId", accessTokenId);
        }
        return "admin/user/auth/details";
    }
}
