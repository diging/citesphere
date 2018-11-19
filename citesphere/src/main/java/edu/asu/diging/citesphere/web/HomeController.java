package edu.asu.diging.citesphere.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.asu.diging.citesphere.core.model.IUser;
import edu.asu.diging.citesphere.core.service.IZoteroTokenManager;

@Controller
public class HomeController {
    
    @Autowired
    private IZoteroTokenManager tokenManager;
    
    @RequestMapping(value = "/")
    public String home(Authentication authentication, Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            IUser user = (IUser) authentication.getPrincipal();
            model.addAttribute("isZoteroConnected", tokenManager.getToken(user) != null);
            model.addAttribute("user", user);
        }
        
        return "home";
    }
}
