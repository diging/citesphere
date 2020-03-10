package edu.asu.diging.citesphere.core.model.impl;

import java.time.OffsetDateTime;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;

import edu.asu.diging.citesphere.core.model.IPasswordResetToken;
import edu.asu.diging.citesphere.user.IUser;
import edu.asu.diging.citesphere.user.impl.User;

@Entity
public class PasswordResetToken implements IPasswordResetToken {

    @Id
    @GeneratedValue(generator="token_generator", strategy=GenerationType.SEQUENCE)
    @SequenceGenerator(name="token_generator")
    private Long id;
    
    private String token;
    
    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private IUser user;
  
    private OffsetDateTime expiryDate;

    /* (non-Javadoc)
     * @see edu.asu.diging.simpleusers.core.model.impl.IPasswordResetToken#getId()
     */
    @Override
    public Long getId() {
        return id;
    }

    /* (non-Javadoc)
     * @see edu.asu.diging.simpleusers.core.model.impl.IPasswordResetToken#setId(java.lang.Long)
     */
    @Override
    public void setId(Long id) {
        this.id = id;
    }

    /* (non-Javadoc)
     * @see edu.asu.diging.simpleusers.core.model.impl.IPasswordResetToken#getToken()
     */
    @Override
    public String getToken() {
        return token;
    }

    /* (non-Javadoc)
     * @see edu.asu.diging.simpleusers.core.model.impl.IPasswordResetToken#setToken(java.lang.String)
     */
    @Override
    public void setToken(String token) {
        this.token = token;
    }

    /* (non-Javadoc)
     * @see edu.asu.diging.simpleusers.core.model.impl.IPasswordResetToken#getUser()
     */
    @Override
    public IUser getUser() {
        return user;
    }

    /* (non-Javadoc)
     * @see edu.asu.diging.simpleusers.core.model.impl.IPasswordResetToken#setUser(edu.asu.diging.simpleusers.core.model.IUser)
     */
    @Override
    public void setUser(IUser user) {
        this.user = user;
    }

    /* (non-Javadoc)
     * @see edu.asu.diging.simpleusers.core.model.impl.IPasswordResetToken#getExpiryDate()
     */
    @Override
    public OffsetDateTime getExpiryDate() {
        return expiryDate;
    }

    /* (non-Javadoc)
     * @see edu.asu.diging.simpleusers.core.model.impl.IPasswordResetToken#setExpiryDate(java.time.OffsetDateTime)
     */
    @Override
    public void setExpiryDate(OffsetDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }
    
}