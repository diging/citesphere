package edu.asu.diging.citesphere.core.service.jobs.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.asu.diging.citesphere.core.model.jobs.IUploadJob;
import edu.asu.diging.citesphere.core.model.jobs.impl.Job;
import edu.asu.diging.citesphere.core.repository.jobs.JobRepository;
import edu.asu.diging.citesphere.core.service.jobs.IUploadJobManager;

@Service
public class UploadJobManager implements IUploadJobManager {

    @Autowired
    private JobRepository jobRepository;
    
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.service.jobs.impl.IUploadJobManager#findUploadJob(java.lang.String)
     */
    @Override
    public IUploadJob findUploadJob(String id) {
        Optional<Job> jobOptional = jobRepository.findById(id);
        if (jobOptional.isPresent()) {
            Job job = jobOptional.get();
            if (IUploadJob.class.isAssignableFrom(job.getClass())) {
                return (IUploadJob) job;
            }
        }
        return null;
    }
}
