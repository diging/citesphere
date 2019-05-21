package edu.asu.diging.citesphere.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:/config.properties")
public class KafkaConfig {
    
    @Value("${_kafka_hosts}")
    private String hosts;
    
    @Value("${_producer_id}")
    private String producerId;

    public String getHosts() {
        return hosts;
    }
    
    public String getProducerId() {
        return producerId;
    }
}