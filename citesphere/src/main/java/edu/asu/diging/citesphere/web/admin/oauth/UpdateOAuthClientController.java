package edu.asu.diging.citesphere.web.admin.oauth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import edu.asu.diging.citesphere.core.exceptions.CannotFindClientException;
import edu.asu.diging.citesphere.core.model.oauth.impl.OAuthClient;
import edu.asu.diging.citesphere.core.service.oauth.IOAuthClientManager;

@Controller
public class UpdateOAuthClientController {
    
    @Autowired
    private IOAuthClientManager clientManager;
    
    @RequestMapping(value="/admin/apps/updateSecret/{clientId}", method=RequestMethod.POST)
    public String regenerateClientSecret(Model model, @PathVariable("clientId") String clientId) throws CannotFindClientException {
        OAuthClient client = clientManager.updateClientSecret(clientId);
        model.addAttribute("clientId", client.getClientId());
        model.addAttribute("secret", client.getClientSecret());
        model.addAttribute("clientName", client.getName());
        model.addAttribute("description", client.getDescription());
        model.addAttribute("redirectUrl", String.join(", ", client.getRegisteredRedirectUri()));
        model.addAttribute("applicationType", client.getAuthorizedGrantTypes().contains("authorization_code")?"authorization_code":"client_credentials");
        return "admin/apps/details";
    }
}
