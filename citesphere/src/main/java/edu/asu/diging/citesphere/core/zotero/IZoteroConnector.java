package edu.asu.diging.citesphere.core.zotero;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.social.zotero.api.Group;
import org.springframework.social.zotero.api.Item;

import edu.asu.diging.citesphere.core.model.IUser;

public interface IZoteroConnector {

    Item[] getGroupItems(IUser user, String groupId, int page);

    Group[] getGroups(IUser user);

}