package edu.asu.diging.citesphere.web.user.jobs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.asu.diging.citesphere.core.service.ICitationManager;
import edu.asu.diging.citesphere.core.service.jobs.IUploadJobManager;
import edu.asu.diging.citesphere.user.IUser;

@Controller
public class ImportCollectionsController {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private IUploadJobManager jobManager;
    
    @Autowired
    private ICitationManager citationManager;
    
    @RequestMapping(value = "/auth/import/collection", method = RequestMethod.GET)
    public String show(Model model, Authentication authentication) {
        model.addAttribute("groups", citationManager.getGroups((IUser)authentication.getPrincipal()));
        return "auth/import/collection";
    }
}
