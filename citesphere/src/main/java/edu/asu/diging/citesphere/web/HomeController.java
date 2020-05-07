package edu.asu.diging.citesphere.web;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.asu.diging.citesphere.core.service.ICitationManager;
import edu.asu.diging.citesphere.core.zotero.IZoteroTokenManager;
import edu.asu.diging.citesphere.model.bib.ICitationGroup;
import edu.asu.diging.citesphere.user.IUser;

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
        }
        
        return "home";
    }
    
    @RequestMapping(value = "/groups")
    public @ResponseBody List<ICitationGroup> getGroups(Authentication authentication, @RequestParam(defaultValue = "false", required = true, value = "isZoteroConnected") String isConnected) {
       boolean isZoteroConnected = Boolean.parseBoolean(isConnected);
        if (isZoteroConnected) {
           return citationManager.getGroups((IUser) authentication.getPrincipal());
       }
        return new ArrayList<ICitationGroup>(); 
    }
    
    @RequestMapping(value ="/login")
    public String login() {
        return "login";
    }
}
