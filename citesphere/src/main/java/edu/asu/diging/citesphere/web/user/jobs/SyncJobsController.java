package edu.asu.diging.citesphere.web.user.jobs;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.format.annotation.DateTimeFormat;

import edu.asu.diging.citesphere.core.service.jobs.ISyncJobManager;
import edu.asu.diging.citesphere.user.IUser;

@Controller
public class SyncJobsController {

    @Autowired
    private ISyncJobManager jobManager;

    @RequestMapping("/auth/jobs/sync/list")
    public String list(Model model, @PageableDefault(sort = { "createdOn" }, direction = Direction.DESC) Pageable page,
            Authentication authentication) {
        long total = jobManager.getJobsCount((IUser) authentication.getPrincipal());
        if (total == -1) {
            return "redirect:/";
        }
        model.addAttribute("jobs", jobManager.getJobs((IUser) authentication.getPrincipal(), page));
        long totalPages = total > 0 ? (long) Math.ceil((double) total / page.getPageSize()) : 1;
        model.addAttribute("total", totalPages);
        model.addAttribute("page", Math.max(1, Math.min(page.getPageNumber() + 1, totalPages)));
        return "auth/jobs/list";
    }
    
    @RequestMapping(value = "/auth/jobs/sync/prune", method = RequestMethod.POST)
    public String prune(@RequestParam("pruneDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate pruneDate, Authentication authentication,
            RedirectAttributes redirectAttributes) {
        OffsetDateTime pruneBefore = pruneDate.plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC);
        long deleted = jobManager.pruneJobs((IUser) authentication.getPrincipal(), pruneBefore);
        
        redirectAttributes.addFlashAttribute("show_alert", true);
        redirectAttributes.addFlashAttribute("alert_type", "success");
        redirectAttributes.addFlashAttribute("alert_msg",
                String.format("%d job(s) deleted up to and including %s.", deleted, pruneDate));
        
        return "redirect:/auth/jobs/sync/list";
    }
}
