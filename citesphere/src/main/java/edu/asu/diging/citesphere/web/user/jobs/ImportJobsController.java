package edu.asu.diging.citesphere.web.user.jobs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import edu.asu.diging.citesphere.core.service.jobs.IUploadJobManager;
import edu.asu.diging.citesphere.user.IUser;

@Controller
public class ImportJobsController {

    @Autowired
    private IUploadJobManager jobManager;
    
    @RequestMapping("/auth/import/jobs")
    public String list(Model model, @RequestParam(value="page", required=false, defaultValue="1") String page, Authentication authentication) {
        model.addAttribute("jobs", jobManager.getUploadJobs((IUser) authentication.getPrincipal(), new Integer(page)-1));
        return "auth/import/jobs";
    }
}
