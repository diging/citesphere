package edu.asu.diging.citesphere.web.user;

import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.asu.diging.citesphere.core.model.oauth.IPersonalAccessToken;
import edu.asu.diging.citesphere.core.service.IPersonalAccessTokenManager;

@Controller
public class PersonalAccessTokenController {

    @Autowired
    private IPersonalAccessTokenManager personalAccessTokenManager;

    @RequestMapping(value = "/auth/personalAccessToken", method = RequestMethod.GET)
    public String savePersonalAccessToken(Authentication authentication, Model model) {
        model.addAttribute("userName", authentication.getName());

        List<IPersonalAccessToken> listOfPersonalTokens = personalAccessTokenManager
                .getPersonalAccessTokens(authentication.getName());
        listOfPersonalTokens.sort(Comparator.comparing(IPersonalAccessToken::getCreatedOn).reversed());
        model.addAttribute("listOfPersonalTokens", listOfPersonalTokens);

        String tokenGenerated = personalAccessTokenManager.savePersonalAccessToken(authentication.getName());
        model.addAttribute("tokenGenerated", tokenGenerated);
        return "auth/personalAccessToken";
    }

}
