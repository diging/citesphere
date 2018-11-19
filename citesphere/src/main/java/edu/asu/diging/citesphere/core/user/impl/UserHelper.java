package edu.asu.diging.citesphere.core.user.impl;

import org.springframework.social.connect.Connection;
import org.springframework.social.connect.UserProfile;
import org.springframework.stereotype.Service;

import edu.asu.diging.citesphere.core.user.IUserHelper;

@Service
public class UserHelper implements IUserHelper {
    
    /* (non-Javadoc)
     * @see edu.asu.giles.config.IUserHelper#createUser(org.springframework.social.connect.Connection)
     */
    @Override
    public String createUser(Connection<?> connection) {
        UserProfile profile = connection.fetchUserProfile();
        return profile.getUsername() + "_" + connection.getKey().getProviderId();
    }
}