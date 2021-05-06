package edu.asu.diging.citesphere.core.service.jobs.impl;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.social.zotero.exception.ZoteroConnectionException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import edu.asu.diging.citesphere.core.exceptions.AccessForbiddenException;
import edu.asu.diging.citesphere.core.exceptions.CannotFindCitationException;
import edu.asu.diging.citesphere.core.exceptions.CitationIsOutdatedException;
import edu.asu.diging.citesphere.core.exceptions.FileStorageException;
import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.exceptions.MessageCreationException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroHttpStatusException;
import edu.asu.diging.citesphere.core.kafka.IKafkaRequestProducer;
import edu.asu.diging.citesphere.core.model.jobs.IUploadFileJob;
import edu.asu.diging.citesphere.core.model.jobs.JobStatus;
import edu.asu.diging.citesphere.core.model.jobs.impl.JobPhase;
import edu.asu.diging.citesphere.core.model.jobs.impl.UploadFileJob;
import edu.asu.diging.citesphere.core.repository.jobs.UploadFileJobRepository;
import edu.asu.diging.citesphere.core.service.ICitationManager;
import edu.asu.diging.citesphere.core.service.IGroupManager;
import edu.asu.diging.citesphere.core.service.giles.GilesUploadService;
import edu.asu.diging.citesphere.core.service.jobs.IUploadFileJobManager;
import edu.asu.diging.citesphere.core.service.jwt.IJwtTokenService;
import edu.asu.diging.citesphere.core.service.upload.IFileStorageManager;
import edu.asu.diging.citesphere.messages.KafkaTopics;
import edu.asu.diging.citesphere.messages.model.KafkaJobMessage;
import edu.asu.diging.citesphere.model.bib.ICitation;
import edu.asu.diging.citesphere.model.bib.ICitationGroup;
import edu.asu.diging.citesphere.model.bib.IGilesUpload;
import edu.asu.diging.citesphere.user.IUser;

@Service
@Transactional
@PropertySource("classpath:/config.properties")
public class UploadFileJobManager implements IUploadFileJobManager {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private UploadFileJobRepository jobRepository;
    
    @Autowired
    private IKafkaRequestProducer kafkaProducer;
    
    @Autowired
    private IJwtTokenService tokenService;
    
    @Autowired
    private IGroupManager groupManager;

    @Autowired
    private IFileStorageManager fileManager;
    
    @Autowired
    private GilesUploadService gilesUploadService;
    
    @Autowired
    private ICitationManager citationManager;

    
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.service.jobs.impl.IUploadFileJobManager#createUploadJob(edu.asu.diging.citesphere.user.IUser, org.springframework.web.multipart.MultipartFile[], java.util.List, java.lang.String, java.lang.String)
     */
    @Override
    public List<IUploadFileJob> createUploadJob(IUser user, MultipartFile[] files, List<byte[]> fileBytes, String groupId, String itemKey) throws GroupDoesNotExistException {
        ICitationGroup group = groupManager.getGroup(user, groupId);
        if (group == null) {
            throw new GroupDoesNotExistException();
        }
        
        List<IUploadFileJob> jobs = new ArrayList<>();
        int i = 0;
        for (MultipartFile f : files) {
            String filename = f.getOriginalFilename();

            byte[] bytes = null;
            UploadFileJob job = new UploadFileJob();
            jobs.add(job);
            job.setFilename(filename);
            job.setCreatedOn(OffsetDateTime.now());
            job.setUsername(user.getUsername());
            job.setCitationGroup(groupId);
            job.setItemKey(itemKey);
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
                kafkaProducer.sendRequest(new KafkaJobMessage(token), KafkaTopics.FILE_UPLOAD_TOPIC);
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
    public IGilesUpload createGilesJob(IUser user, MultipartFile file, byte[] fileBytes, String groupId, String itemKey) throws GroupDoesNotExistException, AccessForbiddenException, CannotFindCitationException, ZoteroHttpStatusException, ZoteroConnectionException, CitationIsOutdatedException {
        ICitationGroup group = groupManager.getGroup(user, groupId);
        if (group == null) {
            throw new GroupDoesNotExistException();
        }
        
        ICitation citation = citationManager.getCitation(user, groupId, itemKey);
        if (citation != null) {
            IGilesUpload upload = gilesUploadService.uploadFile(user, file, fileBytes);
            if (citation.getGilesUploads() == null) {
                citation.setGilesUploads(new HashSet<>());
            }
            citation.getGilesUploads().add(upload);
            citationManager.updateCitation(user, groupId, citation);
            return upload;
        }
        
        return null;
    }
    
}
