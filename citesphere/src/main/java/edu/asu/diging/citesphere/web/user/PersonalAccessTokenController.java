package edu.asu.diging.citesphere.web.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.asu.diging.citesphere.core.service.IPersonalAccessTokenManager;

@Controller
public class PersonalAccessTokenController {

    @Autowired
    private IPersonalAccessTokenManager personalAccessTokenManager;

    @RequestMapping(value = "/auth/personalToken", method = RequestMethod.GET)
    public String savePersonalAccessToken(Authentication authentication, Model model) {
        personalAccessTokenManager.savePersonalAccessToken(authentication.getName());
        return "auth/personalToken";
    }

}
