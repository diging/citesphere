package edu.asu.diging.citesphere.core.kafka.impl;

public class KafkaJobMessage {

    private String jobToken;
    
    public KafkaJobMessage(String jobToken) {
        this.jobToken = jobToken;
    }

    public String getId() {
        return jobToken;
    }

    public void setId(String id) {
        this.jobToken = id;
    }
}
