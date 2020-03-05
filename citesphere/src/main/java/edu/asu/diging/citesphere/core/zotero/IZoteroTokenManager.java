package edu.asu.diging.citesphere.core.zotero;

import edu.asu.diging.citesphere.core.model.IZoteroToken;
import edu.asu.diging.citesphere.user.IUser;

public interface IZoteroTokenManager {

    IZoteroToken getToken(IUser user);

    IZoteroToken store(IZoteroToken token);

}