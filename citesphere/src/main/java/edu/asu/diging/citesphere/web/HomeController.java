package edu.asu.diging.citesphere.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.asu.diging.citesphere.core.service.ICitationManager;
import edu.asu.diging.citesphere.core.zotero.IZoteroTokenManager;
import edu.asu.diging.citesphere.model.IUser;

@Controller
public class HomeController {

    @Autowired
    private IZoteroTokenManager tokenManager;

    @Autowired
    private ICitationManager citationManager;

    @RequestMapping(value = "/")
    public String home(Authentication authentication, Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            IUser user = (IUser) authentication.getPrincipal();
            boolean isConnected = tokenManager.getToken(user) != null;
            model.addAttribute("isZoteroConnected", isConnected);
            model.addAttribute("user", user);
            if (isConnected) {
                model.addAttribute("groups", citationManager.getGroups(user));
            }
        }
        
        return "home";
    }
    
    @RequestMapping(value ="/login")
    public String login() {
        return "login";
    }
}
