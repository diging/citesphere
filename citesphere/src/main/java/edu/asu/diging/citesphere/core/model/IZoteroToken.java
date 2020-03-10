package edu.asu.diging.citesphere.core.model;

import edu.asu.diging.citesphere.user.IUser;

public interface IZoteroToken {

    String getUserId();

    void setUserId(String userId);

    String getToken();

    void setToken(String token);

    IUser getUser();

    void setUser(IUser user);

    void setSecret(String secret);

    String getSecret();

}