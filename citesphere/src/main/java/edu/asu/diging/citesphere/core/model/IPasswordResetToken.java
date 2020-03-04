
package edu.asu.diging.citesphere.core.model;

import java.time.OffsetDateTime;

import edu.asu.diging.citesphere.user.IUser;

public interface IPasswordResetToken {

    Long getId();

    void setId(Long id);

    String getToken();

    void setToken(String token);

    IUser getUser();

    void setUser(IUser user);

    OffsetDateTime getExpiryDate();

    void setExpiryDate(OffsetDateTime expiryDate);

}