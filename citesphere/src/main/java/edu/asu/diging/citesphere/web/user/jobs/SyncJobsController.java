package edu.asu.diging.citesphere.web.user.jobs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import edu.asu.diging.citesphere.core.model.jobs.JobStatus;
import edu.asu.diging.citesphere.core.service.jobs.ISyncJobManager;
import edu.asu.diging.citesphere.user.IUser;

@Controller
public class SyncJobsController {

    @Autowired
    private ISyncJobManager jobManager;

    @RequestMapping("/auth/jobs/sync/list")
    public String list(Model model, @PageableDefault(sort = { "createdOn" }, direction = Direction.DESC) Pageable page, Authentication authentication,
            @RequestParam(value = "groupId", required = false, defaultValue = "All") String groupId,
            @RequestParam(value = "jobStatus", required = false, defaultValue = "All") String jobStatus) {
        long total = jobManager.getJobsCount((IUser) authentication.getPrincipal());
        if (total == -1) {
            return "redirect:/";
        }
        JobStatus status;
        if (jobStatus.equals("All") && groupId.equals("All")) {
            model.addAttribute("jobs", jobManager.getJobs((IUser) authentication.getPrincipal(), page));
        } else if (!jobStatus.equals("All") && groupId.equals("All")) {
            status = JobStatus.valueOf(jobStatus);
            model.addAttribute("jobs", jobManager.getJobs((IUser) authentication.getPrincipal(), status, page));
        }  else if (jobStatus.equals("All") && !groupId.equals("All")) {
            model.addAttribute("jobs", jobManager.getJobs(groupId, page));
        } else {
            status = JobStatus.valueOf(jobStatus);
            model.addAttribute("jobs", jobManager.getJobs(groupId, status, page));
        }
        model.addAttribute("groupId", groupId);
        model.addAttribute("total", Math.ceil(total/page.getPageSize()));
        model.addAttribute("page", page.getPageNumber() + 1);
        return "auth/jobs/list";
    }
}
