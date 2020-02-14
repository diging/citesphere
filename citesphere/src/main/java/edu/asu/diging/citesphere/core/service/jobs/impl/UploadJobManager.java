package edu.asu.diging.citesphere.core.service.jobs.impl;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import edu.asu.diging.citesphere.core.exceptions.FileStorageException;
import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.exceptions.MessageCreationException;
import edu.asu.diging.citesphere.core.kafka.IKafkaRequestProducer;
import edu.asu.diging.citesphere.core.model.jobs.IJob;
import edu.asu.diging.citesphere.core.model.jobs.IUploadJob;
import edu.asu.diging.citesphere.core.model.jobs.JobStatus;
import edu.asu.diging.citesphere.core.model.jobs.impl.JobPhase;
import edu.asu.diging.citesphere.core.model.jobs.impl.UploadJob;
import edu.asu.diging.citesphere.core.repository.jobs.UploadJobRepository;
import edu.asu.diging.citesphere.core.service.IGroupManager;
import edu.asu.diging.citesphere.core.service.jobs.IUploadJobManager;
import edu.asu.diging.citesphere.core.service.jwt.IJwtTokenService;
import edu.asu.diging.citesphere.core.service.upload.IFileStorageManager;
import edu.asu.diging.citesphere.messages.KafkaTopics;
import edu.asu.diging.citesphere.messages.model.KafkaJobMessage;
import edu.asu.diging.citesphere.model.IUser;
import edu.asu.diging.citesphere.model.bib.ICitationGroup;

@Service
@Transactional
@PropertySource("classpath:/config.properties")
public class UploadJobManager implements IUploadJobManager {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Value("${_job_page_size}")
    private int jobPageSize;

    @Autowired
    private UploadJobRepository jobRepository;

    @Autowired
    private IFileStorageManager fileManager;

    @Autowired
    private IKafkaRequestProducer kafkaProducer;
    
    @Autowired
    private IJwtTokenService tokenService;
    
    @Autowired
    private IGroupManager groupManager;

    /*
     * (non-Javadoc)
     * 
     * @see edu.asu.diging.citesphere.core.service.jobs.impl.IUploadJobManager#
     * findUploadJob(java.lang.String)
     */
    @Override
    public IUploadJob findUploadJob(String id) {
        Optional<UploadJob> jobOptional = jobRepository.findById(id);
        if (jobOptional.isPresent()) {
            IJob job = jobOptional.get();
            if (IUploadJob.class.isAssignableFrom(job.getClass())) {
                return (IUploadJob) job;
            }
        }
        return null;
    }
    
    @Override
    public IUploadJob findUploadJobFullyLoaded(String id) {
        Optional<UploadJob> jobOptional = jobRepository.findById(id);
        if (jobOptional.isPresent()) {
            IJob job = jobOptional.get();
            // load phases
            job.getPhases().size();
            if (IUploadJob.class.isAssignableFrom(job.getClass())) {
                return (IUploadJob) job;
            }
        }
        return null;
    }

    @Override
    public List<IUploadJob> createUploadJob(IUser user, MultipartFile[] files, List<byte[]> fileBytes, String groupId) throws GroupDoesNotExistException {
        ICitationGroup group = groupManager.getGroup(user, groupId);
        if (group == null) {
            throw new GroupDoesNotExistException();
        }
        
        List<IUploadJob> jobs = new ArrayList<>();
        int i = 0;
        for (MultipartFile f : files) {
            String filename = f.getOriginalFilename();

            byte[] bytes = null;
            UploadJob job = new UploadJob();
            jobs.add(job);
            job.setFilename(filename);
            job.setCreatedOn(OffsetDateTime.now());
            job.setUsername(user.getUsername());
            job.setCitationGroup(groupId);
            job.setPhases(new ArrayList<>());
            try {
                if (fileBytes != null && fileBytes.size() == files.length) {
                    bytes = fileBytes.get(i);
                } else {
                    job.setStatus(JobStatus.FAILURE);
                    job.getPhases().add(new JobPhase(JobStatus.FAILURE,
                            "There is a mismatch between file metadata and file contents."));
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
                continue;
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
            String token = tokenService.generateJobApiToken(job);
            try {
                kafkaProducer.sendRequest(new KafkaJobMessage(token), KafkaTopics.REFERENCES_IMPORT_TOPIC);
            } catch (MessageCreationException e) {
                logger.error("Could not send Kafka message.", e);
                job.setStatus(JobStatus.FAILURE);
                job.getPhases().add(new JobPhase(JobStatus.FAILURE, e.getMessage()));
                jobRepository.save(job);
            }
        }

        return jobs;
    }
    
    @Override
    public byte[] getUploadedFile(IUploadJob job) {
        String folderPath = fileManager.getFolderPath(job.getUsername(), job.getId());
        try {
            return fileManager.getFileContentFromUrl(new URL("file:" + folderPath + File.separator + job.getFilename()));
        } catch (IOException e) {
            logger.error("Could not retrieve uploaded file.", e);
            return null;
        }
    }
    
    @Override
    public List<IUploadJob> getUploadJobs(IUser user, int page) {
        List<IUploadJob> results = new ArrayList<>();
        jobRepository.findByUsername(user.getUsername(), PageRequest.of(page, jobPageSize, Sort.by(Direction.DESC, "createdOn", "id"))).forEach(job -> {
            job.setCitationGroupDetail(groupManager.getGroup(user, job.getCitationGroup()));
            results.add(job); 
        });
        return results;
    }
}
