package edu.asu.diging.citesphere.web.admin.oauth;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import edu.asu.diging.citesphere.core.service.oauth.IOAuthClientManager;
import edu.asu.diging.citesphere.core.service.oauth.OAuthCredentials;
import edu.asu.diging.citesphere.core.service.oauth.OAuthScope;
import edu.asu.diging.citesphere.web.admin.forms.AppForm;

@Controller
public class AddOAuthClientController {
    
    @Autowired
    private IOAuthClientManager clientManager;

    @RequestMapping(value="/admin/apps/add", method=RequestMethod.GET)
    public String show(Model model) {
        model.addAttribute("appForm", new AppForm());
        return "admin/apps/add";
    }
    
    @RequestMapping(value="/admin/apps/add", method=RequestMethod.POST)
    public String add(AppForm appForm, Model model) {
        OAuthCredentials creds = clientManager.create(appForm.getName(), appForm.getDescription(), Arrays.asList(OAuthScope.READ));
        model.addAttribute("clientId", creds.getClientId());
        model.addAttribute("secret", creds.getSecret());
        model.addAttribute("clientName", appForm.getName());
        model.addAttribute("description", appForm.getDescription());
        return "admin/apps/details";
    }
}
