package edu.asu.diging.citesphere.core.service.jobs.impl;

import java.io.IOException;
import java.util.Base64;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.asu.diging.citesphere.core.export.ExportFinishedCallback;
import edu.asu.diging.citesphere.core.export.IExportJobManager;
import edu.asu.diging.citesphere.core.export.IExportManager;
import edu.asu.diging.citesphere.core.export.IExportTaskManager;
import edu.asu.diging.citesphere.core.model.jobs.IExportJob;
import edu.asu.diging.citesphere.core.model.jobs.IUploadJob;
import edu.asu.diging.citesphere.core.model.jobs.JobStatus;
import edu.asu.diging.citesphere.core.model.jobs.impl.JobPhase;
import edu.asu.diging.citesphere.core.model.jobs.impl.UploadJob;
import edu.asu.diging.citesphere.core.repository.jobs.UploadJobRepository;
import edu.asu.diging.citesphere.core.service.jobs.IJobCompleteProcessor;
import edu.asu.diging.citesphere.core.service.jobs.IUploadJobManager;
import edu.asu.diging.citesphere.messages.model.KafkaExportReturnMessage;
import edu.asu.diging.citesphere.messages.model.KafkaImportReturnMessage;
import edu.asu.diging.citesphere.messages.model.KafkaReturnMessage;
import edu.asu.diging.citesphere.messages.model.ResponseCode;
import edu.asu.diging.citesphere.messages.model.Status;

@Service
@Transactional
public class JobCompleteProcessor implements IJobCompleteProcessor {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private IUploadJobManager jobManager;
    
    @Autowired
    private UploadJobRepository jobRepository;
    
    @Autowired
    private IExportJobManager exportJobManager;
    
    @Autowired
    private IExportTaskManager exportTaskManager;
    
    @Autowired
    private ExportFinishedCallback exportManager;

    
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.service.jobs.impl.IJobCompleteProcessor#jobComplete(edu.asu.diging.citesphere.messages.model.KafkaImportReturnMessage)
     */
    @Override
    public void jobComplete(KafkaImportReturnMessage msg) {
        TokenPayload tokenPayload = getPayload(msg);
        
        if (tokenPayload != null && tokenPayload.getSub() != null) {
            IUploadJob job = jobManager.findUploadJobFullyLoaded(tokenPayload.getSub());
            if (job != null) {
                JobPhase phase = new JobPhase();
                if (msg.getStatus() == Status.DONE) {
                    job.setStatus(JobStatus.DONE);
                    phase.setStatus(JobStatus.DONE);
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        phase.setMessage(mapper.writeValueAsString(msg.getZoteroResponse()));
                    } catch (JsonProcessingException e) {
                        // should never happen
                        logger.error("Could not store response message.", e);
                    }
                } else if (msg.getStatus() == Status.FAILED) {
                    job.setStatus(JobStatus.FAILURE);
                    phase.setStatus(JobStatus.FAILURE);
                    setPhaseMessage(phase, msg);
                } else if (msg.getStatus() == Status.PROCESSING) {
                    job.setStatus(JobStatus.STARTED);
                    phase.setStatus(JobStatus.STARTED);
                }
                
                job.getPhases().add(phase);
                
                jobRepository.save((UploadJob)job);
            }
        }
        
    }
    
    @Override
    public void jobComplete(KafkaExportReturnMessage msg) {
        TokenPayload tokenPayload = getPayload(msg);
        
        if (tokenPayload != null && tokenPayload.getSub() != null) {
            IExportJob job = exportJobManager.getExportJob(tokenPayload.getSub());
            if (job != null) {
                JobPhase phase = new JobPhase();
                if (msg.getStatus() == Status.DONE) {
                    job.setStatus(JobStatus.DONE);
                    phase.setStatus(JobStatus.DONE);
                } else if (msg.getStatus() == Status.FAILED) {
                    job.setStatus(JobStatus.FAILURE);
                    phase.setStatus(JobStatus.FAILURE);
                    setPhaseMessage(phase, msg);
                } else if (msg.getStatus() == Status.PROCESSING) {
                    job.setStatus(JobStatus.STARTED);
                    phase.setStatus(JobStatus.STARTED);
                } else if (msg.getStatus() == Status.SYNCING_RETRY) {
                    job.setStatus(JobStatus.SYNCING);
                    phase.setStatus(JobStatus.SYNCING);
                    exportManager.retryExport(job);
                }
                
                job.getPhases().add(phase);
                
                exportJobManager.save(job);
                exportTaskManager.updateTask(job);
            }
        }
    }
    
    private TokenPayload getPayload(KafkaReturnMessage msg) {
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String[] parts = msg.getJobId().split("\\."); 
        String payload = new String(decoder.decode(parts[1]));
        
        ObjectMapper mapper = new ObjectMapper();
        TokenPayload tokenPayload = null;
        try {
            tokenPayload = mapper.readValue(payload, TokenPayload.class);
        } catch (IOException e) {
            logger.error("Could not unmarshall message.", e);    
            return null;
        }
        
        return tokenPayload;
    }
    
    private void setPhaseMessage(JobPhase phase, KafkaReturnMessage msg) {
        if (msg.getCode() == ResponseCode.X00) {
            phase.setMessage(msg.getCode() + " - request could not be processed due to an internal error.");
        } else if (msg.getCode() == ResponseCode.X10) {
            phase.setMessage(msg.getCode() + " - uploader could not authenticate with Citephere.");
        } else if (msg.getCode() == ResponseCode.X20) {
            phase.setMessage(msg.getCode() + " - uploader could not download required file from Citephere.");
        } else if (msg.getCode() == ResponseCode.X30) {
            phase.setMessage(msg.getCode() + " - file format cannot be handled.");
        } else if (msg.getCode() == ResponseCode.X40) {
            phase.setMessage(msg.getCode() + " - could not download citations from Zotero.");
        } else if (msg.getCode() == ResponseCode.W10) {
            phase.setMessage(msg.getCode() + " - an error occured but processing might still have finished successfully.");
        }
    }
    
}
