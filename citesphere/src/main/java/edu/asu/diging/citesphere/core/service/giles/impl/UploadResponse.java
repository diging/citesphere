package edu.asu.diging.citesphere.core.service.giles.impl;

import java.io.IOException;
import java.util.List;

import org.javers.common.collections.Arrays;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

class UploadResponse {
    private String id;
    private String checkUrl;
    private String documentIds;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCheckUrl() {
        return checkUrl;
    }

    public void setCheckUrl(String checkUrl) {
        this.checkUrl = checkUrl;
    }

    public String getDocumentIds() {
        return documentIds;
    }

    public void setDocumentIds(String documentIds) {
        this.documentIds = documentIds;
    }
    
//    public void setDocumentIds(String documentIds) throws JsonParseException, JsonMappingException, IOException {
//        ObjectMapper mapper = new ObjectMapper();
//        this.documentIds = Arrays.asList(mapper.readValue(documentIds, String[].class));
//    }

    @Override
    public String toString() {
        return "UploadResponse [id=" + id + ", checkUrl=" + checkUrl + ", documentIds=" + documentIds + "]";
    }
    
    
}