package edu.asu.diging.citesphere.web.admin.userOAuth;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.asu.diging.citesphere.core.service.oauth.IPersonalAccessTokenManager;
import edu.asu.diging.citesphere.core.service.oauth.PersonalAccessTokenResultPage;
import edu.asu.diging.citesphere.core.user.IUserManager;
import edu.asu.diging.citesphere.user.IUser;

@Controller
public class ShowAccessTokensController {

    @Autowired
    private IUserManager userManager;

    @Autowired
    private IPersonalAccessTokenManager tokenManager;

    @RequestMapping(value="/admin/user/auth/accessTokens", method=RequestMethod.GET)
    public String showAllApps(Model model, Pageable pageable, Principal principal) {
        IUser user = userManager.findByUsername(principal.getName());
        PersonalAccessTokenResultPage result = tokenManager.getTokensForUser(user, pageable);
        model.addAttribute("tokenList", result.getTokens());
        model.addAttribute("currentPage", pageable.getPageNumber()+1);
        model.addAttribute("totalPages", result.getTotalPages());
        return "admin/user/auth/show";
    }
}
