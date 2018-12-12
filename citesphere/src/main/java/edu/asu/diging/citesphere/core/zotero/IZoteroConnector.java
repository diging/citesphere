package edu.asu.diging.citesphere.core.zotero;

import org.springframework.social.zotero.api.Group;
import org.springframework.social.zotero.api.Item;
import org.springframework.social.zotero.api.ZoteroResponse;

import edu.asu.diging.citesphere.core.model.IUser;

public interface IZoteroConnector {

    ZoteroResponse<Item> getGroupItems(IUser user, String groupId, int page);

    ZoteroResponse<Group> getGroups(IUser user);

    Item getItem(IUser user, String groupId, String itemKey);

    ZoteroResponse<Group> getGroupsVersions(IUser user);

    ZoteroResponse<Item> getGroupItemsWithLimit(IUser user, String groupId, int limit);

    Group getGroup(IUser user, String groupId, boolean forceRefresh);

}