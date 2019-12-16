package edu.asu.diging.citesphere.core.zotero;

import edu.asu.diging.citesphere.core.model.IZoteroToken;
import edu.asu.diging.citesphere.model.IUser;

public interface IZoteroTokenManager {

    IZoteroToken getToken(IUser user);

    IZoteroToken store(IZoteroToken token);

}