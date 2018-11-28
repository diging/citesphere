package edu.asu.diging.citesphere.core.model.impl;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import edu.asu.diging.citesphere.core.model.IUser;
import edu.asu.diging.citesphere.core.model.IZoteroToken;

@Entity(name="tokens")
public class ZoteroToken implements IZoteroToken {

    @Id
    @GeneratedValue(generator = "token-id-generator")
    @GenericGenerator(name = "token-id-generator",    
                    parameters = @Parameter(name = "prefix", value = "TO"), 
                    strategy = "edu.asu.diging.citesphere.core.repository.IdGenerator"
            )
    private String id;
    private String userId;
    private String token;
    private String secret;
    @OneToOne(targetEntity=User.class)
    private IUser user;
    
    
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.impl.IZoteroToken#getUserId()
     */
    @Override
    public String getUserId() {
        return userId;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.impl.IZoteroToken#setUserId(java.lang.String)
     */
    @Override
    public void setUserId(String userId) {
        this.userId = userId;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.impl.IZoteroToken#getToken()
     */
    @Override
    public String getToken() {
        return token;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.impl.IZoteroToken#setToken(java.lang.String)
     */
    @Override
    public void setToken(String token) {
        this.token = token;
    }
    @Override
    public String getSecret() {
        return secret;
    }
    @Override
    public void setSecret(String secret) {
        this.secret = secret;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.impl.IZoteroToken#getUser()
     */
    @Override
    public IUser getUser() {
        return user;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.impl.IZoteroToken#setUser(edu.asu.diging.citesphere.core.model.IUser)
     */
    @Override
    public void setUser(IUser user) {
        this.user = user;
    }
    
}
