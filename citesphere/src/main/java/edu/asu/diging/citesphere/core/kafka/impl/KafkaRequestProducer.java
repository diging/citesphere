package edu.asu.diging.citesphere.core.kafka.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.stereotype.Service;

import edu.asu.diging.citesphere.config.KafkaConfig;
import edu.asu.diging.citesphere.core.exceptions.MessageCreationException;
import edu.asu.diging.citesphere.core.kafka.IJsonMessageCreator;
import edu.asu.diging.citesphere.core.kafka.IKafkaRequestProducer;
import edu.asu.diging.citesphere.messages.model.KafkaJobMessage;

@Service
public class KafkaRequestProducer implements IKafkaRequestProducer {

    @Autowired
    private KafkaConfig kafkaConfig;

    @Autowired
    private IJsonMessageCreator messageCreator;

    private KafkaTemplate<Integer, String> template;

    @PostConstruct
    public void init() {
        template = createTemplate();
    }

    private KafkaTemplate<Integer, String> createTemplate() {
        Map<String, Object> senderProps = senderProps();
        ProducerFactory<Integer, String> pf = new DefaultKafkaProducerFactory<Integer, String>(senderProps);
        KafkaTemplate<Integer, String> template = new KafkaTemplate<>(pf);
        return template;
    }

    private Map<String, Object> senderProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.CLIENT_ID_CONFIG, kafkaConfig.getProducerId());
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getHosts());
        props.put(ProducerConfig.RETRIES_CONFIG, 0);
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return props;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.giles.service.kafka.impl.IOCRRequestProducer#sendOCRRequest(java.lang
     * .String)
     */
    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.diging.citesphere.core.kafka.impl.IKafkaRequestProducer#sendRequest(
     * edu.asu.diging.citesphere.core.model.jobs.impl.Job, java.lang.String)
     */
    @Override
    public void sendRequest(KafkaJobMessage msg, String topic) throws MessageCreationException {
        template.send(topic, messageCreator.createMessage(msg));
    }
}