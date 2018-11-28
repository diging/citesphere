package edu.asu.diging.citesphere.core.service;

import org.springframework.social.zotero.api.Group;

import edu.asu.diging.citesphere.core.model.IUser;

public interface IZoteroManager {

    Group[] getGroupItems(IUser user);

}