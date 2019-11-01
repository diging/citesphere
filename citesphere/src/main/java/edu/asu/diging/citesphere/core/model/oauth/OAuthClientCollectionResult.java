package edu.asu.diging.citesphere.core.model.oauth;

import java.util.List;

import edu.asu.diging.citesphere.core.model.IOAuthClient;

public class OAuthClientCollectionResult {
    
    private long totalResults;
    private List<IOAuthClient> clientList;
    
    public long getTotalResults() {
        return totalResults;
    }
    public void setTotalResults(long totalResults) {
        this.totalResults = totalResults;
    }
    public List<IOAuthClient> getClientList() {
        return clientList;
    }
    public void setClientList(List<IOAuthClient> clientList) {
        this.clientList = clientList;
    }
}
