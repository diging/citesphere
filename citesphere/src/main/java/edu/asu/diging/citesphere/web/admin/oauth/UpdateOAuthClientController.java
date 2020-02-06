package edu.asu.diging.citesphere.web.admin.oauth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import edu.asu.diging.citesphere.core.exceptions.CannotFindClientException;
import edu.asu.diging.citesphere.core.service.oauth.IOAuthClientManager;
import edu.asu.diging.citesphere.core.service.oauth.OAuthCredentials;

@Controller
public class UpdateOAuthClientController {
    
    @Autowired
    private IOAuthClientManager clientManager;
    
    @RequestMapping(value="/admin/apps/updateSecret/{clientId}", method=RequestMethod.POST)
    public String regenerateClientSecret(Model model, @PathVariable("clientId") String clientId, RedirectAttributes redirectAttrs) throws CannotFindClientException {
        OAuthCredentials creds = clientManager.updateClientSecret(clientId);
        redirectAttrs.addFlashAttribute("clientId", creds.getClientId());
        redirectAttrs.addFlashAttribute("secret", creds.getSecret());
        return "redirect:/admin/apps/" + creds.getClientId();
    }
}
