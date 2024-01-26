package edu.asu.diging.citesphere.web.admin.userOAuth;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.asu.diging.citesphere.core.service.oauth.IUserTokenManager;
import edu.asu.diging.citesphere.core.service.oauth.UserAccessTokenResultPage;
import edu.asu.diging.citesphere.core.user.IUserManager;
import edu.asu.diging.citesphere.user.IUser;

@Controller
public class ShowAccessTokensController {
    @Autowired
    private IUserTokenManager userTokenManager;
    
    @Autowired
    private IUserManager userManager;
    
    @RequestMapping(value="/admin/user/auth/accessTokens", method=RequestMethod.GET)
    public String showAllApps(Model model, Pageable pageable, Principal principal) {
        IUser user = userManager.findByUsername(principal.getName());
        UserAccessTokenResultPage result = userTokenManager.getAllAccessTokensDetails(pageable, user);
        model.addAttribute("clientList", result.getClientList());
        model.addAttribute("currentPage", pageable.getPageNumber()+1);
        model.addAttribute("totalPages", result.getTotalPages());
        return "admin/user/auth/show";
    }
}
