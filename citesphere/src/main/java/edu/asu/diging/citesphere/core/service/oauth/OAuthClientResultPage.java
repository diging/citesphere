package edu.asu.diging.citesphere.core.service.oauth;

import java.util.List;

import edu.asu.diging.citesphere.core.model.oauth.IOAuthClient;

public class OAuthClientResultPage {
    
    private long totalPages;
    private List<IOAuthClient> clientList;
    
    public long getTotalPages() {
        return totalPages;
    }
    public void setTotalPages(long totalPages) {
        this.totalPages = totalPages;
    }
    public List<IOAuthClient> getClientList() {
        return clientList;
    }
    public void setClientList(List<IOAuthClient> clientList) {
        this.clientList = clientList;
    }
}
