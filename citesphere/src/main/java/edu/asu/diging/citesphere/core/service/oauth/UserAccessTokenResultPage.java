package edu.asu.diging.citesphere.core.service.oauth;

import java.util.List;

import edu.asu.diging.citesphere.core.model.oauth.IOAuthClient;
import edu.asu.diging.citesphere.core.model.oauth.IUserAccessToken;

public class UserAccessTokenResultPage {
    private long totalPages;
    private List<IOAuthClient> accessTokenList;
    
    public long getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(long totalPages) {
        this.totalPages = totalPages;
    }
    
    public List<IOAuthClient> getClientList() {
        return accessTokenList;
    }
    
    public void setClientList(List<IOAuthClient> accessTokenList) {
        this.accessTokenList = accessTokenList;
    }
}
