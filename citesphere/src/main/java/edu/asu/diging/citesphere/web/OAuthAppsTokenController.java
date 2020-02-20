package edu.asu.diging.citesphere.web;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import edu.asu.diging.citesphere.core.model.oauth.impl.DbAccessToken;
import edu.asu.diging.citesphere.core.service.impl.DbTokenStore;
import edu.asu.diging.citesphere.core.service.oauth.impl.OAuthClientManager;

@Controller
public class OAuthAppsTokenController {

    @Autowired
    private DbTokenStore tokenStore;
    
    @Autowired
    OAuthClientManager clientManager;

    @RequestMapping(value="/tokens", method=RequestMethod.GET)
    public String getAllAccessTokensForUser(Authentication authentication, Model model) {
        List<DbAccessToken> tokens = tokenStore.findTokensByUserName(authentication.getName());
        List<String> clientList = new ArrayList<>();
        tokens.forEach(t -> clientList.add(t.getClientId()));
        model.addAttribute("clientList",clientManager.getClientsDetails(clientList));
        return "tokens";
    }    
}
