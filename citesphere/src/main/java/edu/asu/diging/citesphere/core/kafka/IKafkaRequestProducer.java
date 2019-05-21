package edu.asu.diging.citesphere.core.kafka;

import edu.asu.diging.citesphere.core.exceptions.MessageCreationException;
import edu.asu.diging.citesphere.core.kafka.impl.KafkaJobMessage;

public interface IKafkaRequestProducer {

    /* (non-Javadoc)
     * @see edu.asu.giles.service.kafka.impl.IOCRRequestProducer#sendOCRRequest(java.lang.String)
     */
    void sendRequest(KafkaJobMessage msg, String topic) throws MessageCreationException;

}