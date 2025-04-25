package edu.asu.diging.citesphere.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.asu.diging.citesphere.core.service.jobs.ISyncJobManager;

@Controller
public class DeleteSyncJobController {
    @Autowired
    private ISyncJobManager syncManager;

    @RequestMapping(value = "/auth/jobs/sync/{jobId}/delete", method=RequestMethod.POST)
    public String delete(@PathVariable String jobId) {
        syncManager.deleteJob(jobId);        
        return "redirect:/auth/jobs/sync/list";
    }
}
