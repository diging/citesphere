package edu.asu.diging.citesphere.core.factory.impl;

import org.springframework.stereotype.Component;

import edu.asu.diging.citesphere.core.factory.IZoteroTokenFactory;
import edu.asu.diging.citesphere.core.model.IUser;
import edu.asu.diging.citesphere.core.model.IZoteroToken;
import edu.asu.diging.citesphere.core.model.impl.ZoteroToken;

@Component
public class ZoteroTokenFactory implements IZoteroTokenFactory {

    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.factory.impl.IZoteroTokenFactory#createZoteroToken(java.lang.String, java.lang.String, edu.asu.diging.citesphere.core.model.IUser)
     */
    @Override
    public IZoteroToken createZoteroToken(String token, String secret, String userId, IUser user) {
        IZoteroToken zoteroToken = new ZoteroToken();
        zoteroToken.setToken(token);
        zoteroToken.setUserId(userId);
        zoteroToken.setUser(user);
        zoteroToken.setSecret(secret);
        return zoteroToken;
    }
}
