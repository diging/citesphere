package edu.asu.diging.citesphere.core.kafka;

import edu.asu.diging.citesphere.core.exceptions.MessageCreationException;
import edu.asu.diging.citesphere.messages.model.KafkaJobMessage;

public interface IJsonMessageCreator {

    public String createMessage(KafkaJobMessage ms) throws MessageCreationException;
}