package edu.asu.diging.citesphere.core.util.model.impl;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import edu.asu.diging.citesphere.core.util.model.IGilesUtil;
import edu.asu.diging.citesphere.model.bib.IGilesUpload;

@Component
public class GilesUtil implements IGilesUtil {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void convertFilesToBytesList(List<byte[]> fileBytes, MultipartFile[] files) {
        for (MultipartFile file : files) {
            try {
                fileBytes.add(file.getBytes());
            } catch (IOException e) {
                logger.error("Could not get file content from request.", e);
                fileBytes.add(null);
            }
        }
    }

    @Override
    public void createJobObjectNode(ObjectNode root, IGilesUpload job) {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode filesNode = root.putArray("jobs");
        ObjectNode jobNode = mapper.createObjectNode();
        jobNode.put("jobId", job.getProgressId());
        filesNode.add(jobNode);
    }
}
