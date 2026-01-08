package edu.asu.diging.citesphere.web.user;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.asu.diging.citesphere.core.model.jobs.impl.GroupSyncJob;
import edu.asu.diging.citesphere.core.repository.jobs.GroupSyncJobRepository;

/**
 * REST controller for checking job status.
 * Provides a polling endpoint for the UI to track job progress and cancellation.
 */
@Controller
public class JobStatusController {

    @Autowired
    private GroupSyncJobRepository jobRepo;

    /**
     * Get the current status of a job.
     * Returns job status, progress, and completion time.
     *
     * @param jobId the job ID to check
     * @return JSON response with job details, or 404.
     */
    @RequestMapping(value = "/api/jobs/{jobId}/status", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getJobStatus(@PathVariable String jobId) {
        Optional<GroupSyncJob> job = jobRepo.findById(jobId);
        if (!job.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("status", job.get().getStatus().toString());
        response.put("current", job.get().getCurrent());
        response.put("total", job.get().getTotal());
        if (job.get().getFinishedOn() != null) {
            response.put("finishedOn", job.get().getFinishedOn().toString());
        }

        return ResponseEntity.ok(response);
    }
}
