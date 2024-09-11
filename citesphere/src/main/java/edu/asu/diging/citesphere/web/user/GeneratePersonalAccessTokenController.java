package edu.asu.diging.citesphere.web.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import edu.asu.diging.citesphere.core.service.IPersonalAccessTokenManager;

@Controller
public class GeneratePersonalAccessTokenController {

    @Autowired
    private IPersonalAccessTokenManager personalAccessTokenManager;
    
    @RequestMapping(value = "/auth/personalAccessToken/create")
    public String savePersonalAccessToken(Authentication authentication, Model model) {
        model.addAttribute("userName", authentication.getName());
        String tokenGenerated = personalAccessTokenManager.savePersonalAccessToken(authentication.getName());
        model.addAttribute("tokenGenerated", tokenGenerated);
        return "auth/generatePersonalAccessToken";
    }

}
