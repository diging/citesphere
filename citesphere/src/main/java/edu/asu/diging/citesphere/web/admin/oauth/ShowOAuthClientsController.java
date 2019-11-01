package edu.asu.diging.citesphere.web.admin.oauth;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.asu.diging.citesphere.core.model.IOAuthClient;
import edu.asu.diging.citesphere.core.model.oauth.OAuthClientCollectionResult;
import edu.asu.diging.citesphere.core.service.oauth.IOAuthClientManager;

@Controller
public class ShowOAuthClientsController {

    @Autowired
    private IOAuthClientManager clientManager;
    

    @RequestMapping(value="/admin/apps", method=RequestMethod.GET)
    public String showAllApps(Model model, Pageable pageable) {
        OAuthClientCollectionResult result = clientManager.getClientDetails(pageable);
        model.addAttribute("clientList", result.getClientList());
        model.addAttribute("currentPage", pageable.getPageNumber()+1);
        model.addAttribute("totalPages", Math.ceil(new Float(22) / new Float(pageable.getPageSize())));
        return "admin/apps/show";
    }
}
