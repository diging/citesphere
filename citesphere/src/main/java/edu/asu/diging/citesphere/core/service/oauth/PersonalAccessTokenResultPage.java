package edu.asu.diging.citesphere.core.service.oauth;

import java.util.List;

import edu.asu.diging.citesphere.core.model.oauth.IPersonalAccessToken;

/**
 * Result page for paginated personal access token listings.
 */
public class PersonalAccessTokenResultPage {

    private long totalPages;
    private List<IPersonalAccessToken> tokens;

    public long getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(long totalPages) {
        this.totalPages = totalPages;
    }

    public List<IPersonalAccessToken> getTokens() {
        return tokens;
    }

    public void setTokens(List<IPersonalAccessToken> tokens) {
        this.tokens = tokens;
    }
}
