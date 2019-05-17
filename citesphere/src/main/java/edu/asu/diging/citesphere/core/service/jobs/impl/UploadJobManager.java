package edu.asu.diging.citesphere.core.service.jobs.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import edu.asu.diging.citesphere.core.exceptions.FileStorageException;
import edu.asu.diging.citesphere.core.model.IUser;
import edu.asu.diging.citesphere.core.model.jobs.IUploadJob;
import edu.asu.diging.citesphere.core.model.jobs.JobStatus;
import edu.asu.diging.citesphere.core.model.jobs.impl.Job;
import edu.asu.diging.citesphere.core.model.jobs.impl.JobPhase;
import edu.asu.diging.citesphere.core.model.jobs.impl.UploadJob;
import edu.asu.diging.citesphere.core.repository.jobs.JobRepository;
import edu.asu.diging.citesphere.core.service.jobs.IUploadJobManager;
import edu.asu.diging.citesphere.core.service.upload.IFileStorageManager;

@Service
public class UploadJobManager implements IUploadJobManager {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private IFileStorageManager fileManager;

    /*
     * (non-Javadoc)
     * 
     * @see edu.asu.diging.citesphere.core.service.jobs.impl.IUploadJobManager#
     * findUploadJob(java.lang.String)
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

    @Override
    public List<IUploadJob> createUploadJob(IUser user, MultipartFile[] files, List<byte[]> fileBytes) {
        List<IUploadJob> jobs = new ArrayList<>();
        int i = 0;
        for (MultipartFile f : files) {
            String filename = f.getOriginalFilename();

            byte[] bytes = null;
            UploadJob job = new UploadJob();
            jobs.add(job);
            job.setFilename(filename);
            job.setUsername(user.getUsername());
            job.setPhases(new ArrayList<>());
            try {
                if (fileBytes != null && fileBytes.size() == files.length) {
                    bytes = fileBytes.get(i);
                } else {
                    job.setStatus(JobStatus.FAILURE);
                    job.getPhases().add(new JobPhase(JobStatus.FAILURE, "There is a mismatch between file metadata and file contents."));
                    continue;
                }
                
                if (bytes == null) {
                    job.setStatus(JobStatus.FAILURE);
                    job.getPhases().add(new JobPhase(JobStatus.FAILURE, "There is not file content."));
                    continue;
                }
                job = jobRepository.save(job);
                fileManager.saveFile(user.getUsername(), job.getId(), filename, bytes);
                job.setStatus(JobStatus.PREPARED);
            } catch (FileStorageException e) {
                logger.error("Could not store file.", e);
                job.setStatus(JobStatus.FAILURE);
                job.getPhases().add(new JobPhase(JobStatus.FAILURE, e.getMessage()));
            } finally {
                i++;
                jobRepository.save(job);
            }

            String contentType = null;

            if (bytes != null) {
                Tika tika = new Tika();
                contentType = tika.detect(bytes);
            }

            if (contentType == null) {
                contentType = f.getContentType();
            }

            job.setContentType(contentType);
            job.setFileSize(f.getSize());
            jobRepository.save(job);
        }

        return jobs;
    }
}
