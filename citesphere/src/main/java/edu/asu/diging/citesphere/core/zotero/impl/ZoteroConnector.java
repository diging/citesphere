package edu.asu.diging.citesphere.core.zotero.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.social.oauth1.OAuthToken;
import org.springframework.social.zotero.api.Collection;
import org.springframework.social.zotero.api.CreatorType;
import org.springframework.social.zotero.api.DeletedElements;
import org.springframework.social.zotero.api.FieldInfo;
import org.springframework.social.zotero.api.Group;
import org.springframework.social.zotero.api.Item;
import org.springframework.social.zotero.api.ItemCreationResponse;
import org.springframework.social.zotero.api.ItemCreationResponse.FailedMessage;
import org.springframework.social.zotero.api.Zotero;
import org.springframework.social.zotero.api.ZoteroResponse;
import org.springframework.social.zotero.connect.ZoteroConnectionFactory;
import org.springframework.social.zotero.exception.ZoteroConnectionException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import edu.asu.diging.citesphere.core.exceptions.AccessForbiddenException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroHttpStatusException;
import edu.asu.diging.citesphere.core.exceptions.ZoteroItemCreationFailedException;
import edu.asu.diging.citesphere.core.model.IZoteroToken;
import edu.asu.diging.citesphere.core.zotero.IZoteroConnector;
import edu.asu.diging.citesphere.core.zotero.ZoteroUpdateItemsResponse;
import edu.asu.diging.citesphere.user.IUser;

@Component
@PropertySource("classpath:/config.properties")
public class ZoteroConnector implements IZoteroConnector {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${_zotero_page_size}")
    private Integer zoteroPageSize;

    @Value("${_zotero_collections_max_number}")
    private Integer zoteroCollectionsMaxNumber;

    @Autowired
    private ZoteroTokenManager tokenManager;

    @Autowired
    private ZoteroConnectionFactory zoteroFactory;

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.diging.citesphere.core.service.impl.IZoteroConnector#getGroupItems(
     * edu.asu.diging.citesphere.core.model.IUser, java.lang.String, int)
     */
    @Override
    @Cacheable(value = "groupItems", key = "#user.username + '_' + #groupId + '_' + #page + '_' + #sortBy + '_' + #lastGroupVersion")
    public ZoteroResponse<Item> getGroupItems(IUser user, String groupId, int page, String sortBy,
            Long lastGroupVersion) throws ZoteroHttpStatusException {
        Zotero zotero = getApi(user);
        if (page < 1) {
            page = 0;
        } else {
            page = page - 1;
        }
        try {
            return zotero.getGroupsOperations().getGroupItemsTop(groupId, page * zoteroPageSize, zoteroPageSize, sortBy,
                    lastGroupVersion);
        } catch (HttpClientErrorException ex) {
            throw createException(ex.getStatusCode(), ex);
        }
    }
    
    @Override
    public ZoteroResponse<Item> getGroupItemsVersions(IUser user, String groupId, long version, boolean includeTrashed) {
        Zotero zotero = getApi(user);
        return zotero.getGroupsOperations().getGroupItemsVersions(groupId, version, includeTrashed);
    }
    
    @Override
    public ZoteroResponse<Item> getGroupItemsByKey(IUser user, String groupId, List<String> keys, boolean includeTrashed) {
        Zotero zotero = getApi(user);
        return zotero.getGroupsOperations().getGroupItemsByKey(groupId, keys, includeTrashed);
    }
    
    @Override
    public DeletedElements getDeletedElements(IUser user, String groupId, long version) {
        Zotero zotero = getApi(user);
        return zotero.getGroupsOperations().getDeletedElements(groupId, version);
    }
    
    @Override
    public boolean isGroupModified(IUser user, String groupId, Long lastGroupVersion) throws ZoteroHttpStatusException {
        Zotero zotero = getApi(user);
        try {
            ZoteroResponse<Item> response = zotero.getGroupsOperations().getGroupItemsTop(groupId, 1, 1, "title", lastGroupVersion);
            return !response.getNotModified() || response.getLastVersion() != lastGroupVersion;
        } catch (HttpClientErrorException ex) {
            throw createException(ex.getStatusCode(), ex);
        }
    }

    @Override
    @CacheEvict(value = "groupItems", key = "#user.username + '_' + #groupId + '_' + #page + '_' + #sortBy + '_' + #lastGroupVersion")
    public void clearGroupItemsCache(IUser user, String groupId, int page, String sortBy, Long lastGroupVersion) {
    }

    @Override
    public ZoteroResponse<Item> getGroupItemsWithLimit(IUser user, String groupId, int limit, String sortBy,
            Long lastGroupVersion) {
        Zotero zotero = getApi(user);
        if (limit < 1) {
            limit = 1;
        }
        return zotero.getGroupsOperations().getGroupItemsTop(groupId, 0, 1, sortBy, lastGroupVersion);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.diging.citesphere.core.service.impl.IZoteroConnector#getGroups(edu.
     * asu.diging.citesphere.core.model.IUser)
     */
    @Override
    @Cacheable(value = "groups", key = "#user.username")
    public ZoteroResponse<Group> getGroups(IUser user) {
        Zotero zotero = getApi(user);
        return zotero.getGroupsOperations().getGroups();
    }

    @Override
    @Cacheable(value = "groupCache", key = "#user.username + '_' + #groupId", condition = "#forceRefresh==false")
    public Group getGroup(IUser user, String groupId, boolean forceRefresh) {
        Zotero zotero = getApi(user);
        return zotero.getGroupsOperations().getGroup(groupId);
    }

    @Override
    // @Cacheable(value="groupVersions", key="#user.username")
    public ZoteroResponse<Group> getGroupsVersions(IUser user) {
        Zotero zotero = getApi(user);
        return zotero.getGroupsOperations().getGroupsVersions();
    }

    @Override
    public Item getItem(IUser user, String groupId, String itemKey) throws ZoteroHttpStatusException {
        Zotero zotero = getApi(user);
        try  {
            return zotero.getGroupsOperations().getGroupItem(groupId, itemKey);
        } catch(HttpClientErrorException ex) {
            throw createException(ex.getStatusCode(), ex);
        }
    }

    @Override
    public Item updateItem(IUser user, Item item, String groupId, List<String> collectionIds, List<String> ignoreFields,
            List<String> validCreatorTypes) throws ZoteroConnectionException, ZoteroHttpStatusException {
        Zotero zotero = getApi(user);
        zotero.getGroupsOperations().updateItem(groupId, item, ignoreFields, validCreatorTypes);
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
    
    /**
     * This method makes a call to Zotero to batch update items and return back
     * response
     * 
     * @param groupId      group id of citations
     * @param items        List of items to be updated
     * @param ignoreFields Fields that are not necessary to be updated
     * 
     * @return ZoteroUpdateItemsResponse returns statuses of items.
     */
    @Override
    public ZoteroUpdateItemsResponse updateItems(IUser user, List<Item> items, String groupId,
            List<List<String>> ignoreFieldsList, List<List<String>> validCreatorTypesList) throws ZoteroConnectionException, ZoteroHttpStatusException {
        Zotero zotero = getApi(user);
        ItemCreationResponse response = zotero.getGroupsOperations().batchUpdateItems(groupId, items, ignoreFieldsList, validCreatorTypesList);
        ZoteroUpdateItemsResponse statuses = new ZoteroUpdateItemsResponse();

        Function<Map.Entry<String, String>, String> itemKeyExtractor = e -> e.getValue();
        Function<Map.Entry<String, FailedMessage>, String> failedItemKeyExtractor = e -> e.getValue().getKey();

        statuses.setSuccessItems(extractItemKeys(response.getSuccess(), itemKeyExtractor));
        statuses.setUnchagedItems(extractItemKeys(response.getUnchanged(), itemKeyExtractor));
        statuses.setFailedItems(extractItemKeys(response.getFailed(), failedItemKeyExtractor));

        return statuses;
    }
    
    @Override
    public Item createItem(IUser user, Item item, String groupId, List<String> collectionIds, List<String> ignoreFields,
            List<String> validCreatorTypes) throws ZoteroConnectionException, ZoteroItemCreationFailedException, ZoteroHttpStatusException {
        Zotero zotero = getApi(user);
        ItemCreationResponse response = zotero.getGroupsOperations().createItem(groupId, item, ignoreFields,
                validCreatorTypes);

        // let's give Zotero a minute to process
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            logger.error("Could not sleep.", e);
            // well if something goes wrong here, let's just ignore it
        }

        Map<String, String> success = response.getSuccess();
        if (success.isEmpty()) {
            logger.error("Could not create item: " + response.getFailed().get("0"));
            throw new ZoteroItemCreationFailedException(response);
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
    @Cacheable(value = "itemTypeFields", key = "#itemType")
    public FieldInfo[] getFields(IUser user, String itemType) {
        return getApi(user).getItemTypesOperations().getFields(itemType);
    }

    @Override
    @Cacheable(value = "itemTypeCreatorTypes", key = "#itemType")
    public CreatorType[] getItemTypeCreatorTypes(IUser user, String itemType) {
        return getApi(user).getItemTypesOperations().getCreatorTypes(itemType);
    }

    @Override
    @Cacheable(value = "singleCollections", key = "#user.username + '_' + #collectionId + '_' + #groupId")
    public Collection getCitationCollection(IUser user, String groupId, String collectionId) {
        return getApi(user).getGroupCollectionsOperations().getCollection(groupId, collectionId);
    }

    @Override
    @Cacheable(value = "citationCollections", key = "#user.username + '_' + #collectionId + '_' + #groupId + '_' + #page + '_' + #sortBy + '_' + #lastGroupVersion")
    public ZoteroResponse<Collection> getCitationCollections(IUser user, String groupId, String collectionId, int page,
            String sortBy, Long lastGroupVersion) {
        if (page < 1) {
            page = 0;
        } else {
            page = page - 1;
        }
        if (collectionId == null || collectionId.trim().isEmpty()) {
            return getApi(user).getGroupCollectionsOperations().getTopCollections(groupId,
                    page * zoteroCollectionsMaxNumber, zoteroCollectionsMaxNumber, sortBy, lastGroupVersion);
        }
        return getApi(user).getGroupCollectionsOperations().getCollections(groupId, collectionId,
                page * zoteroCollectionsMaxNumber, zoteroCollectionsMaxNumber, sortBy, lastGroupVersion);
    }
    
    @Override
    public ZoteroResponse<Collection> getCitationCollectionVersions(IUser user, String groupId, Long lastGroupVersion) {
        return getApi(user).getGroupCollectionsOperations().getCollectionsVersions(groupId, lastGroupVersion);
    }
    
    @Override
    public ZoteroResponse<Collection> getCitationCollectionsByKey(IUser user, String groupId, List<String> keys) {
        return getApi(user).getGroupCollectionsOperations().getCollectionsByKey(groupId, keys);
    }

    @Override
    @Cacheable(value = "collectionItems", key = "#user.username + '_' + #groupId + '_' + #collectionId + '_' + #page + '_' + #sortBy + '_' + #lastGroupVersion")
    public ZoteroResponse<Item> getCollectionItems(IUser user, String groupId, String collectionId, int page,
            String sortBy, Long lastGroupVersion) throws ZoteroHttpStatusException {
        Zotero zotero = getApi(user);
        if (page < 1) {
            page = 0;
        } else {
            page = page - 1;
        }
        try {
        return zotero.getGroupCollectionsOperations().getItems(groupId, collectionId, page * zoteroPageSize,
                zoteroPageSize, sortBy, lastGroupVersion);
        } catch(HttpClientErrorException ex) {
            throw createException(ex.getStatusCode(), ex);
        }
    }

    @Override
    @CacheEvict(value = "collectionItems", key = "#user.username + '_' + #groupId + '_' + #collectionId + '_' + #page + '_' + #sortBy + '_' + #lastGroupVersion")
    public void clearCollectionItemsCache(IUser user, String groupId, String collectionId, int page, String sortBy,
            Long lastGroupVersion) {
    }

    private Zotero getApi(IUser user) {
        IZoteroToken token = tokenManager.getToken(user);
        Zotero zotero = zoteroFactory.createConnection(new OAuthToken(token.getToken(), token.getSecret())).getApi();
        zotero.setUserId(token.getUserId());
        return zotero;
    }
    
    private ZoteroHttpStatusException createException(HttpStatus status, Exception cause) {
        if (status == HttpStatus.FORBIDDEN) {
            return new AccessForbiddenException(cause);
        }
        return new ZoteroHttpStatusException(cause);
    }
    
    private <T> List<String> extractItemKeys(Map<String, T> map, Function<Map.Entry<String, T>, String> keyExtractor) {
        return map.entrySet().stream().map(e -> keyExtractor.apply(e)).collect(Collectors.toList());
    }

}
