package edu.asu.diging.citesphere.core.service.oauth;

import edu.asu.diging.citesphere.core.model.IOAuthClient;

/**
 * This class is a temporary holder for app client id and secret to be used after
 * creation of a new {@link IOAuthClient}. The generated secret should not be stored
 * unencypted.
 * 
 * @author jdamerow
 *
 */
public class OAuthCredentials {

    private String clientId;
    
    private String secret;
    
    public OAuthCredentials(String clientId, String secret) {
        super();
        this.clientId = clientId;
        this.secret = secret;
    }
    
    public String getClientId() {
        return clientId;
    }
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
    public String getSecret() {
        return secret;
    }
    public void setSecret(String secret) {
        this.secret = secret;
    }
    
}
