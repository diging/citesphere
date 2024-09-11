package edu.asu.diging.citesphere.core.util.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.asu.diging.citesphere.core.util.IGilesUtil;
import edu.asu.diging.citesphere.model.bib.IGilesUpload;

@Component
public class GilesUtil implements IGilesUtil {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void createJobObjectNode(ObjectNode root, IGilesUpload job) {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode filesNode = root.putArray("jobs");
        ObjectNode jobNode = mapper.createObjectNode();
        jobNode.put("jobId", job.getProgressId());
        filesNode.add(jobNode);
    }
}
