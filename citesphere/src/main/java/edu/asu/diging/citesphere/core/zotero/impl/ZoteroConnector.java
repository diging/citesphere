package edu.asu.diging.citesphere.core.zotero.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.PropertySource;
import org.springframework.social.oauth1.OAuthToken;
import org.springframework.social.zotero.api.FieldInfo;
import org.springframework.social.zotero.api.Group;
import org.springframework.social.zotero.api.Item;
import org.springframework.social.zotero.api.ItemCreationResponse;
import org.springframework.social.zotero.api.Zotero;
import org.springframework.social.zotero.api.ZoteroResponse;
import org.springframework.social.zotero.connect.ZoteroConnectionFactory;
import org.springframework.social.zotero.exception.ZoteroConnectionException;
import org.springframework.stereotype.Component;

import edu.asu.diging.citesphere.core.exceptions.ZoteroItemCreationFailedException;
import edu.asu.diging.citesphere.core.model.IUser;
import edu.asu.diging.citesphere.core.model.IZoteroToken;
import edu.asu.diging.citesphere.core.zotero.IZoteroConnector;

@Component
@PropertySource("classpath:/config.properties")
public class ZoteroConnector implements IZoteroConnector {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
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
    @Cacheable(value="groupItems", key="#user.username + '_' + #groupId + '_' + #page + '_' + #sortBy")
    public ZoteroResponse<Item> getGroupItems(IUser user, String groupId, int page, String sortBy) {
        Zotero zotero = getApi(user);
        if (page < 1) {
            page = 0;
        } else  {
            page = page-1;
        }
        return zotero.getGroupsOperations().getGroupItemsTop(groupId, page*zoteroPageSize, zoteroPageSize, sortBy);          
    }
    
    @Override
    @Cacheable(value="groupItemsLimit", key="#user.username + '_' + #groupId + '_' + #limit + '_' + #sortBy")
    public ZoteroResponse<Item> getGroupItemsWithLimit(IUser user, String groupId, int limit, String sortBy) {
        Zotero zotero = getApi(user);
        if (limit < 1) {
            limit = 1;
        }
        return zotero.getGroupsOperations().getGroupItemsTop(groupId, 0 , 1, sortBy);          
    }
    
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.service.impl.IZoteroConnector#getGroups(edu.asu.diging.citesphere.core.model.IUser)
     */
    @Override
    @Cacheable(value="groups", key="#user.username")
    public ZoteroResponse<Group> getGroups(IUser user) {
        Zotero zotero = getApi(user);
        return zotero.getGroupsOperations().getGroups();
    }
    
    @Override
    @Cacheable(value="groupCache", key="#user.username + '_' + #groupId", condition="#forceRefresh==false")
    public Group getGroup(IUser user, String groupId, boolean forceRefresh) {
        Zotero zotero = getApi(user);
        return zotero.getGroupsOperations().getGroup(groupId);
    }
    
    @Override
//    @Cacheable(value="groupVersions", key="#user.username")
    public ZoteroResponse<Group> getGroupsVersions(IUser user) {
        Zotero zotero = getApi(user);
        return zotero.getGroupsOperations().getGroupsVersions();
    }
    
    @Override
    public Item getItem(IUser user, String groupId, String itemKey) {
        Zotero zotero = getApi(user);
        return zotero.getGroupsOperations().getGroupItem(groupId, itemKey);
    }
    
    @Override
    public Item updateItem(IUser user, Item item, String groupId, List<String> ignoreFields) throws ZoteroConnectionException {
        Zotero zotero = getApi(user);
        zotero.getGroupsOperations().updateItem(groupId, item, ignoreFields);
        // it seems like Zotero needs a minute to process the submitted data
        // so let's wait a second before retrieving updated data
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            logger.error("Could not sleep.", e);
            // well if something goes wrong here, let's just ignore it
        }
        return getItem(user, groupId, item.getKey());
    }
    
    @Override
    public Item createItem(IUser user, Item item, String groupId, List<String> ignoreFields) throws ZoteroConnectionException, ZoteroItemCreationFailedException {
        Zotero zotero = getApi(user);
        ItemCreationResponse response = zotero.getGroupsOperations().createItem(groupId, item, ignoreFields);
        
        // let's give Zotero a minute to process
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            logger.error("Could not sleep.", e);
            // well if something goes wrong here, let's just ignore it
        }
        
        Map<String, String> success = response.getSuccess();
        if (success.isEmpty()) {
            throw new ZoteroItemCreationFailedException(response.getFailed().toString());
        }
        
        // since we only submitted one item, there should only be one in the map
        return getItem(user, groupId, success.values().iterator().next());
    }
    
    @Override
    public long getItemVersion(IUser user, String groupId, String itemKey) {
        Zotero zotero = getApi(user);
        return zotero.getGroupsOperations().getGroupItemVersion(groupId, itemKey);
    }
    
    @Override
    @Cacheable(value="itemTypeFields", key="#itemType")
    public FieldInfo[] getFields(IUser user, String itemType) {
        return getApi(user).getItemTypesOperations().getFields(itemType);
    }
    
    private Zotero getApi(IUser user) {
        IZoteroToken token = tokenManager.getToken(user);
        Zotero zotero = zoteroFactory.createConnection(new OAuthToken(token.getToken(), token.getSecret())).getApi();
        zotero.setUserId(token.getUserId());
        return zotero;
    }
}
