package edu.asu.diging.citesphere.core.service.giles.impl;

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
}