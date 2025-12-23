package edu.asu.diging.citesphere.core.service.oauth;

/**
 * This class is a temporary holder for personal access token ID and token value
 * to be used after creation of a new token. The token value should only be shown
 * once to the user and not stored unencrypted.
 */
public class PersonalAccessTokenCredentials {

    private String tokenId;
    private String tokenValue;
    private String name;

    public PersonalAccessTokenCredentials(String tokenId, String tokenValue, String name) {
        this.tokenId = tokenId;
        this.tokenValue = tokenValue;
        this.name = name;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public String getTokenValue() {
        return tokenValue;
    }

    public void setTokenValue(String tokenValue) {
        this.tokenValue = tokenValue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
