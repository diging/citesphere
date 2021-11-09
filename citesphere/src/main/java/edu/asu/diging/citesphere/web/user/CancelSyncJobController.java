package edu.asu.diging.citesphere.web.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.asu.diging.citesphere.core.service.jobs.ISyncJobManager;

@Controller
public class CancelSyncJobController {
    
    @Autowired
    private ISyncJobManager syncManager;

    @RequestMapping(value = "/auth/jobs/sync/{jobId}/cancel", method=RequestMethod.POST)
    public String cancel(@PathVariable String jobId) throws InterruptedException {
        syncManager.cancelJob(jobId);        
        return "redirect:/auth/jobs/sync/list";
    }
}
