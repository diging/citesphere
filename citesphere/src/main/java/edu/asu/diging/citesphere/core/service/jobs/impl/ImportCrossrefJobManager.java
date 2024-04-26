package edu.asu.diging.citesphere.core.service.jobs.impl;

import java.time.OffsetDateTime;
import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import edu.asu.diging.citesphere.core.exceptions.GroupDoesNotExistException;
import edu.asu.diging.citesphere.core.exceptions.MessageCreationException;
import edu.asu.diging.citesphere.core.kafka.IKafkaRequestProducer;
import edu.asu.diging.citesphere.core.model.jobs.IImportCrossrefJob;
import edu.asu.diging.citesphere.core.model.jobs.JobStatus;
import edu.asu.diging.citesphere.core.model.jobs.impl.ImportCrossrefJob;
import edu.asu.diging.citesphere.core.model.jobs.impl.JobPhase;
import edu.asu.diging.citesphere.core.repository.jobs.ImportCrossrefJobRepository;
import edu.asu.diging.citesphere.core.service.IGroupManager;
import edu.asu.diging.citesphere.core.service.jobs.IImportCrossrefJobManager;
import edu.asu.diging.citesphere.core.service.jwt.IJwtTokenService;
import edu.asu.diging.citesphere.messages.KafkaTopics;
import edu.asu.diging.citesphere.messages.model.KafkaJobMessage;
import edu.asu.diging.citesphere.model.bib.ICitationGroup;
import edu.asu.diging.citesphere.user.IUser;

@Service
@Transactional
@PropertySource("classpath:/config.properties")
public class ImportCrossrefJobManager implements IImportCrossrefJobManager {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Value("${_job_page_size}")
    private int jobPageSize;
    
    @Autowired
    private IGroupManager groupManager;
    
    @Autowired
    private ImportCrossrefJobRepository jobRepo;
    
    @Autowired
    private IKafkaRequestProducer kafkaProducer;
    
    @Autowired
    private IJwtTokenService tokenService;
    
    @Override
    public IImportCrossrefJob createJob(IUser user, String groupId, List<String> dois) throws GroupDoesNotExistException {
        ICitationGroup group = groupManager.getGroup(user, groupId);
        if (group == null) {
            throw new GroupDoesNotExistException();
        }
        
        IImportCrossrefJob job = new ImportCrossrefJob();
        job.setCreatedOn(OffsetDateTime.now());
        job.setUsername(user.getUsername());
        job.setDois(dois);
        job.setCitationGroup(groupId);
        job.setStatus(JobStatus.PREPARED);
        jobRepo.save((ImportCrossrefJob)job);
        
        String token = tokenService.generateJobApiToken(job);
        try {
            kafkaProducer.sendRequest(new KafkaJobMessage(token), KafkaTopics.REFERENCES_IMPORT_CROSSREF_TOPIC);
        } catch (MessageCreationException e) {
            logger.error("Could not send Kafka message.", e);
            job.setStatus(JobStatus.FAILURE);
            job.getPhases().add(new JobPhase(JobStatus.FAILURE, e.getMessage()));
            jobRepo.save((ImportCrossrefJob)job);
        }
        
        return job;
    }
}
