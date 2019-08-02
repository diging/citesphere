package edu.asu.diging.citesphere.core.service.jobs.impl;

import java.io.IOException;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.asu.diging.citesphere.core.model.jobs.IUploadJob;
import edu.asu.diging.citesphere.core.model.jobs.JobStatus;
import edu.asu.diging.citesphere.core.model.jobs.impl.JobPhase;
import edu.asu.diging.citesphere.core.model.jobs.impl.UploadJob;
import edu.asu.diging.citesphere.core.repository.jobs.UploadJobRepository;
import edu.asu.diging.citesphere.core.service.jobs.IJobCompleteProcessor;
import edu.asu.diging.citesphere.core.service.jobs.IUploadJobManager;
import edu.asu.diging.citesphere.messages.model.KafkaImportReturnMessage;
import edu.asu.diging.citesphere.messages.model.ResponseCode;
import edu.asu.diging.citesphere.messages.model.Status;

@Service
public class JobCompleteProcessor implements IJobCompleteProcessor {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private IUploadJobManager jobManager;
    
    @Autowired
    private UploadJobRepository jobRepository;
    
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.service.jobs.impl.IJobCompleteProcessor#jobComplete(edu.asu.diging.citesphere.messages.model.KafkaImportReturnMessage)
     */
    @Override
    public void jobComplete(KafkaImportReturnMessage msg) {
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String[] parts = msg.getJobId().split("\\."); 
        String payload = new String(decoder.decode(parts[1]));
        
        ObjectMapper mapper = new ObjectMapper();
        TokenPayload tokenPayload = null;
        try {
            tokenPayload = mapper.readValue(payload, TokenPayload.class);
        } catch (IOException e) {
            logger.error("Could not unmarshall message.", e);            
        }
        
        if (tokenPayload != null && tokenPayload.getSub() != null) {
            IUploadJob job = jobManager.findUploadJobFullyLoaded(tokenPayload.getSub());
            if (job != null) {
                JobPhase phase = new JobPhase();
                if (msg.getStatus() == Status.DONE) {
                    job.setStatus(JobStatus.DONE);
                    phase.setStatus(JobStatus.DONE);
                    try {
                        phase.setMessage(mapper.writeValueAsString(msg.getZoteroResponse()));
                    } catch (JsonProcessingException e) {
                        // should never happen
                        logger.error("Could not store response message.", e);
                    }
                } else if (msg.getStatus() == Status.FAILED) {
                    job.setStatus(JobStatus.FAILURE);
                    phase.setStatus(JobStatus.FAILURE);
                    if (msg.getCode() == ResponseCode.X10) {
                        phase.setMessage(msg.getCode() + " - uploader could not authenticate with Citephere.");
                    } else if (msg.getCode() == ResponseCode.X20) {
                        phase.setMessage(msg.getCode() + " - uploader could not download required file from Citephere.");
                    }
                } else if (msg.getStatus() == Status.PROCESSING) {
                    job.setStatus(JobStatus.STARTED);
                    phase.setStatus(JobStatus.STARTED);
                }
                
                job.getPhases().add(phase);
                
                jobRepository.save((UploadJob)job);
            }
        }
        
    }
    
}
