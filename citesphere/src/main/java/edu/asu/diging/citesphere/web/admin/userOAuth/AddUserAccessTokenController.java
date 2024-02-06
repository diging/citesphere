package edu.asu.diging.citesphere.web.admin.userOAuth;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.asu.diging.citesphere.core.service.oauth.IOAuthClientManager;
import edu.asu.diging.citesphere.core.service.oauth.OAuthCredentials;
import edu.asu.diging.citesphere.core.user.IUserManager;
import edu.asu.diging.citesphere.user.IUser;
import edu.asu.diging.citesphere.web.admin.forms.UserAccessTokenForm;

@Controller
public class AddUserAccessTokenController {
    
    @Autowired
    private IUserManager userManager;
    
    @Autowired
    private IOAuthClientManager clientManager;
    
    @RequestMapping(value="/admin/user/auth/accessTokens/add", method=RequestMethod.GET)
    public String show(Model model) {
        model.addAttribute("userAccessTokenForm", new UserAccessTokenForm());
        return "admin/user/auth/add";
    }
    
    @RequestMapping(value="/admin/user/auth/accessTokens/add", method=RequestMethod.POST)
    public String add(@Validated UserAccessTokenForm userAccessTokenForm, Model model, BindingResult errors, RedirectAttributes redirectAttrs, Principal principal) {
        IUser user = userManager.findByUsername(principal.getName());
        OAuthCredentials creds = clientManager.createUserAccessToken(userAccessTokenForm.getName(), user);
        redirectAttrs.addFlashAttribute("clientId", creds.getClientId());
        redirectAttrs.addFlashAttribute("secret", creds.getSecret());
        return "redirect:/admin/user/auth/accessTokens/" + creds.getClientId();
    }
}
