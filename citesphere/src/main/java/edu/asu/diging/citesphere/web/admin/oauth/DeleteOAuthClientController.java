package edu.asu.diging.citesphere.web.admin.oauth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import edu.asu.diging.citesphere.core.service.oauth.IOAuthClientManager;

@Controller
public class DeleteOAuthClientController {

    @Autowired
    private IOAuthClientManager clientManager;

    @RequestMapping(value = "/admin/apps/{clientId}", method = RequestMethod.GET)
    public String deleteApp(@PathVariable("clientId") String clientId,
            @RequestParam(defaultValue = "1", required = false, value = "page") String page) {
        clientManager.deleteClient(clientId);
        return "redirect:/admin/apps?page=" + new Integer(page);
    }
}
