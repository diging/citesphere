package edu.asu.diging.citesphere.web.admin.oauth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.asu.diging.citesphere.core.model.oauth.OAuthClient;
import edu.asu.diging.citesphere.core.service.oauth.IOAuthClientManager;
import edu.asu.diging.citesphere.core.service.oauth.OAuthClientResultPage;

@Controller
public class ShowOAuthClientsController {

    @Autowired
    private IOAuthClientManager clientManager;
    

    @RequestMapping(value="/admin/apps", method=RequestMethod.GET)
    public String showAllApps(Model model, Pageable pageable) {
        OAuthClientResultPage result = clientManager.getAllClientDetails(pageable);
        model.addAttribute("clientList", result.getClientList());
        model.addAttribute("currentPage", pageable.getPageNumber()+1);
        model.addAttribute("totalPages", result.getTotalPages());
        return "admin/apps/show";
    }
    
    @RequestMapping(value="/admin/apps/{clientId}", method=RequestMethod.GET)
    public String showAppDetails(Model model, @PathVariable("clientId") String clientId) {
        OAuthClient details = (OAuthClient)clientManager.loadClientByClientId(clientId);
        model.addAttribute("name", details.getName());
        model.addAttribute("clientId", clientId);
        model.addAttribute("description", details.getDescription());
        return "admin/apps/details";
    }
}
