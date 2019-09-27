package edu.asu.diging.citesphere.web.user.jobs;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import edu.asu.diging.citesphere.core.model.jobs.IUploadJob;
import edu.asu.diging.citesphere.core.service.ICitationManager;
import edu.asu.diging.citesphere.core.service.jobs.IUploadJobManager;

@Controller
public class ImportJobsController {

    @Autowired
    private IUploadJobManager jobManager;
    
    @Autowired
    private ICitationManager citationManager;
    
    @RequestMapping("/auth/import/jobs")
    public String list(Model model, @RequestParam(value="page", required=false, defaultValue="1") String page, Authentication authentication) {
    	List<IUploadJob> uploadedJobList = jobManager.getUploadJobs(authentication.getName(), new Integer(page)-1);
    	model.addAttribute("jobs", uploadedJobList);
    	model.addAttribute("citationGroups", citationManager.getCitationGroup(uploadedJobList));
        return "auth/import/jobs";
    }
}
