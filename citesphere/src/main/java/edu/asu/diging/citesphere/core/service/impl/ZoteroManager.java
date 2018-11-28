package edu.asu.diging.citesphere.core.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.social.oauth1.OAuthToken;
import org.springframework.social.zotero.api.Group;
import org.springframework.social.zotero.api.Zotero;
import org.springframework.social.zotero.connect.ZoteroConnectionFactory;
import org.springframework.stereotype.Service;

import edu.asu.diging.citesphere.core.model.IUser;
import edu.asu.diging.citesphere.core.model.IZoteroToken;
import edu.asu.diging.citesphere.core.service.IZoteroManager;

@Service
@PropertySource("classpath:/config.properties")
public class ZoteroManager implements IZoteroManager {

    @Autowired
    private ZoteroConnectionFactory zoteroFactory;
    
    @Autowired
    private ZoteroTokenManager tokenManager;
    
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.service.impl.IZoteroManager#getGroupItems(edu.asu.diging.citesphere.core.model.IUser)
     */
    @Override
    public Group[] getGroupItems(IUser user) {
        IZoteroToken token = tokenManager.getToken(user);
        Zotero zotero = zoteroFactory.createConnection(new OAuthToken(token.getToken(), token.getSecret())).getApi();
        zotero.setUserId(token.getUserId());
        Group[] info = zotero.getGroupsOperations().getGroups();
        return info;
    }
}
