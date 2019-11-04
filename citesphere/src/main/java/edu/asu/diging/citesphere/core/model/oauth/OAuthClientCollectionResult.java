package edu.asu.diging.citesphere.core.model.oauth;

import java.util.List;

import edu.asu.diging.citesphere.core.model.IOAuthClient;

public class OAuthClientCollectionResult {
    
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
