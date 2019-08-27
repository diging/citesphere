package edu.asu.diging.citesphere.core.kafka.impl;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.asu.diging.citesphere.core.exceptions.MessageCreationException;
import edu.asu.diging.citesphere.core.kafka.IJsonMessageCreator;
import edu.asu.diging.citesphere.messages.model.KafkaJobMessage;

@Component
public class JsonMessageCreator implements IJsonMessageCreator {

    /* (non-Javadoc)
     * @see edu.asu.giles.service.kafka.impl.IJsonMessageCreator#createMessage(edu.asu.giles.service.requests.IRequest)
     */
    @Override
    public String createMessage(KafkaJobMessage msg) throws MessageCreationException {
        ObjectMapper mapper = new ObjectMapper(); 
        try {
            return mapper.writeValueAsString(msg);
        } catch (JsonProcessingException e) {
            throw new MessageCreationException("Could not create JSON.", e);
        }
    }
}