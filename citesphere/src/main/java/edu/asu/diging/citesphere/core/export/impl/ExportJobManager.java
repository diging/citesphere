package edu.asu.diging.citesphere.core.export.impl;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.asu.diging.citesphere.core.export.IExportJobManager;
import edu.asu.diging.citesphere.core.model.jobs.IExportJob;
import edu.asu.diging.citesphere.core.model.jobs.JobStatus;
import edu.asu.diging.citesphere.core.model.jobs.impl.ExportJob;
import edu.asu.diging.citesphere.core.repository.jobs.ExportJobRepository;

@Service
public class ExportJobManager implements IExportJobManager {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ExportJobRepository jobRepository;
    
    @Override
    public IExportJob createJob(JobStatus status, String taskId, String username) {
        ExportJob job = new ExportJob();
        job.setCreatedOn(OffsetDateTime.now());
        job.setStatus(status);
        job.setTaskId(taskId);
        job.setUsername(username);
        return job;
    }

    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.export.impl.IExportJobManager#save(edu.asu.diging.citesphere.core.model.jobs.IExportJob)
     */
    @Override
    public IExportJob save(IExportJob job) {
        return jobRepository.save((ExportJob)job);
    }
    
    @Override
    public IExportJob getExportJob(String id) {
        Optional<ExportJob> optional = jobRepository.findById(id);
        if (!optional.isPresent()) {
            logger.warn("Job with id " + id + " does not exist.");
            return null;
        }
        ExportJob job = optional.get();
        // make sure phases are loaded
        job.getPhases().size();
        return job;
    }
    
    @Override
    public IExportJob findByTaskId(String taskId) {
        List<ExportJob> jobs = jobRepository.findByTaskId(taskId);
        if (jobs == null || jobs.isEmpty()) {
            return null;
        }
        
        // for now there is only one job per export
        return jobs.get(0);
    }
}
