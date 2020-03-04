package edu.asu.diging.citesphere.core.service.jobs;

import edu.asu.diging.citesphere.messages.model.KafkaExportReturnMessage;
import edu.asu.diging.citesphere.messages.model.KafkaImportReturnMessage;

public interface IJobCompleteProcessor {

    void jobComplete(KafkaImportReturnMessage msg);

    void jobComplete(KafkaExportReturnMessage msg);

}