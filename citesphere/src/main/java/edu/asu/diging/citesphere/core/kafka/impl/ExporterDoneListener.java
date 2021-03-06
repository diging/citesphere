package edu.asu.diging.citesphere.core.kafka.impl;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.asu.diging.citesphere.core.service.jobs.IJobCompleteProcessor;
import edu.asu.diging.citesphere.messages.KafkaTopics;
import edu.asu.diging.citesphere.messages.model.KafkaExportReturnMessage;

public class ExporterDoneListener {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private IJobCompleteProcessor jobCompleteProcessor;
    
    
    @KafkaListener(topics = KafkaTopics.REFERENCES_EXPORT_DONE_TOPIC)
    public void receiveMessage(String message) {
        ObjectMapper mapper = new ObjectMapper();
        KafkaExportReturnMessage msg = null;
        try {
            msg = mapper.readValue(message, KafkaExportReturnMessage.class);
        } catch (IOException e) {
            logger.error("Could not unmarshall message.", e);
            return;
        }
        
        jobCompleteProcessor.jobComplete(msg);
    }
}
