package edu.asu.diging.citesphere.web.user.jobs;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import edu.asu.diging.citesphere.core.model.jobs.JobStatus;
import edu.asu.diging.citesphere.core.service.ICitationManager;
import edu.asu.diging.citesphere.core.service.jobs.ISyncJobManager;
import edu.asu.diging.citesphere.model.bib.ICitationGroup;
import edu.asu.diging.citesphere.user.IUser;

@Controller
public class SyncJobsController {

    @Autowired
    private ISyncJobManager jobManager;
    
    @Autowired
    private ICitationManager citationManager;

    @RequestMapping("/auth/jobs/sync/list")
    public String list(Model model, @PageableDefault(sort = { "createdOn" }, direction = Direction.DESC) Pageable page,
            Authentication authentication, @RequestParam(name = "groupId", required = false) String groupId,
            @RequestParam(name = "status", required = false) String status) {
        IUser user = (IUser) authentication.getPrincipal();
        List<ICitationGroup> groups = citationManager.getGroups(user);
        model.addAttribute("groups", groups);
        model.addAttribute("statuses", Arrays.asList(JobStatus.values()));
        String selectedGroupId = StringUtils.hasText(groupId) ? groupId : "";
        model.addAttribute("selectedGroupId", selectedGroupId);
        String selectedGroupName = "Group";
        if (StringUtils.hasText(groupId) && groups != null) {
            selectedGroupName = groups.stream()
                .filter(g -> g != null && groupId.equals(g.getGroupId() + ""))
                .findFirst()
                .map(g -> StringUtils.hasText(g.getName()) ? g.getName() : groupId)
                .orElse("Group");
        }
        model.addAttribute("selectedGroupName", selectedGroupName);
        String selectedStatus = StringUtils.hasText(status) ? status : "";
        model.addAttribute("selectedStatus", selectedStatus);
        String selectedStatusName = "Status";
        if (StringUtils.hasText(status)) {
            try {
                JobStatus jobStatus = JobStatus.valueOf(status.toUpperCase());
                selectedStatusName = jobStatus.name();
            } catch (IllegalArgumentException e) {
                selectedStatusName = "Status";
            }
        }
        model.addAttribute("selectedStatusName", selectedStatusName);
        long total = jobManager.getJobsCount(user, groupId, status);
        if (total == -1) {
            return "redirect:/";
        }
        model.addAttribute("jobs", jobManager.getJobs(user, page, groupId, status));
        double pageCount = Math.ceil((double) total / page.getPageSize());
        model.addAttribute("total", pageCount < 1 ? 1 : pageCount);
        model.addAttribute("page", page.getPageNumber() + 1);
        return "auth/jobs/list";
    }
}
