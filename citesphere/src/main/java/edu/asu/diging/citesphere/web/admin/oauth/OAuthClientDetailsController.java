package edu.asu.diging.citesphere.web.admin.oauth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import edu.asu.diging.citesphere.core.model.oauth.IOAuthClient;
import edu.asu.diging.citesphere.core.service.oauth.IOAuthClientManager;

@Controller
public class OAuthClientDetailsController {

    @Autowired
    private IOAuthClientManager clientManager;
    
    @RequestMapping(value="/admin/apps/{clientId}", method=RequestMethod.GET)
    public String showAppDetails(Model model, @PathVariable("clientId") String clientId) {
        IOAuthClient details = (IOAuthClient)clientManager.loadClientByClientId(clientId);
        model.addAttribute("clientName", details.getName());
        model.addAttribute("clientId", clientId);
        model.addAttribute("description", details.getDescription());
        return "admin/apps/details";
    }
}
