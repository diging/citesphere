package edu.asu.diging.citesphere.web.admin.oauth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.asu.diging.citesphere.core.service.oauth.IOAuthClientManager;

@Controller
public class ShowOAuthClientController {

    @Autowired
    private IOAuthClientManager clientManager;
    

    @RequestMapping(value="/admin/apps/show", method=RequestMethod.GET)
    public String showAllApps(Model model) {
        model.addAttribute("clientList", clientManager.showAllApps());
        return "admin/apps/show";
    }
}
