package edu.asu.diging.citesphere.core.zotero.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.PropertySource;
import org.springframework.social.oauth1.OAuthToken;
import org.springframework.social.zotero.api.Group;
import org.springframework.social.zotero.api.Item;
import org.springframework.social.zotero.api.Zotero;
import org.springframework.social.zotero.api.ZoteroResponse;
import org.springframework.social.zotero.connect.ZoteroConnectionFactory;
import org.springframework.stereotype.Component;

import edu.asu.diging.citesphere.core.model.IUser;
import edu.asu.diging.citesphere.core.model.IZoteroToken;
import edu.asu.diging.citesphere.core.zotero.IZoteroConnector;

@Component
@PropertySource("classpath:/config.properties")
public class ZoteroConnector implements IZoteroConnector {
    
    @Value("${_zotero_page_size}")
    private Integer zoteroPageSize;
    
    @Autowired
    private ZoteroTokenManager tokenManager;
    
    @Autowired
    private ZoteroConnectionFactory zoteroFactory;    
    
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.service.impl.IZoteroConnector#getGroupItems(edu.asu.diging.citesphere.core.model.IUser, java.lang.String, int)
     */
    @Override
    @Cacheable(value="groupItems", key="#user.username + '_' + #groupId + '_' + #page")
    public ZoteroResponse<Item> getGroupItems(IUser user, String groupId, int page) {
        Zotero zotero = getApi(user);
        if (page < 1) {
            page = 0;
        } else  {
            page = page-1;
        }
        return zotero.getGroupsOperations().getGroupItemsTop(groupId, page*zoteroPageSize, zoteroPageSize);          
    }
    
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.service.impl.IZoteroConnector#getGroups(edu.asu.diging.citesphere.core.model.IUser)
     */
    @Override
    @Cacheable(value="groups", key="#user.username")
    public Group[] getGroups(IUser user) {
        Zotero zotero = getApi(user);
        Group[] info = zotero.getGroupsOperations().getGroups();
        return info;
    }
    
    private Zotero getApi(IUser user) {
        IZoteroToken token = tokenManager.getToken(user);
        Zotero zotero = zoteroFactory.createConnection(new OAuthToken(token.getToken(), token.getSecret())).getApi();
        zotero.setUserId(token.getUserId());
        return zotero;
    }
}
