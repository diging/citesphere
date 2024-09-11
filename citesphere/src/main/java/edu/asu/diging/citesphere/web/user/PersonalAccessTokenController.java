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
    public String getPersonalAccessToken(Authentication authentication, Model model) {
        model.addAttribute("userName", authentication.getName());
        List<IPersonalAccessToken> tokens = personalAccessTokenManager
                .getPersonalAccessTokens(authentication.getName());
        tokens.sort(Comparator.comparing(IPersonalAccessToken::getCreatedOn).reversed());
        model.addAttribute("listOfPersonalTokens", tokens);
        model.addAttribute("personalTokensCount", tokens.size());
        return "auth/personalAccessToken";
    }
}
