package edu.asu.diging.citesphere.core.zotero;

import org.springframework.social.zotero.api.Group;
import org.springframework.social.zotero.api.Item;
import org.springframework.social.zotero.api.ZoteroResponse;

import edu.asu.diging.citesphere.core.model.IUser;

public interface IZoteroConnector {

    ZoteroResponse<Item> getGroupItems(IUser user, String groupId, int page);

    Group[] getGroups(IUser user);

}