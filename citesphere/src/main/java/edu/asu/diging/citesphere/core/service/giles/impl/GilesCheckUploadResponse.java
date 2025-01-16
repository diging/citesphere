package edu.asu.diging.citesphere.core.service.giles.impl;

public class GilesCheckUploadResponse {

    private String msg;
    private String uploadUrl;
    private String uploadId;
    private String msgCode;
    
    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
    public String getUploadUrl() {
        return uploadUrl;
    }
    public void setUploadUrl(String uploadUrl) {
        this.uploadUrl = uploadUrl;
    }
    public String getUploadId() {
        return uploadId;
    }
    public void setUploadId(String uploadId) {
        this.uploadId = uploadId;
    }
    public String getMsgCode() {
        return msgCode;
    }
    public void setMsgCode(String msgCode) {
        this.msgCode = msgCode;
    }
    
}
