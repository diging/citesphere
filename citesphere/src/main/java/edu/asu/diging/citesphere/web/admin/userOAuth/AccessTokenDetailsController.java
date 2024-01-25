package edu.asu.diging.citesphere.web.admin.userOAuth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.asu.diging.citesphere.core.model.oauth.IUserAccessToken;
import edu.asu.diging.citesphere.core.service.oauth.IUserTokenManager;

@Controller
public class AccessTokenDetailsController {
    @Autowired
    private IUserTokenManager userTokenManager;
    
    @RequestMapping(value="/admin/user/auth/{accessTokenId}", method=RequestMethod.GET)
    public String showAppDetails(Model model, @PathVariable("accessTokenId") String accessTokenId) {
        IUserAccessToken details = (IUserAccessToken) userTokenManager.loadClientByClientId(accessTokenId);
        model.addAttribute("clientName", details.getName());
        model.addAttribute("clientId", details.getClientId());
        return "admin/user/auth/details";
    }
}
